package com.hocuscoquus.hocuscoquus.handler;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.hocuscoquus.hocuscoquus.model.AccountUpdateRequest;
import com.hocuscoquus.hocuscoquus.service.AccountService;

@Service
public class AccountHandler {

    private final AccountService accountService;

    public AccountHandler(AccountService accountService) {
        this.accountService = accountService;
    }

    public ResponseEntity<Void> processAccountUpdateRequest(
            long accountId, AccountUpdateRequest request) {

        boolean updated = accountService.updateAccount(accountId, request);

        if (!updated) {
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.noContent().build();
    }
}