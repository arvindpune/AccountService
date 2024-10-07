package com.dws.challenge.service;

import com.dws.challenge.domain.Account;
import com.dws.challenge.exception.AccountIdNotFound;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicReference;
@Slf4j
@Service
public class BalanceTransferService {

    @Autowired
    AccountsService accountsService;

    @Autowired
    NotificationService notificationService;

    private static final Object lock = new Object();


    public void transferBalance(String accountIdTo, String accountIdFrom, BigDecimal amount) throws ExecutionException, InterruptedException {
       log.info("inside transferAmount {}  from {}  amount {}",accountIdTo , accountIdFrom,amount);
        Result accountObj = getCombinedAccountObj(accountIdTo);
        if (null != accountObj && null != accountObj.accountTo() && null != accountObj.accountFrom) {
            Optional<List<Account>> updatedAccounts = Optional.ofNullable(callDepositAmount(accountObj.accountTo(), accountObj.accountFrom(), amount));
            updatedAccounts.ifPresent(accounts -> accounts.forEach(account -> {
                accountsService.updateAccount(account);
            }));
        } else {
            throw new AccountIdNotFound("account Id not Found");
        }
    }

    private Result getCombinedAccountObj(String accountIdTo) {
        try {
            Account accountTo = getAccountById(accountIdTo);
            Account accountFrom = getAccountById(accountIdTo);
            return new Result(accountTo, accountFrom);
        } catch (Exception e) {
            throw new AccountIdNotFound("account Id not Found");
        }

    }

    private record Result(Account accountTo, Account accountFrom) {
    }

    public Account getAccountById(String accountIdTo) {
        return accountsService.getAccount(accountIdTo);
    }

    public List<Account> callDepositAmount(Account accountTo, Account accountFrom, BigDecimal amount) throws ExecutionException, InterruptedException {
        AtomicReference<List<Account>> accList = new AtomicReference<>(new ArrayList<>());
        CompletableFuture.runAsync(() -> {
            accList.set(depositAmount(accountTo, accountFrom, amount));
        }).get();
        return accList.get();
    }


    public List<Account> depositAmount(Account accountTo, Account accountFrom, BigDecimal amount) {
        List<Account> accList = new ArrayList<>();
        log.info("inside depositAmount" + "accountIdTo " + accountTo.getAccountId() + " accountIdFrom::  " + accountFrom.getAccountId() + " amount:: " + amount);
            synchronized (lock) {
                if ((amount.intValue() > accountTo.getBalance().intValue()) || (amount.intValue() > accountFrom.getBalance().intValue())) {
                    throw new AccountIdNotFound(
                            "Transfer cannot be completed due to low balance");
                }
                accountTo.setBalance(accountTo.getBalance().add(amount));
                accountFrom.setBalance(accountFrom.getBalance().subtract(amount));
                sendNotification(accountTo, accountFrom, amount);
                accList.add(0, accountTo);
                accList.add(1, accountFrom);
            }
        return accList;
    }

    public void sendNotification(Account accountTo, Account accountFrom, BigDecimal amount) {
        notificationService.notifyAboutTransfer(accountTo, amount + " amount received from accountId" + accountFrom.getAccountId());
        notificationService.notifyAboutTransfer(accountFrom, amount + " amount transfer to  accountId " + accountTo.getAccountId());
    }
}
