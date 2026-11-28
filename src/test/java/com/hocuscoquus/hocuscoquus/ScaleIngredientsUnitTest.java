package com.hocuscoquus.hocuscoquus;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import com.hocuscoquus.hocuscoquus.entity.Account;
import com.hocuscoquus.hocuscoquus.entity.Ingredient;
import com.hocuscoquus.hocuscoquus.entity.Recipe;
import com.hocuscoquus.hocuscoquus.entity.RecipeIngredient;
import com.hocuscoquus.hocuscoquus.model.RecipeRecord;
import com.hocuscoquus.hocuscoquus.repository.IngredientRepository;
import com.hocuscoquus.hocuscoquus.repository.RecipeIngredientRepository;
import com.hocuscoquus.hocuscoquus.repository.RecipeRepository;
import com.hocuscoquus.hocuscoquus.service.IngredientService;
import com.hocuscoquus.hocuscoquus.service.RecipeService;

@ExtendWith(MockitoExtension.class)
class ScaleIngredientsUnitTest {

    @Mock
    private RecipeRepository recipeRepository;

    @Mock
    private IngredientRepository ingredientRepository;

    @Mock
    private RecipeIngredientRepository recipeIngredientRepository;

    @Mock
    private IngredientService ingredientService;

    private RecipeService recipeService;
    private Recipe recipe;

    @BeforeEach
    void setUp() {
        recipeService = new RecipeService(
                ingredientService,
                recipeRepository,
                recipeIngredientRepository,
                ingredientRepository);

        Account account = new Account();
        account.setAccountId(1L);

        recipe = Recipe.builder()
                .setRecipeName("Test Pancakes")
                .setRecipeDescription("Test description")
                .setInstructions("Mix and cook.")
                .setAccount(account)
                .build();

        recipe.setRecipeId(10L);

        addIngredient(101L, "flour", 2.0, "cups");
        addIngredient(102L, "milk", 1.5, "cups");
        addIngredient(103L, "eggs", 3.0, "whole");
        addIngredient(104L, "salt", 0.25, "tsp");
    }

    private void addIngredient(
            long id, String name, double quantity,
            String measurement) {

        Ingredient ingredient = new Ingredient();
        ingredient.setIngredientId(id);
        ingredient.setIngredientName(name);

        RecipeIngredient recipeIngredient = new RecipeIngredient();
        recipeIngredient.setRecipe(recipe);
        recipeIngredient.setIngredient(ingredient);
        recipeIngredient.setQuantity(quantity);
        recipeIngredient.setMeasurement(measurement);

        recipe.getRecipeIngredients().add(recipeIngredient);
    }

    @ParameterizedTest
    @ValueSource(doubles = {0.5, 1.0, 2.0, 2.5, 3.0})
    void scaleIngredients_validScalar_scalesQuantities(double scalar) {
        when(recipeRepository.findByRecipeId(10L))
                .thenReturn(recipe);

        RecipeRecord result =
                recipeService.scaleIngredients(10L, null, scalar);

        assertEquals(4, result.ingredients().size());

        assertEquals(2.0 * scalar,
                result.ingredients().get(0).quantity(), 1e-9);

        assertEquals(1.5 * scalar,
                result.ingredients().get(1).quantity(), 1e-9);

        assertEquals(3.0 * scalar,
                result.ingredients().get(2).quantity(), 1e-9);

        assertEquals(0.25 * scalar,
                result.ingredients().get(3).quantity(), 1e-9);
    }

    @ParameterizedTest
    @ValueSource(doubles = {
            0.0,
            -1.0,
            -2.5,
            Double.NaN,
            Double.POSITIVE_INFINITY,
            Double.NEGATIVE_INFINITY
    })
    void scaleIngredients_invalidScalar_throwsBadRequest(double scalar) {
        ResponseStatusException exception =
                assertThrows(ResponseStatusException.class,
                        () -> recipeService.scaleIngredients(
                                10L, null, scalar));

        assertEquals(HttpStatus.BAD_REQUEST,
                exception.getStatusCode());

        verifyNoInteractions(recipeRepository);
    }

    @Test
    void scaleIngredients_recipeNotFound_throwsNotFound() {
        when(recipeRepository.findByRecipeId(999L))
                .thenReturn(null);

        ResponseStatusException exception =
                assertThrows(ResponseStatusException.class,
                        () -> recipeService.scaleIngredients(
                                999L, null, 2.0));

        assertEquals(HttpStatus.NOT_FOUND,
                exception.getStatusCode());
    }

    @Test
    void scaleIngredients_preservesRecipeMetadata() {
        when(recipeRepository.findByRecipeId(10L))
                .thenReturn(recipe);

        RecipeRecord original = RecipeRecord.getRecord(recipe);

        RecipeRecord result =
                recipeService.scaleIngredients(10L, null, 2.0);

        assertEquals(original.id(), result.id());
        assertEquals(original.name(), result.name());
        assertEquals(original.description(), result.description());
        assertEquals(original.instructions(), result.instructions());
        assertEquals(original.image(), result.image());
        assertEquals(original.reactions(), result.reactions());
        assertEquals(original.accountId(), result.accountId());
    }

    @Test
    void scaleIngredients_preservesIngredientMetadata() {
        when(recipeRepository.findByRecipeId(10L))
                .thenReturn(recipe);

        RecipeRecord original = RecipeRecord.getRecord(recipe);

        RecipeRecord result =
                recipeService.scaleIngredients(10L, null, 2.0);

        assertEquals(original.ingredients().size(),
                result.ingredients().size());

        for (int i = 0; i < original.ingredients().size(); i++) {
            var originalIngredient = original.ingredients().get(i);
            var scaledIngredient = result.ingredients().get(i);

            assertEquals(originalIngredient.ingredientId(),
                    scaledIngredient.ingredientId());

            assertEquals(originalIngredient.ingredientName(),
                    scaledIngredient.ingredientName());

            assertEquals(originalIngredient.measurement(),
                    scaledIngredient.measurement());
        }
    }

    @Test
    void scaleIngredients_doesNotModifyOriginalRecipe() {
        when(recipeRepository.findByRecipeId(10L))
                .thenReturn(recipe);

        recipeService.scaleIngredients(10L, null, 2.0);

        assertEquals(2.0,
                recipe.getRecipeIngredients().get(0).getQuantity());

        assertEquals(1.5,
                recipe.getRecipeIngredients().get(1).getQuantity());

        assertEquals(3.0,
                recipe.getRecipeIngredients().get(2).getQuantity());

        assertEquals(0.25,
                recipe.getRecipeIngredients().get(3).getQuantity());

        verify(recipeRepository).findByRecipeId(10L);
        verifyNoMoreInteractions(recipeRepository);
    }

    @Test
    void scaleIngredients_repeatedRequests_doNotAccumulate() {
        when(recipeRepository.findByRecipeId(10L))
                .thenReturn(recipe);

        RecipeRecord first =
                recipeService.scaleIngredients(10L, null, 2.0);

        RecipeRecord second =
                recipeService.scaleIngredients(10L, null, 0.5);

        assertEquals(4.0, first.ingredients().get(0).quantity());
        assertEquals(1.0, second.ingredients().get(0).quantity());

        assertEquals(2.0,
                recipe.getRecipeIngredients().get(0).getQuantity());
    }

    @Test
    void scaleIngredients_noIngredients_returnsEmptyList() {
        recipe.getRecipeIngredients().clear();

        when(recipeRepository.findByRecipeId(10L))
                .thenReturn(recipe);

        RecipeRecord result =
                recipeService.scaleIngredients(10L, null, 2.0);

        assertNotNull(result.ingredients());
        assertTrue(result.ingredients().isEmpty());
    }

    @Test
    void scaleIngredients_differentAccount_canScaleRecipe() {
        when(recipeRepository.findByRecipeId(10L))
                .thenReturn(recipe);

        RecipeRecord result =
                recipeService.scaleIngredients(10L, 2L, 2.0);

        assertEquals(4.0, result.ingredients().get(0).quantity());
        assertEquals(1L, result.accountId());
    }

    @Test
    void scaleIngredients_quantityOverflow_throwsBadRequest() {
        when(recipeRepository.findByRecipeId(10L))
                .thenReturn(recipe);

        ResponseStatusException exception =
                assertThrows(ResponseStatusException.class,
                        () -> recipeService.scaleIngredients(
                                10L, null, Double.MAX_VALUE));

        assertEquals(HttpStatus.BAD_REQUEST,
                exception.getStatusCode());

        assertEquals(
                "Scaled ingredient quantity exceeds supported range",
                exception.getReason());
    }
}