package com.hocuscoquus.hocuscoquus.service;

import java.util.Objects;

import com.hocuscoquus.hocuscoquus.model.Account;
import com.hocuscoquus.hocuscoquus.repository.AccountRepository;

public class AccountService {
    private final AccountRepository accountRepository;

    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = Objects.requireNonNull(accountRepository);
    }

    public Account createAccount() {
        // return accountRepository.save(Objects.requireNonNull(account));
        return new Account();
    }

    public void deleteAccount(long accountId) {
        // accountRepository.deleteById(accountId);
    }

    public Account updateAccount(Account account) {
        // return accountRepository.save(Objects.requireNonNull(account));
        return new Account();
    }

    public Account login(String email) {
        return accountRepository.findByEmail(email);
    }
}
