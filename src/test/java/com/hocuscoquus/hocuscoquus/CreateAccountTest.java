package com.hocuscoquus.hocuscoquus;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import com.hocuscoquus.hocuscoquus.entity.Account;
import com.hocuscoquus.hocuscoquus.repository.AccountRepository;
import com.hocuscoquus.hocuscoquus.service.AccountService;

@SpringBootTest
@Transactional
public class CreateAccountTest {
    @Autowired
    AccountService accountService;

    @Autowired
    AccountRepository accountRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Test
    void createAccount() {
        // Call createAccount from the account service
        String email = "test" + UUID.randomUUID() + "@test.com";
        String password = "password";
        Account created = accountService.createAccount("Test", email, password);

        // Check that the values inside the new Account object are the same (password too using passwordEncoder)
        assertNotNull(created);
        assertTrue(created.getAccountId() > 0);
        assertEquals("Test", created.getFirstName());
        assertEquals(email, created.getEmail());
        assertTrue(passwordEncoder.matches(password, created.getPassword()));

        // Ensure the account is persisted in the database
        Account persisted = accountRepository.findById(created.getAccountId());
        assertNotNull(persisted);
        assertEquals(created.getAccountId(), persisted.getAccountId());
        assertEquals("Test", persisted.getFirstName());
        assertEquals(email, persisted.getEmail());
        assertTrue(passwordEncoder.matches(password, persisted.getPassword()));
    }
}
