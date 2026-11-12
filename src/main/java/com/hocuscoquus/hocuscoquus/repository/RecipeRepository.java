package com.hocuscoquus.hocuscoquus.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hocuscoquus.hocuscoquus.entity.Ingredient;
import com.hocuscoquus.hocuscoquus.entity.Recipe;
import com.hocuscoquus.hocuscoquus.entity.RecipeIngredient;

public interface RecipeRepository extends JpaRepository<Recipe, Long> {
    Recipe findById(long recipeId);
    Recipe findByRecipeId(long recipeId);
    //List<Recipe> findByRecipeIngredients_Ingredient(List<RecipeIngredient> recipeIngredients);

}
