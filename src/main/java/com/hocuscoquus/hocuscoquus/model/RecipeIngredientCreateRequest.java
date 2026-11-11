package com.hocuscoquus.hocuscoquus.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

public record RecipeIngredientCreateRequest(
		
		@NotBlank
		String ingredient,
		
		@Positive
		double quantity,
		
		String measurement
) {}
