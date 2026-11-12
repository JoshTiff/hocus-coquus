package com.hocuscoquus.hocuscoquus.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record RecipeIngredientCreateRequest(
		
		@NotBlank @Size(max = 255)
		String ingredient,
		
		@Positive
		double quantity,
		
		@Size(max = 255) String measurement
) {}
