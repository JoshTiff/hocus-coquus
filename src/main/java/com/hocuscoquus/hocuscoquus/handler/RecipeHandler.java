package com.hocuscoquus.hocuscoquus.handler;

import com.hocuscoquus.hocuscoquus.entity.*;
import com.hocuscoquus.hocuscoquus.entity.Account;
import com.hocuscoquus.hocuscoquus.entity.RecipeNote;
import com.hocuscoquus.hocuscoquus.entity.RecipeTag;
import com.hocuscoquus.hocuscoquus.model.*;
import com.hocuscoquus.hocuscoquus.service.RecipeService;
import org.springframework.boot.http.converter.autoconfigure.ServerHttpMessageConvertersCustomizer;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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
    public ResponseEntity<?> processRecipeCreateRequest(Account account, RecipeCreateRequest request) {
    	
    	try {
    		Recipe newRecipe = recipeService.createRecipe(account, request);
    		
    		 
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

	public ResponseEntity<?> getRecipeByID(long recipeID) {
		return getRecipeByID(recipeID, null);
	}

	public ResponseEntity<?> getRecipeByID(long recipeID, Long accountId) {
		return getRecipeByID(recipeID, accountId, 1.0);
	}

	/**
	 * Retrieves a recipe with its ingredient quantities scaled by a scalar.
	 * @param recipeID ID of the recipe to retrieve.
	 * @param accountId ID of the requesting account, or null if unauthenticated.
	 * @param scalar Multiplier applied to each ingredient quantity.
	 * @return ResponseEntity containing the scaled {@link RecipeRecord}.
	 */
	public ResponseEntity<RecipeRecord> getRecipeByID(
			long recipeID, Long accountId, double scalar) {
		return ResponseEntity.ok(
				recipeService.scaleIngredients(recipeID, accountId, scalar));
	}

	public ResponseEntity<?> getRandomRecipe(Long accountID) {
		Recipe recipe = recipeService.getRandomRecipe(accountID);
		return ResponseEntity.ok(RecipeRecord.getRecord(recipe));
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
	
	public ResponseEntity<?> processRecipeNoteUpdateRequest(long accountId, RecipeNoteUpdateRequest request) {
		RecipeNote note = recipeService.updateRecipeNote(accountId, request);
		RecipeNoteRecord response = RecipeNoteRecord.getRecord(note);
		return ResponseEntity.ok(response);
	}

	public ResponseEntity<Void> deleteRecipeNote(long accountId, long recipeNoteId) {
		recipeService.removeRecipeNote(accountId, recipeNoteId);
		return ResponseEntity.noContent().build();
	}

	public ResponseEntity<?> updateRecipeTag(long accountId, long recipeTagId, RecipeTagUpdateRequest request) {
		RecipeTag tag = recipeService.updateRecipeTag(accountId, recipeTagId, request);
		RecipeTagRecord response = RecipeTagRecord.getRecord(tag);
		return ResponseEntity.ok(response);
	}

	public ResponseEntity<?> getSimilarRecipe(long recipeID, Long accountId) {
		List<Recipe> similarRecipes = recipeService.getSimilarRecipes(recipeID, accountId);
		// Return the most similar recipe if the list is not empty.
		if (!similarRecipes.isEmpty()) {
			return ResponseEntity.ok(RecipeRecord.getRecord(similarRecipes.getFirst()));
		}
		// Return the empty list if the list is empty.
		return ResponseEntity.ok(similarRecipes);

	}
}
