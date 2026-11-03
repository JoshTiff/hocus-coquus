package com.hocuscoquus.hocuscoquus.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hocuscoquus.hocuscoquus.entity.RecipeNote;

public interface RecipeNoteRepository extends JpaRepository<RecipeNote, Long> {
    
	RecipeNote findById(long recipeNoteId);
    
    List<RecipeNote> findByAccount(long accountId);
    
    List<RecipeNote> findByRecipe(long recipeId);
    
    List<RecipeNote> findByAccountAndRecipe(long accountId, long recipeId);
}
