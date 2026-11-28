package com.hocuscoquus.hocuscoquus;

import static org.junit.jupiter.api.Assertions.*;
import static org.hamcrest.Matchers.contains;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.hocuscoquus.hocuscoquus.entity.Account;
import com.hocuscoquus.hocuscoquus.entity.AccountDetails;
import com.hocuscoquus.hocuscoquus.entity.Recipe;
import com.hocuscoquus.hocuscoquus.model.RecipeCreateRequest;
import com.hocuscoquus.hocuscoquus.model.RecipeIngredientCreateRequest;
import com.hocuscoquus.hocuscoquus.repository.RecipeRepository;
import com.hocuscoquus.hocuscoquus.service.AccountService;
import com.hocuscoquus.hocuscoquus.service.RecipeService;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@SpringBootTest(properties = "spring.jpa.hibernate.ddl-auto=validate")
@AutoConfigureMockMvc
@Transactional
class ScaleIngredientsIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private RecipeService recipeService;

    @Autowired
    private RecipeRepository recipeRepository;

    @Autowired
    private AccountService accountService;

    @PersistenceContext
    private EntityManager entityManager;

    private Account account;
    private long recipeId;
    private String flourName;
    private String milkName;

    @BeforeEach
    void setUp() {
        account = createAccount();

        String suffix = UUID.randomUUID().toString();

        flourName = "test-flour-" + suffix;
        milkName = "test-milk-" + suffix;

        RecipeCreateRequest request = new RecipeCreateRequest(
                "Test Pancakes",
                "A recipe for testing ingredient scaling.",
                "Mix ingredients and cook.",
                null,
                List.of(
                        new RecipeIngredientCreateRequest(
                                flourName, 2.0, "cups"),
                        new RecipeIngredientCreateRequest(
                                milkName, 1.5, "cups")
                )
        );

        Recipe recipe =
                recipeService.createRecipe(account, request);

        recipeId = recipe.getRecipeId();

        flushAndClear();
    }

    private Account createAccount() {
        return accountService.createAccount(
                "Test",
                "test-" + UUID.randomUUID() + "@example.com",
                "password");
    }

    private void flushAndClear() {
        entityManager.flush();
        entityManager.clear();
    }

    private String recipeUrl() {
        return "/api/recipe/view-recipe/" + recipeId;
    }

    private String quantityPath(String ingredientName) {
        return "$.ingredients[?(@.ingredientName == '"
                + ingredientName + "')].quantity";
    }

    private double persistedQuantity(
            Recipe recipe, String ingredientName) {

        return recipe.getRecipeIngredients().stream()
                .filter(ingredient ->
                        ingredient.getIngredient()
                                .getIngredientName()
                                .equals(ingredientName))
                .findFirst()
                .orElseThrow()
                .getQuantity();
    }

    @Test
    void scaleIngredients_integerScalar_returnsScaledRecipe()
            throws Exception {

        mockMvc.perform(get(recipeUrl())
                        .param("scalar", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(recipeId))
                .andExpect(jsonPath("$.name")
                        .value("Test Pancakes"))
                .andExpect(jsonPath("$.accountId")
                        .value(account.getAccountId()))
                .andExpect(jsonPath(quantityPath(flourName))
                        .value(contains(4.0)))
                .andExpect(jsonPath(quantityPath(milkName))
                        .value(contains(3.0)));
    }

    @Test
    void scaleIngredients_fractionalScalar_returnsScaledRecipe()
            throws Exception {

        mockMvc.perform(get(recipeUrl())
                        .param("scalar", "0.5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath(quantityPath(flourName))
                        .value(contains(1.0)))
                .andExpect(jsonPath(quantityPath(milkName))
                        .value(contains(0.75)));
    }

    @Test
    void scaleIngredients_missingScalar_returnsOriginalRecipe()
            throws Exception {

        mockMvc.perform(get(recipeUrl()))
                .andExpect(status().isOk())
                .andExpect(jsonPath(quantityPath(flourName))
                        .value(contains(2.0)))
                .andExpect(jsonPath(quantityPath(milkName))
                        .value(contains(1.5)));
    }

    @Test
    void scaleIngredients_doesNotModifyDatabase()
            throws Exception {

        mockMvc.perform(get(recipeUrl())
                        .param("scalar", "3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath(quantityPath(flourName))
                        .value(contains(6.0)))
                .andExpect(jsonPath(quantityPath(milkName))
                        .value(contains(4.5)));

        flushAndClear();

        Recipe persisted =
                recipeRepository.findByRecipeId(recipeId);

        assertNotNull(persisted);

        assertEquals(2.0,
                persistedQuantity(persisted, flourName), 1e-9);

        assertEquals(1.5,
                persistedQuantity(persisted, milkName), 1e-9);
    }

    @Test
    void scaleIngredients_repeatedRequests_doNotAccumulate()
            throws Exception {

        mockMvc.perform(get(recipeUrl())
                        .param("scalar", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath(quantityPath(flourName))
                        .value(contains(4.0)));

        mockMvc.perform(get(recipeUrl())
                        .param("scalar", "0.5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath(quantityPath(flourName))
                        .value(contains(1.0)));

        mockMvc.perform(get(recipeUrl()))
                .andExpect(status().isOk())
                .andExpect(jsonPath(quantityPath(flourName))
                        .value(contains(2.0)))
                .andExpect(jsonPath(quantityPath(milkName))
                        .value(contains(1.5)));
    }

    @Test
    void scaleIngredients_ownerCanScaleRecipe()
            throws Exception {

        mockMvc.perform(get(recipeUrl())
                        .param("scalar", "2")
                        .with(user(new AccountDetails(account))))
                .andExpect(status().isOk())
                .andExpect(jsonPath(quantityPath(flourName))
                        .value(contains(4.0)));
    }

    @Test
    void scaleIngredients_nonOwnerCanScaleRecipe()
            throws Exception {

        Account otherAccount = createAccount();

        mockMvc.perform(get(recipeUrl())
                        .param("scalar", "2")
                        .with(user(new AccountDetails(otherAccount))))
                .andExpect(status().isOk())
                .andExpect(jsonPath(quantityPath(flourName))
                        .value(contains(4.0)))
                .andExpect(jsonPath("$.accountId")
                        .value(account.getAccountId()));
    }

    @Test
    void scaleIngredients_invalidScalar_returnsBadRequest()
            throws Exception {

        mockMvc.perform(get(recipeUrl())
                        .param("scalar", "0"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void scaleIngredients_nonexistentRecipe_returnsNotFound()
            throws Exception {

        mockMvc.perform(
                        get("/api/recipe/view-recipe/" + Long.MAX_VALUE)
                        .param("scalar", "2"))
                .andExpect(status().isNotFound());
    }
}