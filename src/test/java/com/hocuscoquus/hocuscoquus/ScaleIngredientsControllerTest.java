package com.hocuscoquus.hocuscoquus;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;

import com.hocuscoquus.hocuscoquus.config.SecurityConfig;
import com.hocuscoquus.hocuscoquus.controller.RecipeController;
import com.hocuscoquus.hocuscoquus.entity.Account;
import com.hocuscoquus.hocuscoquus.entity.AccountDetails;
import com.hocuscoquus.hocuscoquus.handler.RecipeHandler;
import com.hocuscoquus.hocuscoquus.model.ReactionType;
import com.hocuscoquus.hocuscoquus.model.RecipeIngredientRecord;
import com.hocuscoquus.hocuscoquus.model.RecipeRecord;
import com.hocuscoquus.hocuscoquus.service.RecipeService;

@WebMvcTest(RecipeController.class)
@Import(SecurityConfig.class)
class ScaleIngredientsControllerTest {

    private static final String URL =
            "/api/recipe/view-recipe/10";

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private RecipeHandler recipeHandler;

    @MockitoBean
    private RecipeService recipeService;

    private AccountDetails testUser() {
        Account account = new Account();
        account.setAccountId(2L);
        account.setFirstName("Test");
        account.setEmail("test@example.com");
        account.setPassword("storedHash");

        return new AccountDetails(account);
    }

    private RecipeRecord testRecipe(double scalar) {
        return new RecipeRecord(
                10L,
                "Test Pancakes",
                "Test description",
                "Mix and cook.",
                List.of(
                        new RecipeIngredientRecord(
                                "cups", 2.0 * scalar,
                                "flour", 101L),
                        new RecipeIngredientRecord(
                                "cups", 1.5 * scalar,
                                "milk", 102L)),
                null,
                Map.of(
                        ReactionType.POSITIVE, 0,
                        ReactionType.NEGATIVE, 0),
                1L);
    }

    @Test
    void scaleIngredients_validScalar_returnsScaledRecipe()
            throws Exception {

        when(recipeHandler.getRecipeByID(10L, null, 2.0))
                .thenReturn(ResponseEntity.ok(testRecipe(2.0)));

        mockMvc.perform(get(URL).param("scalar", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.name").value("Test Pancakes"))
                .andExpect(jsonPath("$.ingredients[0].quantity")
                        .value(4.0))
                .andExpect(jsonPath("$.ingredients[1].quantity")
                        .value(3.0))
                .andExpect(jsonPath("$.accountId").value(1));

        verify(recipeHandler).getRecipeByID(10L, null, 2.0);
    }

    @Test
    void scaleIngredients_fractionalScalar_returnsScaledRecipe()
            throws Exception {

        when(recipeHandler.getRecipeByID(10L, null, 0.5))
                .thenReturn(ResponseEntity.ok(testRecipe(0.5)));

        mockMvc.perform(get(URL).param("scalar", "0.5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ingredients[0].quantity")
                        .value(1.0))
                .andExpect(jsonPath("$.ingredients[1].quantity")
                        .value(0.75));

        verify(recipeHandler).getRecipeByID(10L, null, 0.5);
    }

    @Test
    void scaleIngredients_missingScalar_defaultsToOne()
            throws Exception {

        when(recipeHandler.getRecipeByID(10L, null, 1.0))
                .thenReturn(ResponseEntity.ok(testRecipe(1.0)));

        mockMvc.perform(get(URL))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ingredients[0].quantity")
                        .value(2.0))
                .andExpect(jsonPath("$.ingredients[1].quantity")
                        .value(1.5));

        verify(recipeHandler).getRecipeByID(10L, null, 1.0);
    }

    @Test
    void scaleIngredients_scalarOne_returnsOriginalQuantities()
            throws Exception {

        when(recipeHandler.getRecipeByID(10L, null, 1.0))
                .thenReturn(ResponseEntity.ok(testRecipe(1.0)));

        mockMvc.perform(get(URL).param("scalar", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ingredients[0].quantity")
                        .value(2.0))
                .andExpect(jsonPath("$.ingredients[1].quantity")
                        .value(1.5));

        verify(recipeHandler).getRecipeByID(10L, null, 1.0);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "0", "-1", "-2.5", "NaN", "Infinity", "-Infinity"
    })
    void scaleIngredients_invalidScalar_returnsBadRequest(
            String scalar) throws Exception {

        double value = Double.parseDouble(scalar);

        when(recipeHandler.getRecipeByID(10L, null, value))
                .thenThrow(new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Scalar must be a finite positive number"));

        mockMvc.perform(get(URL).param("scalar", scalar))
                .andExpect(status().isBadRequest());

        verify(recipeHandler).getRecipeByID(10L, null, value);
    }

    @Test
    void scaleIngredients_nonnumericScalar_returnsBadRequest()
            throws Exception {

        mockMvc.perform(get(URL).param("scalar", "abc"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(recipeHandler);
    }

    @Test
    void scaleIngredients_nonexistentRecipe_returnsNotFound()
            throws Exception {

        when(recipeHandler.getRecipeByID(999L, null, 2.0))
                .thenThrow(new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Recipe not found"));

        mockMvc.perform(get("/api/recipe/view-recipe/999")
                        .param("scalar", "2"))
                .andExpect(status().isNotFound());
    }

    @Test
    void scaleIngredients_authenticatedUser_returnsScaledRecipe()
            throws Exception {

        when(recipeHandler.getRecipeByID(10L, 2L, 2.0))
                .thenReturn(ResponseEntity.ok(testRecipe(2.0)));

        mockMvc.perform(get(URL)
                        .param("scalar", "2")
                        .with(user(testUser())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ingredients[0].quantity")
                        .value(4.0))
                .andExpect(jsonPath("$.accountId").value(1));

        verify(recipeHandler).getRecipeByID(10L, 2L, 2.0);
    }

    @Test
    void scaleIngredients_unauthenticatedUser_returnsScaledRecipe()
            throws Exception {

        when(recipeHandler.getRecipeByID(10L, null, 2.0))
                .thenReturn(ResponseEntity.ok(testRecipe(2.0)));

        mockMvc.perform(get(URL).param("scalar", "2"))
                .andExpect(status().isOk());

        verify(recipeHandler).getRecipeByID(10L, null, 2.0);
    }
}