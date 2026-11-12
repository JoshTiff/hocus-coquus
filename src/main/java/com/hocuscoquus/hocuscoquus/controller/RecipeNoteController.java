package com.hocuscoquus.hocuscoquus.controller;

import java.util.NoSuchElementException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hocuscoquus.hocuscoquus.entity.AccountDetails;
import com.hocuscoquus.hocuscoquus.entity.RecipeNote;
import com.hocuscoquus.hocuscoquus.model.RecipeNoteCreateRequest;
import com.hocuscoquus.hocuscoquus.service.RecipeNoteService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/recipe-notes")
public class RecipeNoteController {

    private final RecipeNoteService recipeNoteService;

    public RecipeNoteController(RecipeNoteService recipeNoteService) {
        this.recipeNoteService = recipeNoteService;
    }

    @PostMapping
    public ResponseEntity<?> addRecipeNote(
            @AuthenticationPrincipal AccountDetails details,
            @Valid @RequestBody RecipeNoteCreateRequest request) {

        if (details == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        try {
            RecipeNote note = recipeNoteService.addRecipeNote(
                    details.getAccountId(), request);

            com.hocuscoquus.hocuscoquus.model.RecipeNote response =
                    new com.hocuscoquus.hocuscoquus.model.RecipeNote(
                            note.getRecipeNoteId(),
                            note.getRecipe().getRecipeId(),
                            note.getAccount().getAccountId(),
                            note.getMessage());

            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
