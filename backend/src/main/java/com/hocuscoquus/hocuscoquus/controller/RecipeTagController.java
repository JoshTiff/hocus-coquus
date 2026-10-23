package com.hocuscoquus.hocuscoquus.controller;

import com.hocuscoquus.hocuscoquus.model.RecipeTag;
import com.hocuscoquus.hocuscoquus.service.RecipeTagService;

public class RecipeTagController {
    private final RecipeTagService recipeTagService;

    public RecipeTagController(RecipeTagService recipeTagService) {
        this.recipeTagService = recipeTagService;
    }

    public RecipeTag addRecipeTag(RecipeTag recipeTag, String tag) {
        return recipeTagService.addRecipeTag(recipeTag, tag);
    }

    public void removeRecipeTag(long recipeTagId, String tag) {
        recipeTagService.removeRecipeTag(recipeTagId, tag);
    }
}
