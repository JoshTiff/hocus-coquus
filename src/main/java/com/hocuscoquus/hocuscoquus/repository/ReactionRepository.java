package com.hocuscoquus.hocuscoquus.repository;

import java.util.List;

import com.hocuscoquus.hocuscoquus.model.Reaction;

public interface ReactionRepository {
    Reaction findById(long reactionId);
    List<Reaction> findByRecipe(long recipeId);
    List<Reaction> findByAccount(long accountId);
}
