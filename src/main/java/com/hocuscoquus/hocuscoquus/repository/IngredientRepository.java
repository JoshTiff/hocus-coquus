package com.hocuscoquus.hocuscoquus.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hocuscoquus.hocuscoquus.entity.Ingredient;

public interface IngredientRepository extends JpaRepository<Ingredient, Long> {
    Ingredient findById(long ingredientId);
    
    //List<Ingredient> findByRecipes_RecipeId(long recipeId);
    
    List<Ingredient> findByAccounts_AccountId(long accountId);
    
    boolean existsByIngredientName(String name);
    Ingredient findByIngredientName(String ingredientName);
}
