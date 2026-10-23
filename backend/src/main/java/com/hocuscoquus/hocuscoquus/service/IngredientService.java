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
        // return repository.save(Objects.requireNonNull(ingredient));
        return new Ingredient();
    }

    public void deleteIngredient(long ingredientId) {
        // repository.deleteById(ingredientId);
    }

    public Ingredient updateIngredient(Ingredient ingredient) {
        // return repository.save(Objects.requireNonNull(ingredient));
        return new Ingredient();
    }
}
