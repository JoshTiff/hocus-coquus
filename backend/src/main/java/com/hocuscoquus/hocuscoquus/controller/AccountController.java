package com.hocuscoquus.hocuscoquus.controller;

import com.hocuscoquus.hocuscoquus.model.Account;
import com.hocuscoquus.hocuscoquus.service.AccountService;

public class AccountController {
    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    public Account createAccount() {
        return accountService.createAccount();
    }

    public void deleteAccount(long accountId) {
        accountService.deleteAccount(accountId);
    }

    public Account updateAccount(Account account) {
        return accountService.updateAccount(account);
    }

    public Account login(String email) {
        return accountService.login(email);
    }
}
