package com.hocuscoquus.hocuscoquus.service;

import java.util.Objects;

import org.springframework.stereotype.Service;

import com.hocuscoquus.hocuscoquus.entity.Ingredient;
import com.hocuscoquus.hocuscoquus.model.IngredientCreateDto;
import com.hocuscoquus.hocuscoquus.model.IngredientModel;
import com.hocuscoquus.hocuscoquus.repository.IngredientRepository;

@Service
public class IngredientService {
    private final IngredientRepository ingredientRepository;

    public IngredientService(IngredientRepository ingredientRepository) {
        this.ingredientRepository = Objects.requireNonNull(ingredientRepository);
    }

    public IngredientCreateDto createIngredient(IngredientCreateDto ingredientCreateDto) {
        // TODO: add implementation for this method
    	
    	String name = ingredientCreateDto.getIngredientName().trim().toLowerCase();
    	
    	if ( name == null || name.isEmpty() ) {
    		throw new IllegalArgumentException("Invalid Ingredient Name");
    	}
    	if ( ingredientRepository.existsByIngredientName(name) ) {
    		String msg = "The Ingredient '" + name + "' already exists.";
            throw new IllegalArgumentException(msg);
        }
    	
    	Ingredient ingredient = new Ingredient();
    	ingredient.setIngredientName(name);
    	
    	ingredientRepository.save(ingredient);
    	
        return ingredientCreateDto;
    }

    public void deleteIngredient(long ingredientId) {
        // TODO: add implementation for this method
    }

    public IngredientModel updateIngredient(IngredientModel ingredient) {
       // TODO: add implementation for this method
        return new IngredientModel();
    }
}
