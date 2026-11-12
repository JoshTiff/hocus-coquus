package com.hocuscoquus.hocuscoquus.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Map;

import org.junit.jupiter.api.Test;

import com.hocuscoquus.hocuscoquus.model.ReactionType;

class RecipeReactionCountTest {

	@Test
	void computesCountsForBothReactionTypes() {
		Recipe recipe = Recipe.builder().setRecipeName("Test recipe").build();
		Reaction positiveOne = reaction(ReactionType.POSITIVE);
		Reaction positiveTwo = reaction(ReactionType.POSITIVE);
		Reaction negative = reaction(ReactionType.NEGATIVE);

		recipe.getReactions().add(positiveOne);
		recipe.getReactions().add(positiveTwo);
		recipe.getReactions().add(negative);

		assertEquals(Map.of(ReactionType.POSITIVE, 2, ReactionType.NEGATIVE, 1),
				recipe.computeNumReactions());
	}

	@Test
	void includesZeroForReactionTypesWithNoReactions() {
		Recipe recipe = Recipe.builder().setRecipeName("Test recipe").build();

		assertEquals(Map.of(ReactionType.POSITIVE, 0, ReactionType.NEGATIVE, 0),
				recipe.computeNumReactions());
	}

	private Reaction reaction(ReactionType type) {
		Reaction reaction = new Reaction();
		reaction.setReactionType(type);
		return reaction;
	}
}
