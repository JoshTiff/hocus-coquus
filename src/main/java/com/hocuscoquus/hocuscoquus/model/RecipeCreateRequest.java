package com.hocuscoquus.hocuscoquus.model;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RecipeCreateRequest(

	@NotBlank(message = "Recipe Name is required")
	String recipeName,
	
	@Size(min = 0, max = 255, message = "Maximum description length is 255 characters")
    String recipeDescription,
    
	@Size(min = 0, max = 255, message = "Maximum instructions length is 255 characters")
    String instructions,
    
    String image,
    
    List<@Valid RecipeIngredientCreateRequest> ingredients
    
) {}
