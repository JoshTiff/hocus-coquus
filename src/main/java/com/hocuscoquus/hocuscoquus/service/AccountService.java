package com.hocuscoquus.hocuscoquus.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.hocuscoquus.hocuscoquus.entity.Account;
import com.hocuscoquus.hocuscoquus.entity.AccountDetails;
import com.hocuscoquus.hocuscoquus.repository.AccountRepository;

@Service
public class AccountService implements UserDetailsService {
    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;

    public AccountService(AccountRepository accountRepository, PasswordEncoder passwordEncoder) {
        this.accountRepository = accountRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Account createAccount(String firstName, String email, String password) {
        if (firstName == null || firstName.isBlank()
                || email == null || email.isBlank()
                || password == null || password.isBlank()) {
            throw new IllegalArgumentException("First name, email, and password are required.");
        }

        String normalizedEmail = email.trim();
        if (accountRepository.existsByEmail(normalizedEmail)) {
            throw new IllegalArgumentException("An account with this email already exists.");
        }

        Account account = new Account();
        account.setFirstName(firstName.trim());
        account.setEmail(normalizedEmail);
        account.setPassword(passwordEncoder.encode(password));
        return accountRepository.save(account);
    }

    public void deleteAccount(long accountId, String email, String password) {
        // Confirm account exists
        Account acc = accountRepository.findById(accountId);
        if (acc == null) {
            throw new IllegalArgumentException("Account not found: " + accountId);
        }

        // TODO - Confirm requesting account is the same as account to delete

        // Confirm password match
        String pwd = accountRepository.findById(accountId).getPassword();
        if (!pwd.equals(password)) {
            throw new IllegalArgumentException("Invalid password: " + accountId);
        }

        // Delete the account
        this.accountRepository.deleteById(accountId);
    }

    public Account updateAccount(Account account) {
        // TODO: add implementation for this method
        return new Account();
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Account account = accountRepository.findByEmail(email);
        if (account == null) {
            throw new UsernameNotFoundException("Account not found for email: " + email);
        }

        return new AccountDetails(account);
    }
}
