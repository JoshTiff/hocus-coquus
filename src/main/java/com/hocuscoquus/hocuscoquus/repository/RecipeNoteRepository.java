package com.hocuscoquus.hocuscoquus.repository;

import java.util.List;

import com.hocuscoquus.hocuscoquus.model.RecipeNote;

public interface RecipeNoteRepository {
    RecipeNote findById(long recipeNoteId);
    List<RecipeNote> findByAccount(long accountId);
    List<RecipeNote> findByRecipe(long recipeId);
    List<RecipeNote> findByAccountAndRecipe(long accountId, long recipeId);
}
