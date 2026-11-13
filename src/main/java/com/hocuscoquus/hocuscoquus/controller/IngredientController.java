package com.hocuscoquus.hocuscoquus.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hocuscoquus.hocuscoquus.model.IngredientCreateRequest;
import com.hocuscoquus.hocuscoquus.handler.IngredientHandler;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/ingredient")
public class IngredientController {

	private final IngredientHandler ingredientHandler;
	public IngredientController(IngredientHandler ingredientHandler) {
		this.ingredientHandler = ingredientHandler;
	}
	
	
	@PostMapping("/create")
	public ResponseEntity<?> createIngredient(@Valid @RequestBody IngredientCreateRequest request) {
		return ingredientHandler.processIngredientCreateRequest(request);
	}
}
