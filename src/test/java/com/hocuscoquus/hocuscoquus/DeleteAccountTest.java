package com.hocuscoquus.hocuscoquus;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.hocuscoquus.hocuscoquus.entity.Account;
import com.hocuscoquus.hocuscoquus.repository.AccountRepository;
import com.hocuscoquus.hocuscoquus.service.AccountService;

@SpringBootTest
@Transactional
public class DeleteAccountTest {
    @Autowired
    AccountService accountService;

    @Autowired
    AccountRepository accountRepository;

    @Test
    void deleteAccount() {
        // Create test account
        String email = "test" + UUID.randomUUID() + "@test.com";
        String password = "password";
        Account created = accountService.createAccount("Test", email, password);
        long id = created.getAccountId();

        // Confirm account creation
        assertNotNull(accountRepository.findById(id));

        // Delete account
        accountService.deleteAccount(id, email, password);

        // Confirm account deletion
        assertNull(accountRepository.findById(id));
        assertNull(accountRepository.findByEmail(email));
    }
}