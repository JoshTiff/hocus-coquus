package com.hocuscoquus.hocuscoquus.service;

import java.util.Objects;

import com.hocuscoquus.hocuscoquus.model.Reaction;
import com.hocuscoquus.hocuscoquus.model.ReactionType;
import com.hocuscoquus.hocuscoquus.model.Recipe;
import com.hocuscoquus.hocuscoquus.repository.ReactionRepository;
import com.hocuscoquus.hocuscoquus.repository.RecipeRepository;

public class ReactionService {
    private final ReactionRepository reactionRepository;
    private final RecipeRepository recipeRepository;

    public ReactionService(ReactionRepository reactionRepository, RecipeRepository recipeRepository) {
        this.reactionRepository = Objects.requireNonNull(reactionRepository);
        this.recipeRepository = Objects.requireNonNull(recipeRepository);
    }

    public Reaction addReactionToRecipe(long recipeId, long accountId, ReactionType type) {
        // Recipe recipe = recipeRepository.findById(recipeId);
        // if (recipe == null) {
        //     throw new IllegalArgumentException("Recipe not found: " + recipeId);
        // }
        // Reaction reaction = new Reaction(0L, recipeId, accountId, type);
        // recipe.getReactions().add(reaction);
        // recipeRepository.save(recipe);
        // return reactionRepository.save(reaction);
        ReactionType pos = ReactionType.POSITIVE;
        return new Reaction(pos);
    }

    public void removeReactionFromRecipe(long recipeId, long accountId) {
        // Reaction reaction = reactionRepository.findByRecipe(recipeId).stream()
        //         .filter(reaction -> reaction.getAccountId() == accountId)
        //         .findFirst()
        //         .orElse(null);
        // if (reaction == null) {
        //     return;
        // }
        // Recipe recipe = recipeRepository.findById(recipeId);
        // if (recipe != null) {
        //     recipe.getReactions().removeIf(existing ->
        //             existing.getReactionId() == reaction.getReactionId());
        //     recipeRepository.save(recipe);
        // }
        // reactionRepository.deleteById(reaction.getReactionId());
    }
}
