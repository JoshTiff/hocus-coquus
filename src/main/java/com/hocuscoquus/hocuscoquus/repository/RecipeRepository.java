package com.hocuscoquus.hocuscoquus.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.hocuscoquus.hocuscoquus.entity.Reaction;
import com.hocuscoquus.hocuscoquus.entity.Recipe;

public interface RecipeRepository extends JpaRepository<Recipe, Long> {
    Recipe findById(long recipeId);
    Recipe findByRecipeId(long recipeId);
    Recipe getRecipeByRecipeId(long recipeId);

    @Query("SELECT reaction FROM Reaction reaction WHERE reaction.recipe.recipeId = :recipeId")
    List<Reaction> findByRecipe_RecipeId(@Param("recipeId") long recipeId);

    @Query("SELECT reaction FROM Reaction reaction WHERE reaction.account.accountId = :accountId")
    List<Reaction> findByAccount_AccountId(@Param("accountId") long accountId);

    @Query("SELECT reaction FROM Reaction reaction "
            + "WHERE reaction.recipe.recipeId = :recipeId AND reaction.account.accountId = :accountId")
    Reaction findByRecipe_RecipeIdAndAccount_AccountId(
            @Param("recipeId") long recipeId, @Param("accountId") long accountId);

    //List<Recipe> findByRecipeIngredients_Ingredient(List<RecipeIngredient> recipeIngredients);

}
