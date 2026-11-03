package com.hocuscoquus.hocuscoquus.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hocuscoquus.hocuscoquus.entity.Ingredient;
import com.hocuscoquus.hocuscoquus.entity.Recipe;

public interface RecipeRepository extends JpaRepository<Recipe, Long> {
    Recipe findById(long recipeId);
    List<Recipe> findByIngredients(List<Ingredient> ingredients);
}
