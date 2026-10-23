package com.hocuscoquus.hocuscoquus.controller;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hocuscoquus.hocuscoquus.model.RecipeNote;
import com.hocuscoquus.hocuscoquus.service.RecipeNoteService;

@RestController
@RequestMapping("/api/recipe-notes")
public class RecipeNoteController {
    private final RecipeNoteService recipeNoteService;

    public RecipeNoteController(RecipeNoteService recipeNoteService) {
        this.recipeNoteService = recipeNoteService;
    }

    @PostMapping
    public RecipeNote addRecipeNote(@RequestBody String message) {
        return recipeNoteService.addRecipeNote(message);
    }

    @PutMapping
    public RecipeNote updateRecipeNote(@RequestBody RecipeNote note) {
        return recipeNoteService.updateRecipeNote(note);
    }

    @DeleteMapping("/{recipeNoteId}")
    public void deleteRecipeNote(@PathVariable long recipeNoteId) {
        recipeNoteService.deleteRecipeNote(recipeNoteId);
    }
}
