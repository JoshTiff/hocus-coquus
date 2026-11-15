package com.hocuscoquus.hocuscoquus.model;

import com.hocuscoquus.hocuscoquus.entity.Recipe;
import com.hocuscoquus.hocuscoquus.entity.RecipeIngredient;
import com.hocuscoquus.hocuscoquus.entity.RecipeTag;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
	 * Static builder method to construct a {@link RecipeRecord} from a {@link Recipe}.
	 * @param recipe {@link Recipe} from which to construct the {@link RecipeRecord} object.
	 * @return {@link RecipeRecord} object representing the given {@link Recipe}.
	 */
	public static RecipeRecord getRecord(Recipe recipe) {
		List<RecipeIngredientRecord> ingredientRecords = new ArrayList<>();
		for (RecipeIngredient recipeIngredient : recipe.getRecipeIngredients()) {
			RecipeIngredientRecord ingredientRecord = new RecipeIngredientRecord(
					recipeIngredient.getMeasurement(),
					recipeIngredient.getQuantity(),
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
