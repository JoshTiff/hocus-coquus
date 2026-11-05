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
    	
    	Ingredient ingredient = new Ingredient();
    	ingredient.setIngredientName(ingredientCreateDto.getIngredientName());
    	ingredient.setQuantity(ingredientCreateDto.getQuantity());
    	ingredient.setMeasurement(ingredientCreateDto.getMeasurement());
    	
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
