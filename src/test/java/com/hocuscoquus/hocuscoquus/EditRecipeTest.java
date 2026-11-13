package com.hocuscoquus.hocuscoquus;

import com.hocuscoquus.hocuscoquus.controller.RecipeController;
import com.hocuscoquus.hocuscoquus.entity.AccountDetails;
import com.hocuscoquus.hocuscoquus.entity.Recipe;
import com.hocuscoquus.hocuscoquus.model.RecipeEditRequest;
import com.hocuscoquus.hocuscoquus.service.RecipeService;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@Transactional
public class EditRecipeTest {

    @Autowired
    RecipeController recipeController;

    @Autowired
    RecipeService recipeService;

    @Autowired
    EntityManager entityManager;

    @Test
    void editRecipe() {
        // Use the same seeded recipe as ViewRecipeTest.
        final long recipeId = 29;
        Recipe original = recipeService.getRecipeByID(recipeId);
        assertNotNull(original, "Seeded recipe 29 is required");

        AccountDetails owner = new AccountDetails(original.getAccount());
        RecipeEditRequest request = new RecipeEditRequest(
                "Updated Soup",
                "Updated recipe description",
                "Simmer for 20 minutes.",
                "https://example.com/updated-soup.png");

        ResponseEntity<Void> response = recipeController.updateRecipe(recipeId, request, owner);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());

        // Reload from the database instead of checking the managed object we just edited.
        entityManager.flush();
        entityManager.clear();
        Recipe updated = recipeService.getRecipeByID(recipeId);
        assertNotNull(updated);

        assertEquals(recipeId, updated.getRecipeId());
        assertEquals(owner.getAccountId(), updated.getAccount().getAccountId());
        assertEquals(request.recipeName(), updated.getRecipeName());
        assertEquals(request.recipeDescription(), updated.getRecipeDescription());
        assertEquals(request.instructions(), updated.getInstructions());
        assertEquals(request.image(), updated.getImage());
    }
}