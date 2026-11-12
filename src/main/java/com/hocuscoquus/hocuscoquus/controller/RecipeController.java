package com.hocuscoquus.hocuscoquus.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.hocuscoquus.hocuscoquus.handler.RecipeHandler;
import com.hocuscoquus.hocuscoquus.model.ReactionCreateRequest;
import com.hocuscoquus.hocuscoquus.model.ReactionType;
import com.hocuscoquus.hocuscoquus.model.RecipeCreateRequest;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/recipe")
public class RecipeController {
    private final RecipeHandler recipeHandler;

    public RecipeController(RecipeHandler recipeHandler) {
        this.recipeHandler = recipeHandler;
    }

    @PostMapping("/create")
    public ResponseEntity<?> saveRecipe(@Valid @RequestBody RecipeCreateRequest request) {
        return recipeHandler.processRecipeCreateRequest(request);
    }

    @GetMapping("/view-recipe/{recipeId}")
    public ResponseEntity<?> viewRecipe(@PathVariable long recipeId) {
        return recipeHandler.getRecipeByID(recipeId);
    }

    @PostMapping("/{recipeId}/reactions")
    public ResponseEntity<Map<ReactionType, Integer>> addReactionToRecipe(
            @PathVariable long recipeId,
            @Valid @RequestBody ReactionCreateRequest request) {
        return recipeHandler.processReactionCreateRequest(recipeId, request);
    }

    @DeleteMapping("/{recipeId}/reactions")
    public ResponseEntity<Map<ReactionType, Integer>> removeReactionFromRecipe(
            @PathVariable long recipeId) {
        return recipeHandler.processReactionDeleteRequest(recipeId);
    }
}
