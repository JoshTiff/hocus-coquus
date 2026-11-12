package com.hocuscoquus.hocuscoquus.model;

import java.util.List;
import java.util.Map;

public record RecipeRecord(
		
		long id,
		String name,
		String description,
		String instructions,
		List<RecipeIngredientRecord> ingredients,
		String image,
		Map<ReactionType, Integer> reactions,
		long accountId
		) {}
