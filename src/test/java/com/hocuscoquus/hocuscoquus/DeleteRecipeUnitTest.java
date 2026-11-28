package com.hocuscoquus.hocuscoquus;

import com.hocuscoquus.hocuscoquus.entity.Account;
import com.hocuscoquus.hocuscoquus.entity.CalendarEntry;
import com.hocuscoquus.hocuscoquus.entity.Recipe;
import com.hocuscoquus.hocuscoquus.entity.RecipeTag;
import com.hocuscoquus.hocuscoquus.repository.IngredientRepository;
import com.hocuscoquus.hocuscoquus.repository.RecipeIngredientRepository;
import com.hocuscoquus.hocuscoquus.repository.RecipeRepository;
import com.hocuscoquus.hocuscoquus.service.IngredientService;
import com.hocuscoquus.hocuscoquus.service.RecipeService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeleteRecipeUnitTest {

    @Mock
    private IngredientService ingredientService;

    @Mock
    private RecipeRepository recipeRepository;

    @Mock
    private RecipeIngredientRepository recipeIngredientRepository;

    @Mock
    private IngredientRepository ingredientRepository;

    private RecipeService recipeService;

    private static final long ACCOUNT_ID = 1L;
    private static final long OTHER_ACCOUNT_ID = 2L;
    private static final long RECIPE_ID = 10L;

    @BeforeEach
    void setUp() {
        recipeService = new RecipeService(
                ingredientService,
                recipeRepository,
                recipeIngredientRepository,
                ingredientRepository
        );
    }

    private Recipe createRecipe(long recipeId, long accountId) {
        Account account = new Account();
        account.setAccountId(accountId);

        Recipe recipe = Recipe.builder()
                .setRecipeName("Test Recipe")
                .setAccount(account)
                .build();

        recipe.setRecipeId(recipeId);

        return recipe;
    }

    @Test
    void deleteOwnedRecipe() {
        Recipe recipe = createRecipe(RECIPE_ID, ACCOUNT_ID);

        when(recipeRepository
                .findForUpdateByRecipeIdAndAccount_AccountId(
                        RECIPE_ID, ACCOUNT_ID))
                .thenReturn(Optional.of(recipe));

        assertDoesNotThrow(() ->
                recipeService.deleteRecipe(RECIPE_ID, ACCOUNT_ID));

        verify(recipeRepository)
                .findForUpdateByRecipeIdAndAccount_AccountId(
                        RECIPE_ID, ACCOUNT_ID);

        verify(recipeRepository).delete(recipe);
    }

    @Test
    void deleteNonexistentRecipeReturnsNotFound() {
        when(recipeRepository
                .findForUpdateByRecipeIdAndAccount_AccountId(
                        RECIPE_ID, ACCOUNT_ID))
                .thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> recipeService.deleteRecipe(RECIPE_ID, ACCOUNT_ID)
        );

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());

        verify(recipeRepository, never()).delete(any(Recipe.class));
    }

    @Test
    void deleteAnotherAccountsRecipeReturnsNotFound() {
        when(recipeRepository
                .findForUpdateByRecipeIdAndAccount_AccountId(
                        RECIPE_ID, OTHER_ACCOUNT_ID))
                .thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> recipeService.deleteRecipe(
                        RECIPE_ID, OTHER_ACCOUNT_ID)
        );

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());

        verify(recipeRepository)
                .findForUpdateByRecipeIdAndAccount_AccountId(
                        RECIPE_ID, OTHER_ACCOUNT_ID);

        verify(recipeRepository, never()).delete(any(Recipe.class));
    }

    @Test
    void deleteRecipeWithZeroIdReturnsBadRequest() {
        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> recipeService.deleteRecipe(0L, ACCOUNT_ID)
        );

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());

        verifyNoInteractions(recipeRepository);
    }

    @Test
    void deleteRecipeWithNegativeIdReturnsBadRequest() {
        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> recipeService.deleteRecipe(-1L, ACCOUNT_ID)
        );

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());

        verifyNoInteractions(recipeRepository);
    }

    @Test
    void deleteRecipeRemovesTagAssociations() {
        Recipe recipe = createRecipe(RECIPE_ID, ACCOUNT_ID);

        RecipeTag tag = new RecipeTag();
        tag.setTag("Test Tag");

        tag.getRecipes().add(recipe);
        recipe.getRecipeTags().add(tag);

        when(recipeRepository
                .findForUpdateByRecipeIdAndAccount_AccountId(
                        RECIPE_ID, ACCOUNT_ID))
                .thenReturn(Optional.of(recipe));

        recipeService.deleteRecipe(RECIPE_ID, ACCOUNT_ID);

        assertFalse(tag.getRecipes().contains(recipe));
        assertTrue(recipe.getRecipeTags().isEmpty());

        verify(recipeRepository).delete(recipe);
    }

    @Test
    void deleteRecipePreservesOtherRecipesInTag() {
        Recipe recipe = createRecipe(RECIPE_ID, ACCOUNT_ID);
        Recipe otherRecipe = createRecipe(11L, OTHER_ACCOUNT_ID);

        RecipeTag tag = new RecipeTag();
        tag.setTag("Shared Tag");

        tag.getRecipes().add(recipe);
        tag.getRecipes().add(otherRecipe);

        recipe.getRecipeTags().add(tag);
        otherRecipe.getRecipeTags().add(tag);

        when(recipeRepository
                .findForUpdateByRecipeIdAndAccount_AccountId(
                        RECIPE_ID, ACCOUNT_ID))
                .thenReturn(Optional.of(recipe));

        recipeService.deleteRecipe(RECIPE_ID, ACCOUNT_ID);

        assertFalse(tag.getRecipes().contains(recipe));
        assertTrue(tag.getRecipes().contains(otherRecipe));
        assertEquals(1, tag.getRecipes().size());

        assertTrue(recipe.getRecipeTags().isEmpty());
        assertTrue(otherRecipe.getRecipeTags().contains(tag));

        verify(recipeRepository).delete(recipe);
        verify(recipeRepository, never()).delete(otherRecipe);
    }

    @Test
    void deleteRecipeRemovesCalendarEntryAssociations() {
        Recipe recipe = createRecipe(RECIPE_ID, ACCOUNT_ID);

        CalendarEntry entry = new CalendarEntry();

        entry.getRecipes().add(recipe);
        recipe.getCalendarEntries().add(entry);

        when(recipeRepository
                .findForUpdateByRecipeIdAndAccount_AccountId(
                        RECIPE_ID, ACCOUNT_ID))
                .thenReturn(Optional.of(recipe));

        recipeService.deleteRecipe(RECIPE_ID, ACCOUNT_ID);

        assertFalse(entry.getRecipes().contains(recipe));
        assertTrue(recipe.getCalendarEntries().isEmpty());

        verify(recipeRepository).delete(recipe);
    }

    @Test
    void deleteRecipePreservesOtherRecipesInCalendarEntry() {
        Recipe recipe = createRecipe(RECIPE_ID, ACCOUNT_ID);
        Recipe otherRecipe = createRecipe(11L, OTHER_ACCOUNT_ID);

        CalendarEntry entry = new CalendarEntry();

        entry.getRecipes().add(recipe);
        entry.getRecipes().add(otherRecipe);

        recipe.getCalendarEntries().add(entry);
        otherRecipe.getCalendarEntries().add(entry);

        when(recipeRepository
                .findForUpdateByRecipeIdAndAccount_AccountId(
                        RECIPE_ID, ACCOUNT_ID))
                .thenReturn(Optional.of(recipe));

        recipeService.deleteRecipe(RECIPE_ID, ACCOUNT_ID);

        assertFalse(entry.getRecipes().contains(recipe));
        assertTrue(entry.getRecipes().contains(otherRecipe));
        assertEquals(1, entry.getRecipes().size());

        assertTrue(recipe.getCalendarEntries().isEmpty());
        assertTrue(otherRecipe.getCalendarEntries().contains(entry));

        verify(recipeRepository).delete(recipe);
        verify(recipeRepository, never()).delete(otherRecipe);
    }

    @Test
    void deleteRecipeRemovesMultipleAssociations() {
        Recipe recipe = createRecipe(RECIPE_ID, ACCOUNT_ID);

        RecipeTag firstTag = new RecipeTag();
        RecipeTag secondTag = new RecipeTag();

        CalendarEntry firstEntry = new CalendarEntry();
        CalendarEntry secondEntry = new CalendarEntry();

        firstTag.getRecipes().add(recipe);
        secondTag.getRecipes().add(recipe);

        firstEntry.getRecipes().add(recipe);
        secondEntry.getRecipes().add(recipe);

        recipe.getRecipeTags().add(firstTag);
        recipe.getRecipeTags().add(secondTag);

        recipe.getCalendarEntries().add(firstEntry);
        recipe.getCalendarEntries().add(secondEntry);

        when(recipeRepository
                .findForUpdateByRecipeIdAndAccount_AccountId(
                        RECIPE_ID, ACCOUNT_ID))
                .thenReturn(Optional.of(recipe));

        recipeService.deleteRecipe(RECIPE_ID, ACCOUNT_ID);

        assertTrue(firstTag.getRecipes().isEmpty());
        assertTrue(secondTag.getRecipes().isEmpty());

        assertTrue(firstEntry.getRecipes().isEmpty());
        assertTrue(secondEntry.getRecipes().isEmpty());

        assertTrue(recipe.getRecipeTags().isEmpty());
        assertTrue(recipe.getCalendarEntries().isEmpty());

        verify(recipeRepository).delete(recipe);
    }
}