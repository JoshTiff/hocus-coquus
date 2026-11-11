package com.hocuscoquus.hocuscoquus.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hocuscoquus.hocuscoquus.handler.RecipeHandler;
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

}
