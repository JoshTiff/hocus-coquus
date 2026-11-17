package com.hocuscoquus.hocuscoquus.controller;

import com.hocuscoquus.hocuscoquus.entity.AccountDetails;
import com.hocuscoquus.hocuscoquus.handler.RecipeHandler;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import com.hocuscoquus.hocuscoquus.model.RecipeTag;
import com.hocuscoquus.hocuscoquus.service.RecipeTagService;

@RestController
@RequestMapping("/api/recipe-tags")
public class RecipeTagController {
    private final RecipeHandler recipeHandler;

    public RecipeTagController(RecipeHandler recipeHandler) {
        this.recipeHandler = recipeHandler;
    }

    @PutMapping("/add-tag/{recipeID}")
    public ResponseEntity<?> addRecipeTag(
            @AuthenticationPrincipal AccountDetails details,
            @PathVariable long recipeID,
            @RequestParam String tagName) {
        return recipeHandler.addRecipeTag(details.getAccountId(), recipeID, tagName);
    }

    @DeleteMapping("/delete-tag/{recipeTagId}")
    public ResponseEntity<?> deleteRecipeTag(
            @AuthenticationPrincipal AccountDetails details,
            @PathVariable long recipeTagId,
            @RequestParam(required = false) Long recipeId) {
        if (recipeId == null) // Delete entire recipe tag.
            return recipeHandler.deleteRecipeTag(details.getAccountId(), recipeTagId);
        else // Delete recipe tag from single recipe.
            return recipeHandler.deleteRecipeTagFromRecipe(details.getAccountId(), recipeTagId, recipeId);
    }

    @GetMapping("/view-all")
    public ResponseEntity<?> getRecipeTagsByAccount(@AuthenticationPrincipal AccountDetails details) {
        return recipeHandler.getRecipeTagsByAccount(details.getAccountId());
    }

    @GetMapping("/view-tag/{recipeTagId}")
    public ResponseEntity<?> getRecipeTagsByAccount(
            @AuthenticationPrincipal AccountDetails details,
            @PathVariable long recipeTagId) {
        return recipeHandler.getRecipeTag(details.getAccountId(), recipeTagId);
    }


}
