package com.hocuscoquus.hocuscoquus;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
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

    @Test
    void createAccount() {
        String email = "test" + UUID.randomUUID() + "@test.com";
        String password = "password";
        Account created = accountService.createAccount("Test", email, password);

        // Confirm account creation
        assertNotNull(accountRepository.findById(created.getAccountId()));
    }
}
