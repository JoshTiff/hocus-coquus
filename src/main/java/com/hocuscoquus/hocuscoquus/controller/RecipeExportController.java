package com.hocuscoquus.hocuscoquus.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hocuscoquus.hocuscoquus.model.RecipeEmailShareRequest;
import com.hocuscoquus.hocuscoquus.service.RecipeExportService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/recipe")
public class RecipeExportController {
    private final RecipeExportService recipeExportService;

    public RecipeExportController(RecipeExportService recipeExportService) {
        this.recipeExportService = recipeExportService;
    }

    @PostMapping("/{recipeId}/share/email")
    public ResponseEntity<String> shareRecipeByEmail(
            @PathVariable long recipeId,
            @Valid @RequestBody RecipeEmailShareRequest request) {
        String mailtoUri = recipeExportService.shareRecipeByEmail(recipeId, request.recipientEmail());
        return ResponseEntity.ok()
                .contentType(MediaType.TEXT_PLAIN)
                .body(mailtoUri);
    }
}
