package com.dws.challenge;

import com.dws.challenge.domain.Account;

import com.dws.challenge.exception.AccountIdNotFound;
import com.dws.challenge.repository.AccountsRepositoryInMemory;
import com.dws.challenge.service.AccountsService;
import com.dws.challenge.service.BalanceTransferService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;


import java.math.BigDecimal;
import java.util.List;

@ExtendWith(MockitoExtension.class)
class DemoApplicationTests {

    @Mock
    AccountsRepositoryInMemory accountsRepository;

    @Mock
    AccountsService accountsService;

    @Mock
    com.dws.challenge.service.NotificationService NotificationService;
    @InjectMocks
    private BalanceTransferService balanceTransferService;


    @Test
    public void testDepositAmount() {
        Result accountObj = getAccountObjects();
        List<Account> accountList = balanceTransferService.depositAmount(accountObj.accountTo(), accountObj.accountFrom(), new BigDecimal("100"));
        Assertions.assertEquals(accountList.get(0).getBalance(), new BigDecimal("333556.222"));
    }

    @Test
    public void testTransfer() throws Exception {
        Result accountObj = getAccountObjects();
        List<Account> accountList =  balanceTransferService.callDepositAmount(accountObj.accountTo, accountObj.accountFrom, new BigDecimal("100"));
        Assertions.assertEquals(accountList.get(0).getBalance(), new BigDecimal("333556.222"));
    }



    @Test()
    public void testNegativeTransfer() throws Exception {
        Result accountObj = getAccountObjects();
        String exceptionResult = null;
        List<Account> accountList = null;
        try {
            accountList = balanceTransferService.callDepositAmount(accountObj.accountTo, accountObj.accountFrom, new BigDecimal("224.00"));
        } catch (Exception e) {
            exceptionResult = e.getMessage();
        }
        Assertions.assertNotNull(exceptionResult);
    }
    private record Result(Account accountTo, Account accountFrom) {
    }
    private static Result getAccountObjects() {
        Account accountTo = new Account("1234", new BigDecimal("333456.222"));
        Account accountFrom = new Account("333", new BigDecimal("222.222"));
        return new Result(accountTo, accountFrom);
    }
}
