package com.hocuscoquus.hocuscoquus.handler;

import com.hocuscoquus.hocuscoquus.entity.Recipe;
import com.hocuscoquus.hocuscoquus.entity.RecipeIngredient;
import com.hocuscoquus.hocuscoquus.model.*;
import com.hocuscoquus.hocuscoquus.service.RecipeService;
import org.springframework.boot.http.converter.autoconfigure.ServerHttpMessageConvertersCustomizer;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Service
public class RecipeHandler {

	private final RecipeService recipeService;
    private final ServerHttpMessageConvertersCustomizer serverConvertersCustomizer;

    public RecipeHandler(RecipeService recipeService, ServerHttpMessageConvertersCustomizer serverConvertersCustomizer) {
        this.recipeService = recipeService;
	    this.serverConvertersCustomizer = serverConvertersCustomizer;
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
                newRecipe.computeNumReactions(),
				newRecipe.getAccount().getAccountId()
        			);
    		
    		return ResponseEntity.ok(response);
    	}
    	catch (IllegalArgumentException e) {
    		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e);
    	}
    	
    }

	/**
	 * Used to create a {@link RecipeRecord} from a given {@link Recipe} entity within the {@link com.hocuscoquus.hocuscoquus.repository.RecipeRepository}.
	 * @param recipeID ID of the recipe for which we want to retrieve the record.
	 * @return {@link RecipeRecord} as a ResponseEntity.
	 */
	public ResponseEntity<?> getRecipeByID(long recipeID) {
		Recipe recipe = recipeService.getRecipeByID(recipeID);

		List<RecipeIngredientRecord> ingredients = new ArrayList<>(recipe.getRecipeIngredients().size());
		HashMap<ReactionType, Integer> reactions = new HashMap<>(2);

		for (RecipeIngredient ingredient : recipe.getRecipeIngredients()) {
			ingredients.add(
					new RecipeIngredientRecord(
							ingredient.getMeasurement(),
							ingredient.getQuantity(),
							ingredient.getIngredient().getIngredientName(),
							ingredient.getIngredient().getIngredientId()
					)
			);
		}

		reactions.put(ReactionType.POSITIVE, recipeService.computeNumReactions(recipeID, ReactionType.POSITIVE));
		reactions.put(ReactionType.NEGATIVE, recipeService.computeNumReactions(recipeID, ReactionType.NEGATIVE));

		RecipeRecord response = new RecipeRecord(
				recipe.getRecipeId(),
				recipe.getRecipeName(),
				recipe.getRecipeDescription(),
				recipe.getInstructions(),
				ingredients,
				recipe.getImage(),
				reactions,
				recipe.getAccount().getAccountId()
		);

		return ResponseEntity.ok(response);
	}
    
}
