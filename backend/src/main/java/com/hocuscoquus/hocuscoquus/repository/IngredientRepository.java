package com.hocuscoquus.hocuscoquus.repository;

import java.util.List;

import com.hocuscoquus.hocuscoquus.model.Ingredient;

public interface IngredientRepository {
    Ingredient findById(long ingredientId);
    List<Ingredient> findByRecipe(long recipeId);
    List<Ingredient> findByAccount(long accountId);
}
