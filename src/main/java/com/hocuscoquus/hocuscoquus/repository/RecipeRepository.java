package com.hocuscoquus.hocuscoquus.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

import com.hocuscoquus.hocuscoquus.entity.Recipe;

import jakarta.persistence.LockModeType;

public interface RecipeRepository extends JpaRepository<Recipe, Long> {
    Recipe findById(long recipeId);

    List<RecipeSummary> findByAccount_AccountIdOrderByRecipeNameAscRecipeIdAsc(long accountId);

    Optional<Recipe> findByRecipeIdAndAccount_AccountId(long recipeId, long accountId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Recipe> findForUpdateByRecipeIdAndAccount_AccountId(long recipeId, long accountId);

    interface RecipeSummary {
        long getRecipeId();
        String getRecipeName();
    }
}
