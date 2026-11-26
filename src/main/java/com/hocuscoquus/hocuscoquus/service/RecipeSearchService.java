package com.hocuscoquus.hocuscoquus.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.hocuscoquus.hocuscoquus.entity.DietaryRestriction;
import com.hocuscoquus.hocuscoquus.entity.Ingredient;
import com.hocuscoquus.hocuscoquus.entity.Recipe;
import com.hocuscoquus.hocuscoquus.repository.RecipeRepository;

@Service
public class RecipeSearchService {

	private final RecipeRepository recipeRepository;
	
	public RecipeSearchService(RecipeRepository recipeRepository) {
		this.recipeRepository = recipeRepository;
	}
	
	
	/**
	 * This method returns a list of recipes which match the supplied ingredients in descending order of how many of the ingredients were in the recipe
	 * @param ingredients
	 * @return list of recipes matching specified ingredients
	 */
	public List<Recipe> searchRecipesByIngredients(List<Ingredient> ingredients) {
		return recipeRepository.findByIngredients(ingredients);
	}

	/**
	 * Returns recipes that contain no ingredients conflicting with the restrictions.
	 * @param dietaryRestrictions the restrictions to apply
	 * @return recipes that comply with all supplied restrictions
	 */
	public List<Recipe> searchRecipesByDietaryRestrictions(
			List<DietaryRestriction> dietaryRestrictions) {
		return recipeRepository.findRecipesWithoutDietaryRestrictions(dietaryRestrictions);
	}

}
