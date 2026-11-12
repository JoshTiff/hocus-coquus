package com.hocuscoquus.hocuscoquus.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hocuscoquus.hocuscoquus.entity.Recipe;

public interface RecipeRepository extends JpaRepository<Recipe, Long> {
    Recipe findById(long recipeId);
    Recipe findByRecipeId(long recipeId);
    Recipe getRecipeByRecipeId(long recipeId);
    //List<Recipe> findByRecipeIngredients_Ingredient(List<RecipeIngredient> recipeIngredients);

}
