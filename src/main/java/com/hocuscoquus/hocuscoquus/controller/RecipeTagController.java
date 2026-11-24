package com.hocuscoquus.hocuscoquus.controller;

import com.hocuscoquus.hocuscoquus.entity.AccountDetails;
import com.hocuscoquus.hocuscoquus.handler.RecipeHandler;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import com.hocuscoquus.hocuscoquus.model.RecipeTag;
import com.hocuscoquus.hocuscoquus.model.RecipeTagUpdateRequest;
import com.hocuscoquus.hocuscoquus.service.RecipeTagService;

import jakarta.validation.Valid;

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
        if (details == null) {
            return ResponseEntity.status(org.springframework.http.HttpStatus.UNAUTHORIZED).build();
        }
        return recipeHandler.addRecipeTag(details.getAccountId(), recipeID, tagName);
    }

    @DeleteMapping("/delete-tag/{recipeTagId}")
    public ResponseEntity<?> deleteRecipeTag(
            @AuthenticationPrincipal AccountDetails details,
            @PathVariable long recipeTagId,
            @RequestParam(required = false) Long recipeId) {
        if (details == null) {
            return ResponseEntity.status(org.springframework.http.HttpStatus.UNAUTHORIZED).build();
        }
        if (recipeId == null) // Delete entire recipe tag.
            return recipeHandler.deleteRecipeTag(details.getAccountId(), recipeTagId);
        else // Delete recipe tag from single recipe.
            return recipeHandler.deleteRecipeTagFromRecipe(details.getAccountId(), recipeTagId, recipeId);
    }

    @GetMapping("/view-all")
    public ResponseEntity<?> getRecipeTagsByAccount(@AuthenticationPrincipal AccountDetails details) {
        if (details == null) {
            return ResponseEntity.status(org.springframework.http.HttpStatus.UNAUTHORIZED).build();
        }
        return recipeHandler.getRecipeTagsByAccount(details.getAccountId());
    }

    @GetMapping("/view-tag/{recipeTagId}")
    public ResponseEntity<?> getRecipeTagsByAccount(
            @AuthenticationPrincipal AccountDetails details,
            @PathVariable long recipeTagId) {
        if (details == null) {
            return ResponseEntity.status(org.springframework.http.HttpStatus.UNAUTHORIZED).build();
        }
        return recipeHandler.getRecipeTag(details.getAccountId(), recipeTagId);
    }

    @PutMapping("/update-tag/{recipeTagId}")
    public ResponseEntity<?> updateRecipeTag(
            @AuthenticationPrincipal AccountDetails details,
            @PathVariable long recipeTagId,
            @Valid @RequestBody RecipeTagUpdateRequest request) {
        if (details == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return recipeHandler.updateRecipeTag(details.getAccountId(), recipeTagId, request);
    }
}
