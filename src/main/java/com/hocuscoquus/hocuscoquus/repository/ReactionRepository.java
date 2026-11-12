package com.hocuscoquus.hocuscoquus.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hocuscoquus.hocuscoquus.entity.Reaction;

public interface ReactionRepository extends JpaRepository<Reaction, Long> {
    List<Reaction> findByRecipe_RecipeId(long recipeId);
    List<Reaction> findByAccount_AccountId(long accountId);
    Reaction findByRecipe_RecipeIdAndAccount_AccountId(long recipeId, long accountId);
}
