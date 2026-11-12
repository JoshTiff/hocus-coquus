package com.hocuscoquus.hocuscoquus.controller;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hocuscoquus.hocuscoquus.handler.RecipeHandler;
import com.hocuscoquus.hocuscoquus.model.RecipeCreateRequest;
import com.hocuscoquus.hocuscoquus.model.RecipeEditRequest;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/recipe")
public class RecipeController {
    private final RecipeHandler recipeHandler;

    public RecipeController(RecipeHandler recipeHandler) {
        this.recipeHandler = recipeHandler;
    }

    @GetMapping("/{recipeId}")
    public ResponseEntity<RecipeEditRequest> getRecipeForEdit(@PathVariable long recipeId) {
        return recipeHandler.processRecipeEditLoad(recipeId);
    }

    @PutMapping(value = "/{recipeId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> updateRecipe(@PathVariable long recipeId,
            @Valid @RequestBody RecipeEditRequest request) {
        return recipeHandler.processRecipeEditRequest(recipeId, request);
    }

    @PostMapping("/create")
    public ResponseEntity<?> saveRecipe(@Valid @RequestBody RecipeCreateRequest request) {
        return recipeHandler.processRecipeCreateRequest(request);
    }

}
