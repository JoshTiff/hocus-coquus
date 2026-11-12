package com.hocuscoquus.hocuscoquus.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hocuscoquus.hocuscoquus.entity.RecipeNote;

public interface RecipeNoteRepository extends JpaRepository<RecipeNote, Long> {

    RecipeNote findById(long recipeNoteId);

    List<RecipeNote> findByAccount_AccountId(long accountId);

    List<RecipeNote> findByRecipe_RecipeId(long recipeId);

    List<RecipeNote> findByAccount_AccountIdAndRecipe_RecipeId(
            long accountId, long recipeId);
}
