package com.hocuscoquus.hocuscoquus.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hocuscoquus.hocuscoquus.entity.AccountDetails;
import com.hocuscoquus.hocuscoquus.handler.RecipeHandler;
import com.hocuscoquus.hocuscoquus.model.RecipeNoteCreateRequest;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/recipe-note")
public class RecipeNoteController {

    private final RecipeHandler recipeHandler;

    public RecipeNoteController(RecipeHandler recipeHandler) {
        this.recipeHandler = recipeHandler;
    }

    @PostMapping("/create")
    public ResponseEntity<?> addRecipeNote(
            @AuthenticationPrincipal AccountDetails details,
            @Valid @RequestBody RecipeNoteCreateRequest request) {

        if (details == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        return recipeHandler.processRecipeNoteCreateRequest(
                details.getAccountId(), request);
    }
}