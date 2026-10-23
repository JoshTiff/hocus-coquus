package com.hocuscoquus.hocuscoquus.controller;

import java.util.List;

import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.hocuscoquus.hocuscoquus.model.Ingredient;
import com.hocuscoquus.hocuscoquus.model.Recipe;
import com.hocuscoquus.hocuscoquus.service.RecipeService;

@Profile("recipe") // TODO: remove once activate controller
@RestController
@RequestMapping("/api/recipes")
public class RecipeController {
    private final RecipeService recipeService;

    public RecipeController(RecipeService recipeService) {
        this.recipeService = recipeService;
    }

    @GetMapping("/search/ingredients")
    public List<Recipe> getRecipesByIngredients(@RequestParam List<Ingredient> ingredients) {
        return recipeService.searchRecipesByIngredients(ingredients);
    }
}
