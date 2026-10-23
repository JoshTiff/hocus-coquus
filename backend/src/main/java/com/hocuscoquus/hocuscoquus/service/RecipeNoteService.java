package com.hocuscoquus.hocuscoquus.service;

import java.util.Objects;

import com.hocuscoquus.hocuscoquus.model.RecipeNote;
import com.hocuscoquus.hocuscoquus.repository.RecipeNoteRepository;

public class RecipeNoteService {
    private final RecipeNoteRepository repository;

    public RecipeNoteService(RecipeNoteRepository repository) {
        this.repository = Objects.requireNonNull(repository);
    }

    public RecipeNote addRecipeNote(String message) {
        // return repository.save(Objects.requireNonNull(note));
        return new RecipeNote(0, 0, 0, message);
    }

    public RecipeNote updateRecipeNote(RecipeNote note) {
        // return repository.save(Objects.requireNonNull(note));
        return new RecipeNote(0, 0, 0, null);
    }

    public void deleteRecipeNote(long recipeNoteId) {
        // repository.deleteById(recipeNoteId);
    }
}
