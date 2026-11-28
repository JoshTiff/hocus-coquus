package com.hocuscoquus.hocuscoquus.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PutMapping;

import com.hocuscoquus.hocuscoquus.entity.AccountDetails;
import com.hocuscoquus.hocuscoquus.handler.RecipeHandler;
import com.hocuscoquus.hocuscoquus.model.RecipeNoteCreateRequest;
import com.hocuscoquus.hocuscoquus.model.RecipeNoteUpdateRequest;

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

    @PutMapping("/update")
    public ResponseEntity<?> updateRecipeNote(
            @AuthenticationPrincipal AccountDetails details,
            @Valid @RequestBody RecipeNoteUpdateRequest request) {

        if (details == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        return recipeHandler.processRecipeNoteUpdateRequest(
                details.getAccountId(), request);
    }

    @DeleteMapping("/delete/{recipeNoteId}")
    public ResponseEntity<Void> deleteRecipeNote(
            @AuthenticationPrincipal AccountDetails details,
            @PathVariable long recipeNoteId) {
        if (details == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        return recipeHandler.deleteRecipeNote(details.getAccountId(), recipeNoteId);
    }
}