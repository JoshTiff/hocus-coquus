package com.hocuscoquus.hocuscoquus;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import com.hocuscoquus.hocuscoquus.entity.Account;
import com.hocuscoquus.hocuscoquus.entity.DietaryRestriction;
import com.hocuscoquus.hocuscoquus.entity.Ingredient;
import com.hocuscoquus.hocuscoquus.model.AccountUpdateRequest;
import com.hocuscoquus.hocuscoquus.repository.AccountRepository;
import com.hocuscoquus.hocuscoquus.repository.DietaryRestrictionRepository;
import com.hocuscoquus.hocuscoquus.repository.IngredientRepository;
import com.hocuscoquus.hocuscoquus.service.AccountService;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@SpringBootTest(properties = "spring.jpa.hibernate.ddl-auto=validate")
@Transactional
class UpdateAccountIntegrationTest {

    @Autowired
    private AccountService accountService;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private IngredientRepository ingredientRepository;

    @Autowired
    private DietaryRestrictionRepository dietaryRestrictionRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PersistenceContext
    private EntityManager entityManager;

    // Helpers for creating test data

    private Account createAccount() {
        String email = "test-" + UUID.randomUUID() + "@example.com";
        return accountService.createAccount("Test", email, "oldPassword");
    }

    private Ingredient createIngredient() {
        Ingredient ingredient = new Ingredient();
        ingredient.setIngredientName("test-" + UUID.randomUUID());
        return ingredientRepository.save(ingredient);
    }

    private DietaryRestriction createRestriction(Account account, String name) {
        DietaryRestriction restriction = new DietaryRestriction();
        restriction.setDietaryRestrictionName(name);
        restriction.setAccount(account);
        return dietaryRestrictionRepository.save(restriction);
    }

    private void flushAndClear() {
        entityManager.flush();
        entityManager.clear();
    }

    private Set<Long> getAccountIngredientIds(long accountId) {
        return ingredientRepository.findByAccounts_AccountId(accountId)
                .stream()
                .map(Ingredient::getIngredientId)
                .collect(Collectors.toSet());
    }

    private Set<String> getAccountRestrictionNames(long accountId) {
        return dietaryRestrictionRepository.findByAccount_AccountId(accountId)
                .stream()
                .map(DietaryRestriction::getDietaryRestrictionName)
                .collect(Collectors.toSet());
    }

    @Test
    void updateAccount_passwordChangePersists() {
        Account account = createAccount();
        long accountId = account.getAccountId();
        String originalHash = account.getPassword();

        AccountUpdateRequest request = new AccountUpdateRequest(
                "oldPassword", "newPassword", null, null);

        assertTrue(accountService.updateAccount(accountId, request));

        flushAndClear();

        Account persisted = accountRepository.findById(accountId);

        assertNotNull(persisted);
        assertNotEquals(originalHash, persisted.getPassword());
        assertTrue(passwordEncoder.matches(
                "newPassword", persisted.getPassword()));
        assertFalse(passwordEncoder.matches(
                "oldPassword", persisted.getPassword()));
    }

    @Test
    void updateAccount_ingredientChangesPersistWithoutAffectingOtherAccounts() {
        Account account = createAccount();
        Account otherAccount = createAccount();

        Ingredient oldIngredient = createIngredient();
        Ingredient sharedIngredient = createIngredient();
        Ingredient newIngredient = createIngredient();

        // Set up existing relationships
        oldIngredient.getAccounts().add(account);
        sharedIngredient.getAccounts().add(otherAccount);

        ingredientRepository.save(oldIngredient);
        ingredientRepository.save(sharedIngredient);

        long accountId = account.getAccountId();
        long otherAccountId = otherAccount.getAccountId();
        long oldId = oldIngredient.getIngredientId();
        long sharedId = sharedIngredient.getIngredientId();
        long newId = newIngredient.getIngredientId();

        flushAndClear();

        AccountUpdateRequest request = new AccountUpdateRequest(
                null, null, List.of(sharedId, newId), null);

        assertTrue(accountService.updateAccount(accountId, request));

        flushAndClear();

        assertEquals(
                Set.of(sharedId, newId),
                getAccountIngredientIds(accountId));

        // The other account must retain its ingredients
        assertEquals(
                Set.of(sharedId),
                getAccountIngredientIds(otherAccountId));

        // Removing an association must not delete an ingredient
        assertTrue(ingredientRepository.existsById(oldId));
        assertTrue(ingredientRepository.existsById(sharedId));
        assertTrue(ingredientRepository.existsById(newId));
    }

    @Test
    void updateAccount_emptyIngredientListRemovesAllAssociations() {
        Account account = createAccount();
        Ingredient ingredient = createIngredient();

        ingredient.getAccounts().add(account);
        ingredientRepository.save(ingredient);

        long accountId = account.getAccountId();
        long ingredientId = ingredient.getIngredientId();

        flushAndClear();

        AccountUpdateRequest request =
                new AccountUpdateRequest(null, null, List.of(), null);

        assertTrue(accountService.updateAccount(accountId, request));

        flushAndClear();

        assertTrue(getAccountIngredientIds(accountId).isEmpty());
        assertTrue(ingredientRepository.existsById(ingredientId));
    }

    @Test
    void updateAccount_dietaryRestrictionsAreCreatedAndPersisted() {
        Account account = createAccount();
        long accountId = account.getAccountId();

        AccountUpdateRequest request = new AccountUpdateRequest(
                null, null, null,
                List.of(" Vegetarian ", "Dairy-Free"));

        assertTrue(accountService.updateAccount(accountId, request));

        flushAndClear();

        List<DietaryRestriction> persisted =
                dietaryRestrictionRepository.findByAccount_AccountId(accountId);

        assertEquals(2, persisted.size());
        assertEquals(
                Set.of("Vegetarian", "Dairy-Free"),
                getAccountRestrictionNames(accountId));

        for (DietaryRestriction restriction : persisted) {
            assertEquals(accountId, restriction.getAccount().getAccountId());
            assertTrue(restriction.getDietaryRestrictionId() > 0);
        }
    }

    @Test
    void updateAccount_restrictionDeletionPreservesExistingAssociations() {
        Account account = createAccount();

        DietaryRestriction retained =
                createRestriction(account, "Dairy-Free");

        DietaryRestriction removed =
                createRestriction(account, "Low-Sodium");

        Ingredient milk = createIngredient();
        Ingredient salt = createIngredient();

        // Add ingredient associations using the owning side
        milk.getDietaryRestriction().add(retained);
        salt.getDietaryRestriction().add(removed);

        ingredientRepository.save(milk);
        ingredientRepository.save(salt);

        long accountId = account.getAccountId();
        long retainedId = retained.getDietaryRestrictionId();
        long removedId = removed.getDietaryRestrictionId();
        long milkId = milk.getIngredientId();
        long saltId = salt.getIngredientId();

        flushAndClear();

        AccountUpdateRequest request = new AccountUpdateRequest(
                null, null, null, List.of("dairy-free"));

        assertTrue(accountService.updateAccount(accountId, request));

        flushAndClear();

        List<DietaryRestriction> restrictions =
                dietaryRestrictionRepository.findByAccount_AccountId(accountId);

        assertEquals(1, restrictions.size());
        assertEquals(retainedId, restrictions.get(0).getDietaryRestrictionId());

        assertFalse(dietaryRestrictionRepository.existsById(removedId));

        // Existing ingredient associations must be preserved
        Ingredient persistedMilk = ingredientRepository.findById(milkId);

        assertTrue(persistedMilk.getDietaryRestriction().stream()
                .anyMatch(r -> r.getDietaryRestrictionId() == retainedId));

        // Associations to deleted restrictions must be removed
        Ingredient persistedSalt = ingredientRepository.findById(saltId);

        assertFalse(persistedSalt.getDietaryRestriction().stream()
                .anyMatch(r -> r.getDietaryRestrictionId() == removedId));

        // Ingredients themselves must not be deleted
        assertTrue(ingredientRepository.existsById(milkId));
        assertTrue(ingredientRepository.existsById(saltId));
    }

    @Test
    void updateAccount_invalidRequestDoesNotModifyStoredData() {
        Account account = createAccount();
        Ingredient ingredient = createIngredient();

        ingredient.getAccounts().add(account);
        ingredientRepository.save(ingredient);

        createRestriction(account, "Vegetarian");

        long accountId = account.getAccountId();
        long ingredientId = ingredient.getIngredientId();

        flushAndClear();

        // Attempt a password change with invalid restriction names
        AccountUpdateRequest request = new AccountUpdateRequest(
                "oldPassword",
                "newPassword",
                List.of(ingredientId),
                List.of("Vegetarian", " vegetarian "));

        assertFalse(accountService.updateAccount(accountId, request));

        flushAndClear();

        Account persisted = accountRepository.findById(accountId);

        assertTrue(passwordEncoder.matches(
                "oldPassword", persisted.getPassword()));

        assertFalse(passwordEncoder.matches(
                "newPassword", persisted.getPassword()));

        assertEquals(
                Set.of(ingredientId),
                getAccountIngredientIds(accountId));

        assertEquals(
                Set.of("Vegetarian"),
                getAccountRestrictionNames(accountId));
    }

    @Test
    void updateAccount_multipleChangesPersistTogether() {
        Account account = createAccount();
        Ingredient ingredient = createIngredient();

        long accountId = account.getAccountId();
        long ingredientId = ingredient.getIngredientId();

        AccountUpdateRequest request = new AccountUpdateRequest(
                "oldPassword",
                "newPassword",
                List.of(ingredientId),
                List.of("Vegan"));

        assertTrue(accountService.updateAccount(accountId, request));

        flushAndClear();

        Account persisted = accountRepository.findById(accountId);

        assertTrue(passwordEncoder.matches(
                "newPassword", persisted.getPassword()));

        assertEquals(
                Set.of(ingredientId),
                getAccountIngredientIds(accountId));

        assertEquals(
                Set.of("Vegan"),
                getAccountRestrictionNames(accountId));
    }

    @Test
    void updateAccount_omittedCollectionsRemainUnchanged() {
        Account account = createAccount();
        Ingredient ingredient = createIngredient();

        ingredient.getAccounts().add(account);
        ingredientRepository.save(ingredient);

        createRestriction(account, "Gluten-Free");

        long accountId = account.getAccountId();
        long ingredientId = ingredient.getIngredientId();

        flushAndClear();

        AccountUpdateRequest request = new AccountUpdateRequest(
                "oldPassword", "newPassword", null, null);

        assertTrue(accountService.updateAccount(accountId, request));

        flushAndClear();

        assertEquals(
                Set.of(ingredientId),
                getAccountIngredientIds(accountId));

        assertEquals(
                Set.of("Gluten-Free"),
                getAccountRestrictionNames(accountId));

        Account persisted = accountRepository.findById(accountId);
        assertTrue(passwordEncoder.matches(
                "newPassword", persisted.getPassword()));
    }
}
