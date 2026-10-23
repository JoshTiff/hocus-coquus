package com.hocuscoquus.hocuscoquus.service;

import java.util.Objects;

import com.hocuscoquus.hocuscoquus.model.RecipeTag;
import com.hocuscoquus.hocuscoquus.repository.RecipeTagRepository;

public class RecipeTagService {
    private final RecipeTagRepository repository;

    public RecipeTagService(RecipeTagRepository repository) {
        this.repository = Objects.requireNonNull(repository);
    }

    public RecipeTag addRecipeTag(RecipeTag recipeTag, String tag) {
        // Objects.requireNonNull(recipeTag).addRecipeTag(tag);
        // return repository.save(recipeTag);
        return new RecipeTag(0, 0, 0, null);
    }

    public void removeRecipeTag(long recipeTagId, String tag) {
        // RecipeTag recipeTag = repository.findById(recipeTagId);
        // if (recipeTag == null) {
        //     throw new IllegalArgumentException("Recipe tag not found: " + recipeTagId);
        // }
        // recipeTag.removeRecipeTag(tag);
        // repository.save(recipeTag);
    }

    public RecipeTag updateRecipeTag(RecipeTag recipeTag) {
        // return repository.save(Objects.requireNonNull(recipeTag));
        return new RecipeTag(0, 0, 0, null);
    }
}
