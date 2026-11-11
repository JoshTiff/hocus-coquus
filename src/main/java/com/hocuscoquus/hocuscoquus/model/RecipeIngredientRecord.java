package com.hocuscoquus.hocuscoquus.model;


public record RecipeIngredientRecord(
		
		String measurement,
		double quantity,
		String ingredientName,
		long ingredientId
	
		) {

}
