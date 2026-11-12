package com.hocuscoquus.hocuscoquus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.hocuscoquus.hocuscoquus.controller.RecipeController;
import com.hocuscoquus.hocuscoquus.entity.*;
import com.hocuscoquus.hocuscoquus.model.ReactionType;
import com.hocuscoquus.hocuscoquus.model.ReactionTypeRecord;
import com.hocuscoquus.hocuscoquus.model.RecipeIngredientRecord;
import com.hocuscoquus.hocuscoquus.model.RecipeRecord;
import com.hocuscoquus.hocuscoquus.service.RecipeService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class ViewRecipeTest {

    @Autowired
    RecipeController recipeController;

    @Autowired
    RecipeService recipeService;

    @Test
    void viewRecipe() {
        final int recipeID = 29;
        ResponseEntity<?> viewRecipeResponse = recipeController.viewRecipe(recipeID);
        assertTrue(viewRecipeResponse.getStatusCode().is2xxSuccessful());
        Recipe recipe = recipeService.getRecipeByID(recipeID);
        RecipeRecord recipeRecord = ((RecipeRecord) viewRecipeResponse.getBody());
        assertNotNull(recipeRecord);

        // Test that basic fields match.
        assertEquals(recipe.getRecipeId(), recipeRecord.id());
        assertEquals(recipe.getRecipeName(), recipeRecord.name());
        assertEquals(recipe.getRecipeDescription(), recipeRecord.description());
        assertEquals(recipe.getInstructions(), recipeRecord.instructions());
        assertEquals(recipe.getAccount().getAccountId(), recipeRecord.accountId());
        assertEquals(recipe.getImage(), recipeRecord.image());

        // Test ingredients match.
        assertEquals(recipe.getRecipeIngredients().size(), recipeRecord.ingredients().size());
        List<Long> recipeIngredientIDs = new ArrayList<>();
        List<Long> recipeRecordIngredientIDs = new ArrayList<>();
        for (RecipeIngredientRecord ingredientRecord : recipeRecord.ingredients())
            recipeRecordIngredientIDs.add(ingredientRecord.ingredientId());
        for (RecipeIngredient ingredient : recipe.getRecipeIngredients())
            recipeIngredientIDs.add(ingredient.getIngredient().getIngredientId());
        for (Long ingredientID : recipeIngredientIDs)
            assertTrue(recipeRecordIngredientIDs.contains(ingredientID));

        // Test reactions match.
        int positiveReactions = recipeService.computeNumReactions(recipeID, ReactionType.POSITIVE);
        int negativeReactions = recipeService.computeNumReactions(recipeID, ReactionType.NEGATIVE);
        HashMap<ReactionTypeRecord, Integer> recipeRecordReactions = recipeRecord.reactions();
        assertEquals(positiveReactions, recipeRecordReactions.get(new ReactionTypeRecord(ReactionType.POSITIVE)));
        assertEquals(negativeReactions, recipeRecordReactions.get(new ReactionTypeRecord(ReactionType.NEGATIVE)));
    }
}
