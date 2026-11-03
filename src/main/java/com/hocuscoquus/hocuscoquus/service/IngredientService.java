package com.hocuscoquus.hocuscoquus.service;

import java.util.Objects;

import com.hocuscoquus.hocuscoquus.model.IngredientModel;
import com.hocuscoquus.hocuscoquus.repository.IngredientRepository;

// @Service
public class IngredientService {
    private final IngredientRepository repository;

    public IngredientService(IngredientRepository repository) {
        this.repository = Objects.requireNonNull(repository);
    }

    public IngredientModel createIngredient(IngredientModel ingredient) {
        // TODO: add implementation for this method
        return new IngredientModel();
    }

    public void deleteIngredient(long ingredientId) {
        // TODO: add implementation for this method
    }

    public IngredientModel updateIngredient(IngredientModel ingredient) {
       // TODO: add implementation for this method
        return new IngredientModel();
    }
}
