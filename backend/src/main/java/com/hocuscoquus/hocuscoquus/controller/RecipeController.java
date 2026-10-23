package com.hocuscoquus.hocuscoquus.controller;

import java.util.List;

import com.hocuscoquus.hocuscoquus.model.Ingredient;
import com.hocuscoquus.hocuscoquus.model.Recipe;
import com.hocuscoquus.hocuscoquus.service.RecipeService;

public class RecipeController {
    private final RecipeService recipeService;

    public RecipeController(RecipeService recipeService) {
        this.recipeService = recipeService;
    }

    public List<Recipe> getRecipesByIngredients(List<Ingredient> ingredients) {
        return recipeService.searchRecipesByIngredients(ingredients);
    }
}
