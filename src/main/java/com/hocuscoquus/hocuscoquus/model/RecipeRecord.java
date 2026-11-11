package com.hocuscoquus.hocuscoquus.model;

import java.util.HashMap;
import java.util.List;

import com.hocuscoquus.hocuscoquus.entity.Recipe;

public record RecipeRecord(
		
		long id,
		String name,
		String description,
		String instructions,
		List<RecipeIngredientRecord> ingredients,
		String image,
		HashMap<ReactionTypeRecord, Integer> reactions,
		long accountId
		

		) {}
