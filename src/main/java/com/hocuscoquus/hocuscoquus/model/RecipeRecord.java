package com.hocuscoquus.hocuscoquus.model;

import com.hocuscoquus.hocuscoquus.entity.Recipe;
import com.hocuscoquus.hocuscoquus.entity.RecipeIngredient;
import com.hocuscoquus.hocuscoquus.entity.RecipeTag;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * API representation of a recipe.
 *
 * @param id recipe ID
 * @param name recipe name
 * @param description optional description
 * @param instructions optional preparation instructions
 * @param ingredients recipe ingredients and their saved IDs
 * @param image optional image reference
 * @param reactions reaction counts by type
 * @param accountId ID of the account that owns the recipe
 */
public record RecipeRecord(
		
		long id,
		String name,
		String description,
		String instructions,
		List<RecipeIngredientRecord> ingredients,
		String image,
		Map<ReactionType, Integer> reactions,
		long accountId
		) {

	/**
	 * Static builder method to construct a {@link RecipeRecord} from a
	 * {@link Recipe} with its original ingredient quantities.
	 * @param recipe {@link Recipe} from which to construct the record.
	 * @return {@link RecipeRecord} representing the given recipe and its owning account.
	 */
	public static RecipeRecord getRecord(Recipe recipe) {
		return getRecord(recipe, 1.0);
	}

	/**
	 * Static builder method to construct a {@link RecipeRecord} from a
	 * {@link Recipe} with ingredient quantities multiplied by a scalar.
	 * The original recipe and its ingredient quantities remain unchanged.
	 * @param recipe {@link Recipe} from which to construct the record.
	 * @param scalar Positive, finite multiplier applied to each ingredient quantity.
	 * @return {@link RecipeRecord} representing the given recipe with
	 *         scaled ingredient quantities.
	 */
	public static RecipeRecord getRecord(Recipe recipe, double scalar) {
		List<RecipeIngredientRecord> ingredientRecords = new ArrayList<>();
		for (RecipeIngredient recipeIngredient : recipe.getRecipeIngredients()) {
			RecipeIngredientRecord ingredientRecord = new RecipeIngredientRecord(
					recipeIngredient.getMeasurement(),
					recipeIngredient.getQuantity() * scalar,
					recipeIngredient.getIngredient().getIngredientName(),
					recipeIngredient.getIngredient().
					getIngredientId()
			);
			ingredientRecords.add(ingredientRecord);
		}
		return new RecipeRecord(
				recipe.getRecipeId(),
				recipe.getRecipeName(),
				recipe.getRecipeDescription(),
				recipe.getInstructions(),
				ingredientRecords,
				recipe.getImage(),
				recipe.computeNumReactions(),
				recipe.getAccount().getAccountId()
		);
	}
}
