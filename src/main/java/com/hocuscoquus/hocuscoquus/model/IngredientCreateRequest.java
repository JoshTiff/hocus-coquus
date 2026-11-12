package com.hocuscoquus.hocuscoquus.model;

import jakarta.validation.constraints.NotBlank;

public record IngredientCreateRequest(
		@NotBlank
		String name
		) {}
