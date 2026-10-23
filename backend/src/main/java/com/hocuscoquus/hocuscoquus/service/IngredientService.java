package com.hocuscoquus.hocuscoquus.service;

import java.util.Objects;

import com.hocuscoquus.hocuscoquus.model.Ingredient;
import com.hocuscoquus.hocuscoquus.repository.IngredientRepository;

public class IngredientService {
    private final IngredientRepository repository;

    public IngredientService(IngredientRepository repository) {
        this.repository = Objects.requireNonNull(repository);
    }

    public Ingredient createIngredient(Ingredient ingredient) {
        // TODO: add implementation for this method
        return new Ingredient();
    }

    public void deleteIngredient(long ingredientId) {
        // TODO: add implementation for this method
    }

    public Ingredient updateIngredient(Ingredient ingredient) {
       // TODO: add implementation for this method
        return new Ingredient();
    }
}
