package com.hocuscoquus.hocuscoquus.service;

import java.util.Objects;

import com.hocuscoquus.hocuscoquus.model.RecipeTag;
import com.hocuscoquus.hocuscoquus.repository.RecipeTagRepository;

// @Service 
public class RecipeTagService {
    private final RecipeTagRepository repository;

    // TODO: review/add constructor(s)
    public RecipeTagService(RecipeTagRepository repository) {
        this.repository = Objects.requireNonNull(repository);
    }

    /**
     * Adds a tag to the list of tags.
     *
     * @param tag the tag to add
     */
    public void addRecipeTag(String tag) {
        // if (tag != null && !tag.strip().isEmpty() && !this.tags.contains(tag)) {
        //     this.tags.add(tag);
        // }
    }

    /**
     * Removes the given tag from the list of tags.
     *
     * @param tag the tag to remove
     */
    public void removeRecipeTag(String tag) {
        // if (tag != null) {
        //     this.tags.remove(tag);
        // }
    }

    public RecipeTag updateRecipeTag(RecipeTag recipeTag) {
        // TODO: add implementation for this method
        return new RecipeTag(0, 0, 0, null);
    }
}
