package com.dws.challenge.service;

import com.dws.challenge.domain.Account;
import com.dws.challenge.repository.AccountsRepository;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Getter
@Service
public class AccountsService {

  private final AccountsRepository accountsRepository;

  @Autowired
  public AccountsService(AccountsRepository accountsRepository) {
    this.accountsRepository = accountsRepository;
  }

  public void createAccount(Account account) {
    this.accountsRepository.createAccount(account);
  }

  public Account getAccount(String accountId) {
    Account accountEntity =  (Account)this.accountsRepository.getAccountById(accountId);
    return new Account(accountEntity.getAccountId(), accountEntity.getBalance());
  }

  public void updateAccount(Account account) {
    this.accountsRepository.updateAccount(account);
  }
}