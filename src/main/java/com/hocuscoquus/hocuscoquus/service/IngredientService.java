package com.hocuscoquus.hocuscoquus.service;

import java.util.Locale;
import java.util.Objects;

import org.springframework.stereotype.Service;

import com.hocuscoquus.hocuscoquus.entity.Ingredient;
import com.hocuscoquus.hocuscoquus.model.IngredientModel;
import com.hocuscoquus.hocuscoquus.repository.IngredientRepository;

@Service
public class IngredientService {
    private final IngredientRepository ingredientRepository;

    public IngredientService(IngredientRepository ingredientRepository) {
        this.ingredientRepository = Objects.requireNonNull(ingredientRepository);
    }

    public Ingredient createIngredient(Ingredient ingredient) {
        if (ingredient == null || ingredient.getIngredientName() == null) {
            throw new IllegalArgumentException("Invalid Ingredient Name");
        }

        String name = ingredient.getIngredientName().trim().toLowerCase(Locale.ROOT);
        if (name.isEmpty()) {
            throw new IllegalArgumentException("Invalid Ingredient Name");
        }
        ingredient.setIngredientName(name);

        if (ingredientRepository.existsByIngredientName(name)) {
            String msg = "The Ingredient '" + name + "' already exists.";
            throw new IllegalArgumentException(msg);
        }

        return ingredientRepository.save(ingredient);
    }

    public void deleteIngredient(long ingredientId) {
        // TODO: add implementation for this method
    }

    public IngredientModel updateIngredient(IngredientModel ingredient) {
       // TODO: add implementation for this method
        return new IngredientModel();
    }
    
    public Ingredient getIngredientByName(String name) {
    	Ingredient i = ingredientRepository.findByIngredientName(name);
    	if (i == null) {
    		throw new RuntimeException("Ingredient '" + name + "'not found");
    	}
    	return i;
    }
}
