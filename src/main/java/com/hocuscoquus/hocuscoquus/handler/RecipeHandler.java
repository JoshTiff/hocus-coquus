package com.hocuscoquus.hocuscoquus.handler;

import com.hocuscoquus.hocuscoquus.entity.Recipe;
import com.hocuscoquus.hocuscoquus.entity.RecipeIngredient;
import com.hocuscoquus.hocuscoquus.entity.RecipeTag;
import com.hocuscoquus.hocuscoquus.model.*;
import com.hocuscoquus.hocuscoquus.service.RecipeService;
import org.springframework.boot.http.converter.autoconfigure.ServerHttpMessageConvertersCustomizer;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

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

	public ResponseEntity<Map<ReactionType, Integer>> processReactionCreateRequest(
			long recipeId, ReactionCreateRequest request) {
		return ResponseEntity.ok(recipeService.addReactionToRecipe(recipeId, request.reactionType()));
	}

	public ResponseEntity<Map<ReactionType, Integer>> processReactionDeleteRequest(long recipeId) {
		return ResponseEntity.ok(recipeService.removeReactionFromRecipe(recipeId));
	}

	public ResponseEntity<?> processRecipeNoteCreateRequest(
        long accountId, RecipeNoteCreateRequest request) {

		try {
			com.hocuscoquus.hocuscoquus.entity.RecipeNote note =
					recipeService.addRecipeNote(accountId, request);

			com.hocuscoquus.hocuscoquus.model.RecipeNote response =
					new com.hocuscoquus.hocuscoquus.model.RecipeNote(
							note.getRecipeNoteId(),
							note.getRecipe().getRecipeId(),
							note.getAccount().getAccountId(),
							note.getMessage());

			return ResponseEntity.status(HttpStatus.CREATED).body(response);

		} catch (NoSuchElementException e) {
			return ResponseEntity.notFound().build();

		} catch (IllegalArgumentException e) {
			return ResponseEntity.badRequest().build();
		}
	}

	public ResponseEntity<?> addRecipeTag(long accountID, long recipeID, String tagName) {
		RecipeTag addedTag = recipeService.addRecipeTag(accountID, recipeID, tagName);
		RecipeTagRecord response = RecipeTagRecord.getRecord(addedTag);
		return ResponseEntity.ok(response);
	}

	public ResponseEntity<?> deleteRecipeTag(long accountID, long recipeTagID) {
		RecipeTag deletedTag = recipeService.removeRecipeTag(accountID, recipeTagID);
		RecipeTagRecord response = RecipeTagRecord.getRecord(deletedTag);
		return ResponseEntity.ok(response);
	}

	public ResponseEntity<?> deleteRecipeTagFromRecipe(long accountID, long recipeTagID, long recipeID) {
		RecipeTag deletedTag = recipeService.removeRecipeTagFromRecipe(accountID, recipeTagID, recipeID);
		RecipeTagRecord response = RecipeTagRecord.getRecord(deletedTag);
		return ResponseEntity.ok(response);
	}

	public ResponseEntity<?> getRecipeTagsByAccount(long accountID) {
		List<RecipeTag> recipeTags = recipeService.getRecipeTagsByAccount(accountID);
		List<RecipeTagRecord> response = new ArrayList<>(recipeTags.size());
		for (RecipeTag tag : recipeTags)
			response.add(RecipeTagRecord.getRecord(tag));
		return ResponseEntity.ok(response);
	}

	public ResponseEntity<?> getRecipeTag(long accountId, long recipeTagId) {
		RecipeTag tag = recipeService.getRecipeTag(accountId, recipeTagId);
		RecipeTagRecord response = RecipeTagRecord.getRecord(tag);
		return ResponseEntity.ok(response);
	}
}
