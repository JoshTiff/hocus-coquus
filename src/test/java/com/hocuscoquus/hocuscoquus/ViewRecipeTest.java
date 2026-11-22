package com.hocuscoquus.hocuscoquus;

import com.hocuscoquus.hocuscoquus.entity.*;
import com.hocuscoquus.hocuscoquus.repository.RecipeRepository;
import com.hocuscoquus.hocuscoquus.service.AccountService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class ViewRecipeTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    RecipeRepository recipeRepository;

    @Autowired
    AccountService accountService;

    Account account;

    Long recipeID = null;
    final String recipeName = "Cheese Hot Dish";
    final String recipeDescription = "A hot dish primarily composed of cheese.";
    final String recipeInstructions = "Place cheese whiz in pan. Cook at 450 degrees Fahrenheit for ten minutes.";

    @BeforeEach
    void before() {
        account = accountService.createAccount("Test", "test@example.com", "storedHash");
        Recipe recipe = Recipe.builder()
                .setRecipeName(recipeName)
                .setRecipeDescription(recipeDescription)
                .setInstructions(recipeInstructions)
                .setAccount(account)
                .build();
        recipe = recipeRepository.save(recipe);
        recipeID = recipe.getRecipeId();
    }

    @Test
    void viewRecipeWithAPIAndNoAccount() throws Exception {
        mockMvc.perform(get("/api/recipe/view-recipe/" + recipeID).with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(recipeID))
                .andExpect(jsonPath("$.description").value(recipeDescription))
                .andExpect(jsonPath("$.instructions").value(recipeInstructions))
                .andExpect(jsonPath("$.accountId").value(account.getAccountId()));
    }

    @Test
    void viewRecipeWithAPIAndAccount() throws Exception {
        mockMvc.perform(get("/api/recipe/view-recipe/" + recipeID).with(csrf()).with(user(new AccountDetails(account))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(recipeID))
                .andExpect(jsonPath("$.description").value(recipeDescription))
                .andExpect(jsonPath("$.instructions").value(recipeInstructions))
                .andExpect(jsonPath("$.accountId").value(account.getAccountId()));
    }

    @Test
    void viewRecipe() throws Exception {
        mockMvc.perform(get("/recipe/" + recipeID).with(csrf()))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("recipe"))
                .andExpect(model().attributeHasNoErrors("recipe"));
    }
}
