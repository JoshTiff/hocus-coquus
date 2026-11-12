package com.hocuscoquus.hocuscoquus;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.hocuscoquus.hocuscoquus.entity.Account;
import com.hocuscoquus.hocuscoquus.entity.DietaryRestriction;
import com.hocuscoquus.hocuscoquus.entity.Ingredient;
import com.hocuscoquus.hocuscoquus.model.AccountUpdateRequest;
import com.hocuscoquus.hocuscoquus.repository.AccountRepository;
import com.hocuscoquus.hocuscoquus.repository.DietaryRestrictionRepository;
import com.hocuscoquus.hocuscoquus.repository.IngredientRepository;
import com.hocuscoquus.hocuscoquus.service.AccountService;

@ExtendWith(MockitoExtension.class)
class AccountUpdateServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private IngredientRepository ingredientRepository;

    @Mock
    private DietaryRestrictionRepository dietaryRestrictionRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    private AccountService accountService;
    private Account account;

    @BeforeEach
    void setUp() {
        accountService = new AccountService(
                accountRepository,
                passwordEncoder,
                ingredientRepository,
                dietaryRestrictionRepository);

        account = new Account();
        account.setAccountId(1L);
        account.setPassword("storedHash");
    }

    private void mockAccountExists() {
        when(accountRepository.findById(1L)).thenReturn(account);
    }

    @Test
    void updateAccount_nullRequest_returnsFalse() {
        assertFalse(accountService.updateAccount(1L, null));

        verifyNoInteractions(accountRepository);
    }

    @Test
    void updateAccount_accountNotFound_returnsFalse() {
        AccountUpdateRequest request =
                new AccountUpdateRequest(null, null, null, null);

        assertFalse(accountService.updateAccount(1L, request));

        verify(accountRepository, never()).save(any());
    }

    @Test
    void updateAccount_validPassword_updatesPassword() {
        mockAccountExists();

        when(passwordEncoder.matches("oldPassword", "storedHash"))
                .thenReturn(true);
        when(passwordEncoder.encode("newPassword"))
                .thenReturn("newHash");

        AccountUpdateRequest request = new AccountUpdateRequest(
                "oldPassword", "newPassword", null, null);

        assertTrue(accountService.updateAccount(1L, request));

        assertEquals("newHash", account.getPassword());
        verify(accountRepository).save(account);
    }

    @Test
    void updateAccount_incorrectPassword_returnsFalse() {
        mockAccountExists();

        when(passwordEncoder.matches("wrongPassword", "storedHash"))
                .thenReturn(false);

        AccountUpdateRequest request = new AccountUpdateRequest(
                "wrongPassword", "newPassword", null, null);

        assertFalse(accountService.updateAccount(1L, request));

        assertEquals("storedHash", account.getPassword());
        verify(passwordEncoder, never()).encode(anyString());
        verify(accountRepository, never()).save(any());
    }

    @Test
    void updateAccount_duplicateIngredientIds_returnsFalse() {
        mockAccountExists();

        AccountUpdateRequest request = new AccountUpdateRequest(
                null, null, List.of(10L, 10L), null);

        assertFalse(accountService.updateAccount(1L, request));

        verifyNoInteractions(ingredientRepository);
        verify(accountRepository, never()).save(any());
    }

    @Test
    void updateAccount_nonexistentIngredient_returnsFalse() {
        mockAccountExists();

        when(ingredientRepository.findAllById(anySet()))
                .thenReturn(List.of());

        AccountUpdateRequest request = new AccountUpdateRequest(
                null, null, List.of(999L), null);

        assertFalse(accountService.updateAccount(1L, request));

        verify(accountRepository, never()).save(any());
    }

    @Test
    void updateAccount_invalidIngredients_doNotChangePassword() {
        mockAccountExists();

        when(passwordEncoder.matches("oldPassword", "storedHash"))
                .thenReturn(true);

        AccountUpdateRequest request = new AccountUpdateRequest(
                "oldPassword", "newPassword", List.of(-1L), null);

        assertFalse(accountService.updateAccount(1L, request));

        assertEquals("storedHash", account.getPassword());
        verify(passwordEncoder, never()).encode(anyString());
        verify(accountRepository, never()).save(any());
    }

    @Test
    void updateAccount_replacesAccountIngredients() {
        mockAccountExists();

        Account otherAccount = new Account();
        otherAccount.setAccountId(2L);

        Ingredient oldIngredient = new Ingredient();
        oldIngredient.setIngredientId(10L);
        oldIngredient.getAccounts().add(account);
        oldIngredient.getAccounts().add(otherAccount);
        account.getIngredients().add(oldIngredient);

        Ingredient newIngredient = new Ingredient();
        newIngredient.setIngredientId(20L);
        newIngredient.getAccounts().add(otherAccount);

        when(ingredientRepository.findAllById(anySet()))
                .thenReturn(List.of(newIngredient));

        when(ingredientRepository.findByAccounts_AccountId(1L))
                .thenReturn(List.of(oldIngredient));

        AccountUpdateRequest request = new AccountUpdateRequest(
                null, null, List.of(20L), null);

        assertTrue(accountService.updateAccount(1L, request));

        assertFalse(oldIngredient.getAccounts().contains(account));
        assertTrue(oldIngredient.getAccounts().contains(otherAccount));
        assertTrue(newIngredient.getAccounts().contains(account));
        assertEquals(List.of(newIngredient), account.getIngredients());
    }

    @Test
    void updateAccount_omittedCollections_remainUnchanged() {
        mockAccountExists();

        Ingredient ingredient = new Ingredient();
        ingredient.setIngredientId(10L);
        account.getIngredients().add(ingredient);

        AccountUpdateRequest request =
                new AccountUpdateRequest(null, null, null, null);

        assertTrue(accountService.updateAccount(1L, request));

        assertEquals(List.of(ingredient), account.getIngredients());
        assertEquals("storedHash", account.getPassword());

        verifyNoInteractions(
                ingredientRepository,
                dietaryRestrictionRepository);
    }

    @Test
    void updateAccount_replacesDietaryRestrictions() {
        mockAccountExists();

        DietaryRestriction removed = new DietaryRestriction();
        removed.setDietaryRestrictionId(10L);
        removed.setDietaryRestrictionName("Low Sugar");
        removed.setAccount(account);

        DietaryRestriction retained = new DietaryRestriction();
        retained.setDietaryRestrictionId(20L);
        retained.setDietaryRestrictionName("Dairy-Free");
        retained.setAccount(account);

        Ingredient ingredient = new Ingredient();
        ingredient.getDietaryRestriction().add(removed);
        removed.getIngredients().add(ingredient);

        account.getDietaryRestrictions().add(removed);
        account.getDietaryRestrictions().add(retained);

        when(dietaryRestrictionRepository.findByAccount_AccountId(1L))
                .thenReturn(List.of(removed, retained));

        when(dietaryRestrictionRepository.save(any(DietaryRestriction.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        AccountUpdateRequest request = new AccountUpdateRequest(
                null, null, null,
                List.of("Dairy-Free", "Vegetarian"));

        assertTrue(accountService.updateAccount(1L, request));

        verify(dietaryRestrictionRepository).delete(removed);

        assertFalse(account.getDietaryRestrictions().contains(removed));
        assertTrue(account.getDietaryRestrictions().contains(retained));
        assertFalse(ingredient.getDietaryRestriction().contains(removed));

        DietaryRestriction created = account.getDietaryRestrictions()
                .stream()
                .filter(r -> r.getDietaryRestrictionName()
                        .equals("Vegetarian"))
                .findFirst()
                .orElseThrow();

        assertSame(account, created.getAccount());
    }

    @Test
    void updateAccount_duplicateRestrictionNames_returnsFalse() {
        mockAccountExists();

        AccountUpdateRequest request = new AccountUpdateRequest(
                null, null, null,
                List.of("Vegetarian", " vegetarian "));

        assertFalse(accountService.updateAccount(1L, request));

        verifyNoInteractions(dietaryRestrictionRepository);
        verify(accountRepository, never()).save(any());
    }

    @Test
    void updateAccount_missingCurrentPassword_returnsFalse() {
        mockAccountExists();

        AccountUpdateRequest request = new AccountUpdateRequest(
                null, "newPassword", null, null);

        assertFalse(accountService.updateAccount(1L, request));

        assertEquals("storedHash", account.getPassword());
        verify(passwordEncoder, never()).encode(anyString());
        verify(accountRepository, never()).save(any());
    }

    @Test
    void updateAccount_blankNewPassword_returnsFalse() {
        mockAccountExists();

        AccountUpdateRequest request = new AccountUpdateRequest(
                "oldPassword", "   ", null, null);

        assertFalse(accountService.updateAccount(1L, request));

        assertEquals("storedHash", account.getPassword());
        verifyNoInteractions(passwordEncoder);
        verify(accountRepository, never()).save(any());
    }

    @Test
    void updateAccount_emptyNewPassword_doesNotChangePassword() {
        mockAccountExists();

        AccountUpdateRequest request = new AccountUpdateRequest(
                null, "", null, null);

        assertTrue(accountService.updateAccount(1L, request));

        assertEquals("storedHash", account.getPassword());
        verifyNoInteractions(passwordEncoder);
        verify(accountRepository).save(account);
    }

    @Test
    void updateAccount_nullIngredientId_returnsFalse() {
        mockAccountExists();

        AccountUpdateRequest request = new AccountUpdateRequest(
                null, null,
                java.util.Arrays.asList(10L, null),
                null);

        assertFalse(accountService.updateAccount(1L, request));

        verifyNoInteractions(ingredientRepository);
        verify(accountRepository, never()).save(any());
    }

    @Test
    void updateAccount_emptyIngredientList_clearsIngredients() {
        mockAccountExists();

        Ingredient ingredient = new Ingredient();
        ingredient.setIngredientId(10L);
        ingredient.getAccounts().add(account);
        account.getIngredients().add(ingredient);

        when(ingredientRepository.findAllById(anySet()))
                .thenReturn(List.of());

        when(ingredientRepository.findByAccounts_AccountId(1L))
                .thenReturn(List.of(ingredient));

        AccountUpdateRequest request = new AccountUpdateRequest(
                null, null, List.of(), null);

        assertTrue(accountService.updateAccount(1L, request));

        assertTrue(account.getIngredients().isEmpty());
        assertFalse(ingredient.getAccounts().contains(account));

        verify(accountRepository).save(account);
        verify(ingredientRepository, never()).delete(any());
    }

    @Test
    void updateAccount_emptyRestrictionList_clearsRestrictions() {
        mockAccountExists();

        DietaryRestriction restriction = new DietaryRestriction();
        restriction.setDietaryRestrictionId(10L);
        restriction.setDietaryRestrictionName("Dairy-Free");
        restriction.setAccount(account);

        Ingredient ingredient = new Ingredient();
        ingredient.getDietaryRestriction().add(restriction);
        restriction.getIngredients().add(ingredient);

        account.getDietaryRestrictions().add(restriction);

        when(dietaryRestrictionRepository.findByAccount_AccountId(1L))
                .thenReturn(List.of(restriction));

        AccountUpdateRequest request = new AccountUpdateRequest(
                null, null, null, List.of());

        assertTrue(accountService.updateAccount(1L, request));

        assertTrue(account.getDietaryRestrictions().isEmpty());
        assertFalse(ingredient.getDietaryRestriction().contains(restriction));

        verify(dietaryRestrictionRepository).delete(restriction);
    }

    @Test
    void updateAccount_retainedRestriction_preservesIngredients() {
        mockAccountExists();

        DietaryRestriction restriction = new DietaryRestriction();
        restriction.setDietaryRestrictionId(10L);
        restriction.setDietaryRestrictionName("Dairy-Free");
        restriction.setAccount(account);

        Ingredient ingredient = new Ingredient();
        ingredient.getDietaryRestriction().add(restriction);
        restriction.getIngredients().add(ingredient);

        account.getDietaryRestrictions().add(restriction);

        when(dietaryRestrictionRepository.findByAccount_AccountId(1L))
                .thenReturn(List.of(restriction));

        AccountUpdateRequest request = new AccountUpdateRequest(
                null, null, null, List.of("dairy-free"));

        assertTrue(accountService.updateAccount(1L, request));

        assertTrue(account.getDietaryRestrictions().contains(restriction));
        assertTrue(restriction.getIngredients().contains(ingredient));
        assertTrue(ingredient.getDietaryRestriction().contains(restriction));

        verify(dietaryRestrictionRepository, never()).delete(any());
        verify(dietaryRestrictionRepository, never()).save(any());
    }

    @Test
    void updateAccount_invalidRestrictions_doNotChangePassword() {
        mockAccountExists();

        when(passwordEncoder.matches("oldPassword", "storedHash"))
                .thenReturn(true);

        AccountUpdateRequest request = new AccountUpdateRequest(
                "oldPassword",
                "newPassword",
                null,
                List.of("Vegetarian", "   "));

        assertFalse(accountService.updateAccount(1L, request));

        assertEquals("storedHash", account.getPassword());
        verify(passwordEncoder, never()).encode(anyString());
        verify(accountRepository, never()).save(any());
        verifyNoInteractions(dietaryRestrictionRepository);
    }
}
