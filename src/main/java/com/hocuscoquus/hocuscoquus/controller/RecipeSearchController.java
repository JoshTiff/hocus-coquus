package com.hocuscoquus.hocuscoquus.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.hocuscoquus.hocuscoquus.handler.RecipeSearchHandler;

@RestController
@RequestMapping("/api/recipe")
public class RecipeSearchController {

	private final RecipeSearchHandler recipeSearchHandler;
	public RecipeSearchController(RecipeSearchHandler recipeSearchHandler) {
		this.recipeSearchHandler = recipeSearchHandler;
	}
	
	/**
	 * Used to search recipes by ingredients
	 * @param ingredients - list of ingredients to search by 
	 * @param usePantry - indicates to use the user's ingredients (defaults true)
	 * @return list of recipes that match the user's query parameters
	 */
	@GetMapping("/search-by-ingredients")
	public ResponseEntity<?> searchByIngredients(
			@RequestParam(required = false) List<String> ingredients, 
			@RequestParam(defaultValue = "true") boolean usePantry) {
		return recipeSearchHandler.processSearchByIngredientsRequest(ingredients,usePantry);
	}
	
	
}
