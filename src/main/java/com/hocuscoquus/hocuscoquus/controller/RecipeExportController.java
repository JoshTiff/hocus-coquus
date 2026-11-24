package com.hocuscoquus.hocuscoquus.controller;

import org.springframework.http.ResponseEntity;


import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.hocuscoquus.hocuscoquus.handler.RecipeExportHandler;
import com.hocuscoquus.hocuscoquus.model.RecipeEmailShareRequest;
import com.hocuscoquus.hocuscoquus.service.RecipeExportService;

import jakarta.validation.Valid;

/** Provides HTTP endpoints for exporting recipes. */
@RestController
@RequestMapping("/api/recipe")
public class RecipeExportController {
	
    private final RecipeExportService recipeExportService;
    private final RecipeExportHandler recipeExportHandler;

    /**
     * Creates the controller with the service that prepares recipe exports.
     *
     * @param recipeExportService service used to build recipe email exports
     */
    public RecipeExportController(RecipeExportService recipeExportService, RecipeExportHandler recipeExportHandler) {
        this.recipeExportService = recipeExportService;
        this.recipeExportHandler = recipeExportHandler;
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


    /**
     * Returns a printable HTML page containing the requested recipe.
     *
     * @param recipeId ID of the recipe to print
     * @return HTML page with the recipe name, ingredients, and instructions
     */
    @GetMapping("/{recipeId}/print")
    public ResponseEntity<String> printRecipe(@PathVariable long recipeId) {
        String printHtml = recipeExportService.printRecipe(recipeId);
        return ResponseEntity.ok()
                .contentType(MediaType.TEXT_HTML)
                .body(printHtml);
	}


    /**
     * Returns a byte array response that downloads a PDF of the recipe to the user's computer
     * @param recipeId
     * @return pdf download
     */
    @GetMapping("/download")
    public ResponseEntity<byte[]> downloadRecipe(@RequestParam Long recipeId) {
    	return recipeExportHandler.processRecipeDownloadRequest(recipeId);
    }
}
