package com.hocuscoquus.hocuscoquus.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hocuscoquus.hocuscoquus.entity.Account;
import com.hocuscoquus.hocuscoquus.entity.AccountDetails;
import com.hocuscoquus.hocuscoquus.entity.DietaryRestriction;
import com.hocuscoquus.hocuscoquus.entity.Ingredient;
import com.hocuscoquus.hocuscoquus.model.AccountUpdateRequest;
import com.hocuscoquus.hocuscoquus.repository.AccountRepository;
import com.hocuscoquus.hocuscoquus.repository.DietaryRestrictionRepository;
import com.hocuscoquus.hocuscoquus.repository.IngredientRepository;

@Service
public class AccountService implements UserDetailsService {
    private final PasswordEncoder passwordEncoder;
    
    private final AccountRepository accountRepository;
    private final IngredientRepository ingredientRepository;
    private final DietaryRestrictionRepository dietaryRestrictionRepository;

    public AccountService(
        AccountRepository accountRepository,
        PasswordEncoder passwordEncoder,
        IngredientRepository ingredientRepository,
        DietaryRestrictionRepository dietaryRestrictionRepository) {

        this.accountRepository = accountRepository;
        this.passwordEncoder = passwordEncoder;
        this.ingredientRepository = ingredientRepository;
        this.dietaryRestrictionRepository = dietaryRestrictionRepository;
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
            throw new IllegalArgumentException("Account not found for ID: " + accountId);
        }

        // Confirm requesting account is the same as account to delete
        if (!email.equals(acc.getEmail())) {
            throw new IllegalArgumentException(
                    "Account " + email + " attempting to delete account with email: " + acc.getEmail());
        }

        // Confirm password match
        String stored_password = accountRepository.findById(accountId).getPassword();
        if (!this.passwordEncoder.matches(password, stored_password)) {
            throw new IllegalArgumentException("Invalid password for ID: " + accountId);
        }

        // Delete the account
        this.accountRepository.deleteById(accountId);
    }

    @Transactional
    public boolean updateAccount(long accountId, AccountUpdateRequest request) {
        // Validate request and account
        if (request == null) {
            return false;
        }

        Account account = accountRepository.findById(accountId);
        if (account == null) {
            return false;
        }

        // Validate password change
        String newPassword = request.newPassword();
        boolean changePassword = newPassword != null && !newPassword.isEmpty();

        if (changePassword && (newPassword.isBlank()
                || request.currentPassword() == null
                || !passwordEncoder.matches(request.currentPassword(), account.getPassword()))) {
            return false;
        }

        // Validate ingredients
        List<Ingredient> ingredients = null;

        if (request.ingredientIds() != null) {
            Set<Long> ids = new HashSet<>();

            for (Long id : request.ingredientIds()) {
                if (id == null || id <= 0 || !ids.add(id)) {
                    return false;
                }
            }

            ingredients = ingredientRepository.findAllById(ids);

            if (ingredients.size() != ids.size()) {
                return false;
            }
        }

        // Validate dietary restrictions
        List<String> restrictions = null;

        if (request.dietaryRestrictionNames() != null) {
            restrictions = new ArrayList<>();
            Set<String> seen = new HashSet<>();

            for (String restriction : request.dietaryRestrictionNames()) {
                if (restriction == null || restriction.isBlank()) {
                    return false;
                }

                String name = restriction.trim();

                if (name.length() > 255 || !seen.add(name.toLowerCase())) {
                    return false;
                }

                restrictions.add(name);
            }
        }

        // Apply changes after validation
        if (changePassword) {
            account.setPassword(passwordEncoder.encode(newPassword));
        }

        if (ingredients != null) {
            updateIngredients(account, ingredients);
        }

        if (restrictions != null) {
            updateDietaryRestrictions(account, restrictions);
        }

        accountRepository.save(account);
        return true;
    }

    private void updateIngredients(Account account, List<Ingredient> ingredients) {
        long accountId = account.getAccountId();

        Set<Long> requestedIds = new HashSet<>();
        for (Ingredient ingredient : ingredients) {
            requestedIds.add(ingredient.getIngredientId());
        }

        // Remove unselected associations
        for (Ingredient ingredient : ingredientRepository.findByAccounts_AccountId(accountId)) {
            if (!requestedIds.contains(ingredient.getIngredientId())) {
                ingredient.getAccounts().removeIf(
                        a -> a.getAccountId() == accountId);
            }
        }

        // Add selected associations
        for (Ingredient ingredient : ingredients) {
            boolean exists = ingredient.getAccounts().stream()
                    .anyMatch(a -> a.getAccountId() == accountId);

            if (!exists) {
                ingredient.getAccounts().add(account);
            }
        }

        // Keep inverse relationship synchronized
        account.getIngredients().clear();
        account.getIngredients().addAll(ingredients);
    }

    private void updateDietaryRestrictions(Account account, List<String> names) {
        long accountId = account.getAccountId();

        List<DietaryRestriction> current =
                dietaryRestrictionRepository.findByAccount_AccountId(accountId);

        Set<String> requestedNames = new HashSet<>();
        for (String name : names) {
            requestedNames.add(name.toLowerCase());
        }

        // Remove unselected restrictions
        for (DietaryRestriction restriction : current) {
            String name = restriction.getDietaryRestrictionName()
                    .trim().toLowerCase();

            if (!requestedNames.contains(name)) {
                // Remove owning-side ingredient associations
                for (Ingredient ingredient : restriction.getIngredients()) {
                    ingredient.getDietaryRestriction().removeIf(
                            r -> r.getDietaryRestrictionId()
                                    == restriction.getDietaryRestrictionId());
                }

                account.getDietaryRestrictions().removeIf(
                        r -> r.getDietaryRestrictionId()
                                == restriction.getDietaryRestrictionId());

                dietaryRestrictionRepository.delete(restriction);
            }
        }

        // Create newly selected restrictions
        for (String name : names) {
            boolean exists = current.stream()
                    .anyMatch(r -> r.getDietaryRestrictionName()
                            .trim().equalsIgnoreCase(name));

            if (!exists) {
                DietaryRestriction restriction = new DietaryRestriction();
                restriction.setDietaryRestrictionName(name);
                restriction.setAccount(account);

                DietaryRestriction saved =
                        dietaryRestrictionRepository.save(restriction);

                account.getDietaryRestrictions().add(saved);
            }
        }
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Account account = accountRepository.findByEmail(email);
        if (account == null) {
            throw new UsernameNotFoundException("Account not found for email: " + email);
        }

        return new AccountDetails(account);
    }

    public Account getAccountById(long accountId) {
        Account account = accountRepository.getAccountByAccountId(accountId);
        if (account == null) {
            throw new UsernameNotFoundException("Account not found for id: " + accountId);
        }

        return account;
    }
}
