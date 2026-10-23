package com.hocuscoquus.hocuscoquus.controller;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.hocuscoquus.hocuscoquus.model.Reaction;
import com.hocuscoquus.hocuscoquus.model.ReactionType;
import com.hocuscoquus.hocuscoquus.service.ReactionService;

@RestController
@RequestMapping("/api/recipes")
public class ReactionController {
    private final ReactionService reactionService;

    public ReactionController(ReactionService reactionService) {
        this.reactionService = reactionService;
    }
    
    @PostMapping("/{recipeId}/reactions")
    public Reaction addReactionToRecipe(@PathVariable long recipeId, @RequestParam long accountId, @RequestParam ReactionType type) {
        return reactionService.addReactionToRecipe(recipeId, accountId, type);
    }

    @DeleteMapping("/{recipeId}/reactions")
    public void removeReactionFromRecipe(@PathVariable long recipeId, @RequestParam long accountId) {
        reactionService.removeReactionFromRecipe(recipeId, accountId);
    }
}
