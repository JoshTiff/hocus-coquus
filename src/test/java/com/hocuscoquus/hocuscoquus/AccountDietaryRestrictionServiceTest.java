package com.hocuscoquus.hocuscoquus;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import com.hocuscoquus.hocuscoquus.entity.Account;
import com.hocuscoquus.hocuscoquus.entity.AccountDetails;
import com.hocuscoquus.hocuscoquus.entity.DietaryRestriction;
import com.hocuscoquus.hocuscoquus.entity.Ingredient;
import com.hocuscoquus.hocuscoquus.repository.AccountRepository;
import com.hocuscoquus.hocuscoquus.repository.IngredientRepository;
import com.hocuscoquus.hocuscoquus.service.AccountDietaryRestrictionService;

@ExtendWith(MockitoExtension.class)
class AccountDietaryRestrictionServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private IngredientRepository ingredientRepository;

    private AccountDietaryRestrictionService service;

    @BeforeEach
    void setUp() {
        service = new AccountDietaryRestrictionService(
                accountRepository, ingredientRepository);

        // The service derives ownership from the authenticated account.
        Account account = new Account();
        account.setAccountId(17L);
        SecurityContextHolder.getContext().setAuthentication(
                new TestingAuthenticationToken(
                        new AccountDetails(account), null, "ROLE_USER"));
    }

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void createDietaryRestriction_linksManuallySelectedIngredientsToCurrentAccount() {
        // The request provides saved ingredient IDs, not inferred ingredients from the name.
        Account account = new Account();
        account.setAccountId(17L);
        Ingredient peanut = new Ingredient();
        peanut.setIngredientId(11L);
        Ingredient peanutButter = new Ingredient();
        peanutButter.setIngredientId(12L);

        DietaryRestriction restriction = new DietaryRestriction();
        restriction.setDietaryRestrictionName("Peanut-Free");
        restriction.setIngredients(List.of(peanut, peanutButter));

        when(accountRepository.findById(17L)).thenReturn(account);
        when(ingredientRepository.findAllById(List.of(11L, 12L)))
                .thenReturn(List.of(peanut, peanutButter));
        when(accountRepository.saveAndFlush(account)).thenAnswer(invocation -> {
            restriction.setDietaryRestrictionId(23L);
            return account;
        });

        DietaryRestriction created = service.createDietaryRestriction(restriction);

        assertEquals(23L, created.getDietaryRestrictionId());
        assertEquals("peanut-free", created.getDietaryRestrictionName());
        assertEquals(account, created.getAccount());
        assertEquals(List.of(peanut, peanutButter), created.getIngredients());
        assertTrue(peanut.getDietaryRestriction().contains(created));
        assertTrue(peanutButter.getDietaryRestriction().contains(created));
        verify(accountRepository).saveAndFlush(account);
        verify(ingredientRepository).saveAll(List.of(peanut, peanutButter));
    }

    @Test
    void deleteDietaryRestriction_removesManualIngredientLinks() {
        // Deleting an account-owned restriction must also clear its ingredient join links.
        DietaryRestriction restriction = new DietaryRestriction();
        restriction.setDietaryRestrictionId(23L);
        Account account = new Account();
        account.setAccountId(17L);
        account.getDietaryRestrictions().add(restriction);
        Ingredient peanut = new Ingredient();
        peanut.getDietaryRestriction().add(restriction);
        restriction.getIngredients().add(peanut);

        when(accountRepository.findById(17L)).thenReturn(account);

        assertTrue(service.deleteDietaryRestriction(23L));

        assertFalse(peanut.getDietaryRestriction().contains(restriction));
        assertFalse(account.getDietaryRestrictions().contains(restriction));
        verify(accountRepository).save(account);
        verify(ingredientRepository).saveAll(List.of(peanut));
    }

    @Test
    void deleteDietaryRestriction_returnsFalseWhenRestrictionDoesNotBelongToAccount() {
        // A restriction absent from this account must not be deleted.
        Account account = new Account();
        account.setAccountId(17L);
        when(accountRepository.findById(17L)).thenReturn(account);

        assertFalse(service.deleteDietaryRestriction(23L));
    }
}
