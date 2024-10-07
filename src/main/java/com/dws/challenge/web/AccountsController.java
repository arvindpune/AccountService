package com.dws.challenge.web;

import com.dws.challenge.domain.Account;
import com.dws.challenge.domain.BalanceTransferBtwAccount;
import com.dws.challenge.exception.AccountIdNotFound;
import com.dws.challenge.exception.DuplicateAccountIdException;
import com.dws.challenge.service.AccountsService;
import com.dws.challenge.service.BalanceTransferService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/v1/accounts")
@Slf4j
public class AccountsController {

    private final AccountsService accountsService;

    @Autowired
    public AccountsController(AccountsService accountsService) {
        this.accountsService = accountsService;
    }

    @Autowired
    BalanceTransferService balanceTransferService;

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> createAccount(@RequestBody @Valid Account account) {
        log.info("Creating account {}", account);

        try {
            this.accountsService.createAccount(account);
        } catch (DuplicateAccountIdException daie) {
            return new ResponseEntity<>(daie.getMessage(), HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @GetMapping(path = "/{accountId}")
    public Account getAccount(@PathVariable String accountId) {
        log.info("Retrieving account for id {}", accountId);
        return this.accountsService.getAccount(accountId);
    }

    @PostMapping(path = "/transferBalance", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> transferBalance(@RequestBody @Valid BalanceTransferBtwAccount balanceTransfer) {
      log.info("inside transferBalance {} ",balanceTransfer);
        try {
            balanceTransferService.transferBalance(balanceTransfer.getAccountToId(), balanceTransfer.getAccountFromId(), balanceTransfer.getAmount());
        } catch (AccountIdNotFound | ExecutionException | InterruptedException e) {
            return new ResponseEntity<>(new AccountIdNotFound("Account Id not found"), HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
