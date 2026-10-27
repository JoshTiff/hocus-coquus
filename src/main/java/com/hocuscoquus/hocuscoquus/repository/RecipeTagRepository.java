package com.hocuscoquus.hocuscoquus.repository;

import java.util.List;

import com.hocuscoquus.hocuscoquus.model.RecipeTag;

public interface RecipeTagRepository {
    RecipeTag findById(long recipeTagId);
    List<RecipeTag> findByAccount(long accountId);
    List<RecipeTag> findByRecipe(long recipeId);
    List<RecipeTag> findByAccountAndRecipe(long accountId, long recipeId);
}
