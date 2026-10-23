package com.hocuscoquus.hocuscoquus.controller;

import com.hocuscoquus.hocuscoquus.model.RecipeNote;
import com.hocuscoquus.hocuscoquus.service.RecipeNoteService;

public class RecipeNoteController {
    private final RecipeNoteService recipeNoteService;

    public RecipeNoteController(RecipeNoteService recipeNoteService) {
        this.recipeNoteService = recipeNoteService;
    }

    public RecipeNote addRecipeNote(String message) {
        return recipeNoteService.addRecipeNote(message);
    }

    public RecipeNote updateRecipeNote(RecipeNote note) {
        return recipeNoteService.updateRecipeNote(note);
    }

    public void deleteRecipeNote(long recipeNoteId) {
        recipeNoteService.deleteRecipeNote(recipeNoteId);
    }
}
