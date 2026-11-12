package com.hocuscoquus.hocuscoquus.repository;

import java.util.List;
import java.util.SequencedCollection;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hocuscoquus.hocuscoquus.entity.RecipeTag;

public interface RecipeTagRepository extends JpaRepository<RecipeTag, Long> {
	
    RecipeTag findById(long recipeTagId);
    List<RecipeTag> findByAccount(long accountId);
    List<RecipeTag> findByRecipe(long recipeId);
    List<RecipeTag> findByAccountAndRecipe(long accountId, long recipeId);
    RecipeTag getByRecipeTagId(long recipeTagId);
    void deleteByRecipeTagId(long recipeTagId);
}
