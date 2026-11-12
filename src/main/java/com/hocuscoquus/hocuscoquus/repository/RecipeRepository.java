package com.hocuscoquus.hocuscoquus.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import com.hocuscoquus.hocuscoquus.entity.Recipe;

public interface RecipeRepository extends JpaRepository<Recipe, Long> {
    Recipe getRecipeByRecipeId(long recipeId);

    List<Recipe> findByAccount_AccountIdOrderByRecipeNameAsc(long accountId);

    Optional<Recipe> findByRecipeIdAndAccount_AccountId(long recipeId, long accountId);
    //List<Recipe> findByRecipeIngredients_Ingredient(List<RecipeIngredient> recipeIngredients);

}
