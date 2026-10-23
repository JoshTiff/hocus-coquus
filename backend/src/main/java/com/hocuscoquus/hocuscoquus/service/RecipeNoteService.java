package com.hocuscoquus.hocuscoquus.service;

import java.util.Objects;

import com.hocuscoquus.hocuscoquus.model.RecipeNote;
import com.hocuscoquus.hocuscoquus.repository.RecipeNoteRepository;

// @Service 
public class RecipeNoteService {
    private final RecipeNoteRepository repository;

    // TODO: review/add constructor(s)
    public RecipeNoteService(RecipeNoteRepository repository) {
        this.repository = Objects.requireNonNull(repository);
    }

    public RecipeNote addRecipeNote(String message) {
        // TODO: add implementation for this method
        return new RecipeNote(0, 0, 0, message);
    }

    public RecipeNote updateRecipeNote(RecipeNote note) {
        // TODO: add implementation for this method
        return new RecipeNote(0, 0, 0, null);
    }

    public void deleteRecipeNote(long recipeNoteId) {
        // TODO: add implementation for this method
    }
}
