package com.hocuscoquus.hocuscoquus.handler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.hocuscoquus.hocuscoquus.entity.Ingredient;
import com.hocuscoquus.hocuscoquus.model.IngredientCreateRequest;
import com.hocuscoquus.hocuscoquus.model.IngredientRecord;
import com.hocuscoquus.hocuscoquus.service.IngredientService;

@Service
public class IngredientHandler {

	private final IngredientService ingredientService;
	public IngredientHandler(IngredientService ingredientService) {
		this.ingredientService = ingredientService;
	}
	
	
	public ResponseEntity<?> processIngredientCreateRequest(IngredientCreateRequest request) {
		
		try {
			Ingredient newIngredient = new Ingredient();
			newIngredient.setIngredientName(request.name().trim().toLowerCase());
			newIngredient = ingredientService.createIngredient(newIngredient);
			IngredientRecord response = new IngredientRecord(newIngredient.getIngredientId(), newIngredient.getIngredientName());
			
			return ResponseEntity.ok(response);
		} catch(IllegalArgumentException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
		}
		
	}
}
