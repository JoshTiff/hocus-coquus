package com.hocuscoquus.hocuscoquus.repository;

import java.util.List;

import com.hocuscoquus.hocuscoquus.entity.Ingredient;
import com.hocuscoquus.hocuscoquus.entity.Recipe;

public interface RecipeRepository {
    Recipe findById(long recipeId);
    List<Recipe> findByIngredients(List<Ingredient> ingredients);
}
