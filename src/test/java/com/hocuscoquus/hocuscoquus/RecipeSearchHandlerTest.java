package com.hocuscoquus.hocuscoquus;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.http.ResponseEntity;

import com.hocuscoquus.hocuscoquus.entity.Account;
import com.hocuscoquus.hocuscoquus.entity.AccountDetails;
import com.hocuscoquus.hocuscoquus.entity.DietaryRestriction;
import com.hocuscoquus.hocuscoquus.entity.Recipe;
import com.hocuscoquus.hocuscoquus.handler.RecipeSearchHandler;
import com.hocuscoquus.hocuscoquus.service.AccountService;
import com.hocuscoquus.hocuscoquus.service.IngredientService;
import com.hocuscoquus.hocuscoquus.service.RecipeSearchService;

@ExtendWith(MockitoExtension.class)
class RecipeSearchHandlerTest {

    @Mock
    private RecipeSearchService recipeSearchService;

    @Mock
    private IngredientService ingredientService;

    @Mock
    private AccountService accountService;

    private RecipeSearchHandler handler;
    private AccountDetails accountDetails;

    @BeforeEach
    void setUp() {
        handler = new RecipeSearchHandler(
                recipeSearchService, ingredientService, accountService);

        Account account = new Account();
        account.setAccountId(17L);
        accountDetails = new AccountDetails(account);
        SecurityContextHolder.getContext().setAuthentication(
                new TestingAuthenticationToken(accountDetails, null, "ROLE_USER"));
    }

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void searchByDietaryRestrictions_withoutSavedRestrictionsReturnsBadRequest() {
        // An account must save at least one restriction before using this search.
        when(accountService.getDietaryRestrictionsForAccount(17L))
                .thenReturn(List.of());

        ResponseEntity<?> response =
                handler.processSearchByDietaryRestrictionsRequest();

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        verifyNoInteractions(recipeSearchService);
    }

    @Test
    void searchByDietaryRestrictions_searchesUsingAccountRestrictions() {
        DietaryRestriction restriction = new DietaryRestriction();
        restriction.setDietaryRestrictionName("Peanut");
        restriction.setDietaryRestrictionId(23L);
        when(accountService.getDietaryRestrictionsForAccount(17L))
                .thenReturn(List.of(restriction));
        when(recipeSearchService.searchRecipesByDietaryRestrictions(
                List.of(restriction))).thenReturn(List.of());

        ResponseEntity<?> response =
                handler.processSearchByDietaryRestrictionsRequest();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(
                "No recipes found that meet your dietary restrictions",
                response.getBody());
        verify(recipeSearchService).searchRecipesByDietaryRestrictions(
                List.of(restriction));
    }

    @Test
    void searchByDietaryRestrictions_usesOnlySelectedAccountRestriction() {
        // Supplying an ID applies only that account-owned restriction to the search.
        DietaryRestriction peanutFree = new DietaryRestriction();
        peanutFree.setDietaryRestrictionId(23L);
        peanutFree.setDietaryRestrictionName("Peanut-Free");
        DietaryRestriction dairyFree = new DietaryRestriction();
        dairyFree.setDietaryRestrictionId(24L);
        dairyFree.setDietaryRestrictionName("Dairy-Free");
        Account account = accountDetails.getAccount();
        Recipe firstRecipe = Recipe.builder()
                .setRecipeName("Vegetable soup")
                .setAccount(account)
                .build();
        Recipe secondRecipe = Recipe.builder()
                .setRecipeName("Fruit salad")
                .setAccount(account)
                .build();
        when(accountService.getDietaryRestrictionsForAccount(17L))
                .thenReturn(List.of(peanutFree, dairyFree));
        when(recipeSearchService.searchRecipesByDietaryRestrictions(
                List.of(peanutFree))).thenReturn(List.of(firstRecipe, secondRecipe));

        ResponseEntity<?> response =
                handler.processSearchByDietaryRestrictionsRequest(23L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(2, ((List<?>) response.getBody()).size());
        verify(recipeSearchService).searchRecipesByDietaryRestrictions(
                List.of(peanutFree));
    }

    @Test
    void searchByDietaryRestrictions_appliesAllSavedRestrictionsToMultipleRecipes() {
        // Without an ID, every saved restriction is applied and every matching recipe is returned.
        DietaryRestriction peanutFree = new DietaryRestriction();
        peanutFree.setDietaryRestrictionId(23L);
        peanutFree.setDietaryRestrictionName("Peanut-Free");
        DietaryRestriction dairyFree = new DietaryRestriction();
        dairyFree.setDietaryRestrictionId(24L);
        dairyFree.setDietaryRestrictionName("Dairy-Free");
        Account account = accountDetails.getAccount();
        Recipe firstRecipe = Recipe.builder()
                .setRecipeName("Vegetable soup")
                .setAccount(account)
                .build();
        Recipe secondRecipe = Recipe.builder()
                .setRecipeName("Fruit salad")
                .setAccount(account)
                .build();
        when(accountService.getDietaryRestrictionsForAccount(17L))
                .thenReturn(List.of(peanutFree, dairyFree));
        when(recipeSearchService.searchRecipesByDietaryRestrictions(
                List.of(peanutFree, dairyFree)))
                .thenReturn(List.of(firstRecipe, secondRecipe));

        ResponseEntity<?> response =
                handler.processSearchByDietaryRestrictionsRequest();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(2, ((List<?>) response.getBody()).size());
        verify(recipeSearchService).searchRecipesByDietaryRestrictions(
                List.of(peanutFree, dairyFree));
    }

    @Test
    void searchByDietaryRestrictions_cannotSelectAnotherAccountsRestriction() {
        DietaryRestriction peanutFree = new DietaryRestriction();
        peanutFree.setDietaryRestrictionId(23L);
        when(accountService.getDietaryRestrictionsForAccount(17L))
                .thenReturn(List.of(peanutFree));

        ResponseEntity<?> response =
                handler.processSearchByDietaryRestrictionsRequest(99L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verifyNoInteractions(recipeSearchService);
    }

    @Test
    void searchByDietaryRestrictions_withoutAuthenticatedAccountReturnsUnauthorized() {
        // Anonymous callers cannot read or search using account restrictions.
        SecurityContextHolder.clearContext();

        ResponseEntity<?> response =
                handler.processSearchByDietaryRestrictionsRequest();

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        verifyNoInteractions(accountService, recipeSearchService);
    }
}
