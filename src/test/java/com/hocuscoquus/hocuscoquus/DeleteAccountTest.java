package com.hocuscoquus.hocuscoquus;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import com.hocuscoquus.hocuscoquus.entity.AccountDetails;

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

        // Confirm that account has been created and is not archived
        Account before = accountRepository.findById(id);
        assertNotNull(before);
        assertFalse(before.isArchived());

        // Archive account
        accountService.deleteAccount(id, email, password);

        // Confirm record exists
        Account after = accountRepository.findById(id);
        assertNotNull(after);

        // Confirm record is archived
        assertTrue(after.isArchived());
        assertNotNull(accountRepository.findByEmail(email));

        // Confirm the user can no longer log in
        assertFalse(new AccountDetails(after).isEnabled());
    }
}