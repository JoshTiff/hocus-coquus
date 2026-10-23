package com.hocuscoquus.hocuscoquus.service;

import java.util.Objects;

import com.hocuscoquus.hocuscoquus.model.Account;
import com.hocuscoquus.hocuscoquus.repository.AccountRepository;

public class AccountService {
    private final AccountRepository accountRepository;

    // TODO: review/add constructor(s)
    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = Objects.requireNonNull(accountRepository);
    }

    public Account createAccount() {
        // TODO: add implementation for this method
        return new Account();
    }

    public void deleteAccount(long accountId) {
        // TODO: add implementation for this method
    }

    public Account updateAccount(Account account) {
        // TODO: add implementation for this method
        return new Account();
    }

    public Account login(String email) {
        
        // TODO: add implementation for this method

        return accountRepository.findByEmail(email);
    }
}
