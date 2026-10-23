package com.hocuscoquus.hocuscoquus.controller;

import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.hocuscoquus.hocuscoquus.model.RecipeTag;
import com.hocuscoquus.hocuscoquus.service.RecipeTagService;

@Profile("recipeTag") // TODO: remove once activate controller
@RestController
@RequestMapping("/api/recipe-tags")
public class RecipeTagController {
    private final RecipeTagService recipeTagService;

    public RecipeTagController(RecipeTagService recipeTagService) {
        this.recipeTagService = recipeTagService;
    }

    @PostMapping
    public RecipeTag addRecipeTag(@RequestBody RecipeTag recipeTag, @RequestParam String tag) {
        // TODO: implement this method
        return new RecipeTag(0, 0, 0, null);
    }

    @DeleteMapping("/{recipeTagId}")
    public void removeRecipeTag(@PathVariable long recipeTagId, @RequestParam String tag) {
        // TODO: implement this method
    }
}
