package com.hocuscoquus.hocuscoquus.handler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.hocuscoquus.hocuscoquus.entity.Recipe;
import com.hocuscoquus.hocuscoquus.entity.RecipeIngredient;
import com.hocuscoquus.hocuscoquus.model.ReactionTypeRecord;
import com.hocuscoquus.hocuscoquus.model.RecipeCreateRequest;
import com.hocuscoquus.hocuscoquus.model.RecipeIngredientRecord;
import com.hocuscoquus.hocuscoquus.model.RecipeRecord;
import com.hocuscoquus.hocuscoquus.service.RecipeService;

@Service
public class RecipeHandler {

	private final RecipeService recipeService;

    public RecipeHandler(RecipeService recipeService) {
        this.recipeService = recipeService;
    }
    
    /*
     * @param request -- the RecipeCreateRequest object holding the information necessary to create a Recipe
     * 
     * constructs a Recipe initialized with corresponding the values of recipeForm
     * this object is passed to the service to save to the database.
     * 
     * returns a Recipe object
     */
    public ResponseEntity<?> processRecipeCreateRequest(RecipeCreateRequest request) {
    	
    	try {
    		Recipe newRecipe = recipeService.createRecipe(request);
    		
    		 
    		List<RecipeIngredientRecord> ingredients = new ArrayList<>();
    		HashMap<ReactionTypeRecord, Integer> reactions = new HashMap<>();
    		
    		for (RecipeIngredient ri : newRecipe.getRecipeIngredients()) {
    			RecipeIngredientRecord temp = new RecipeIngredientRecord(ri.getMeasurement(), ri.getQuantity(), ri.getIngredient().getIngredientName(), ri.getIngredient().getIngredientId());
    			ingredients.add(temp);
    		}
    		
    		RecipeRecord response = new RecipeRecord(
    				newRecipe.getRecipeId(), 
    				newRecipe.getRecipeName(), 
    				newRecipe.getRecipeDescription(), 
    				newRecipe.getInstructions(),
    				ingredients,
    				newRecipe.getImage(),
    				reactions,
    				newRecipe.getAccount().getAccountId()
        			);
    		
    		return ResponseEntity.ok(response);
    	}
    	catch (IllegalArgumentException e) {
    		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e);
    	}
    	
    }
    
}
