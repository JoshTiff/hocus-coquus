package com.hocuscoquus.hocuscoquus.controller;

import com.hocuscoquus.hocuscoquus.model.Reaction;
import com.hocuscoquus.hocuscoquus.model.ReactionType;
import com.hocuscoquus.hocuscoquus.service.ReactionService;

public class ReactionController {
    private final ReactionService reactionService;

    public ReactionController(ReactionService reactionService) {
        this.reactionService = reactionService;
    }

    public Reaction addReactionToRecipe(long recipeId, long accountId, ReactionType type) {
        return reactionService.addReactionToRecipe(recipeId, accountId, type);
    }

    public void removeReactionFromRecipe(long recipeId, long accountId) {
        reactionService.removeReactionFromRecipe(recipeId, accountId);
    }
}
