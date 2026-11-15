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

/** Provides HTTP endpoints for exporting recipes. */
@RestController
@RequestMapping("/api/recipe")
public class RecipeExportController {
    private final RecipeExportService recipeExportService;

    /**
     * Creates the controller with the service that prepares recipe exports.
     *
     * @param recipeExportService service used to build recipe email exports
     */
    public RecipeExportController(RecipeExportService recipeExportService) {
        this.recipeExportService = recipeExportService;
    }

    /**
     * Returns a mailto URI containing the requested recipe and recipient.
     *
     * @param recipeId ID of the recipe to share
     * @param request request containing the recipient's email address
     * @return plain-text mailto URI ready to open in an email application
     */
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
