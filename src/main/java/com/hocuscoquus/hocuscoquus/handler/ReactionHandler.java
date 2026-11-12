package com.hocuscoquus.hocuscoquus.handler;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.hocuscoquus.hocuscoquus.model.ReactionCreateRequest;
import com.hocuscoquus.hocuscoquus.model.ReactionType;
import com.hocuscoquus.hocuscoquus.service.ReactionService;

@Service
public class ReactionHandler {
	private final ReactionService reactionService;

	public ReactionHandler(ReactionService reactionService) {
		this.reactionService = reactionService;
	}

	public ResponseEntity<Map<ReactionType, Integer>> processReactionCreateRequest(
			long recipeId, ReactionCreateRequest request) {
		return ResponseEntity.ok(reactionService.addReactionToRecipe(recipeId, request.reactionType()));
	}

	public ResponseEntity<Map<ReactionType, Integer>> processReactionDeleteRequest(long recipeId) {
		return ResponseEntity.ok(reactionService.removeReactionFromRecipe(recipeId));
	}
}
