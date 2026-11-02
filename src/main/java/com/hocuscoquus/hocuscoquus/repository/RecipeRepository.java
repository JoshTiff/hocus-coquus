package com.hocuscoquus.hocuscoquus.repository;

import java.util.List;

import com.hocuscoquus.hocuscoquus.model.IngredientModel;
import com.hocuscoquus.hocuscoquus.model.Recipe;

public interface RecipeRepository {
    Recipe findById(long recipeId);
    List<Recipe> findByIngredients(List<IngredientModel> ingredients);
}
