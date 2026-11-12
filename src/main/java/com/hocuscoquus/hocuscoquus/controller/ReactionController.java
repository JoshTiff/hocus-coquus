package com.hocuscoquus.hocuscoquus.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hocuscoquus.hocuscoquus.handler.ReactionHandler;
import com.hocuscoquus.hocuscoquus.model.ReactionCreateRequest;
import com.hocuscoquus.hocuscoquus.model.ReactionType;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/recipe")
public class ReactionController {
    private final ReactionHandler reactionHandler;

    public ReactionController(ReactionHandler reactionHandler) {
        this.reactionHandler = reactionHandler;
    }
    
    @PostMapping("/{recipeId}/reactions")
    public ResponseEntity<Map<ReactionType, Integer>> addReactionToRecipe(
            @PathVariable long recipeId,
            @Valid @RequestBody ReactionCreateRequest request) {
        return reactionHandler.processReactionCreateRequest(recipeId, request);
    }

    @DeleteMapping("/{recipeId}/reactions")
    public ResponseEntity<Map<ReactionType, Integer>> removeReactionFromRecipe(@PathVariable long recipeId) {
        return reactionHandler.processReactionDeleteRequest(recipeId);
    }
}
