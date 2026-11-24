package com.hocuscoquus.hocuscoquus.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.hocuscoquus.hocuscoquus.entity.DietaryRestriction;
import com.hocuscoquus.hocuscoquus.entity.Ingredient;
import com.hocuscoquus.hocuscoquus.entity.Reaction;
import com.hocuscoquus.hocuscoquus.entity.Recipe;

import jakarta.persistence.LockModeType;

public interface RecipeRepository extends JpaRepository<Recipe, Long> {
    Recipe findById(long recipeId);
    Recipe findByRecipeId(long recipeId);
    Recipe getRecipeByRecipeId(long recipeId);

    List<Recipe> findByAccount_AccountIdOrderByRecipeNameAsc(long accountId);

    Optional<Recipe> findByRecipeIdAndAccount_AccountId(long recipeId, long accountId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Recipe> findForUpdateByRecipeIdAndAccount_AccountId(long recipeId, long accountId);
    @Query("SELECT reaction FROM Reaction reaction WHERE reaction.recipe.recipeId = :recipeId")
    List<Reaction> findByRecipe_RecipeId(@Param("recipeId") long recipeId);

    @Query("SELECT reaction FROM Reaction reaction WHERE reaction.account.accountId = :accountId")
    List<Reaction> findByAccount_AccountId(@Param("accountId") long accountId);

    @Query("SELECT reaction FROM Reaction reaction "
            + "WHERE reaction.recipe.recipeId = :recipeId AND reaction.account.accountId = :accountId")
    Reaction findByRecipe_RecipeIdAndAccount_AccountId(
            @Param("recipeId") long recipeId, @Param("accountId") long accountId);

    @Query("""
    		SELECT r
	        FROM Recipe r
	        JOIN r.recipeIngredients ri
	        WHERE ri.ingredient IN :ingredients
	        GROUP BY r
	        ORDER BY COUNT(ri) DESC
    		""")
    List<Recipe> findByIngredients(@Param("ingredients") List<Ingredient> ingredients);

    @Query("""
            SELECT r
            FROM Recipe r
            WHERE NOT EXISTS (
                SELECT ri
                FROM RecipeIngredient ri
                JOIN ri.ingredient i
                JOIN i.dietaryRestriction dr
                WHERE ri.recipe = r
                  AND dr IN :dietaryRestrictions
            )
              AND r.account IS NOT NULL
            """)
    List<Recipe> findRecipesWithoutDietaryRestrictions(
            @Param("dietaryRestrictions") List<DietaryRestriction> dietaryRestrictions);
	
}
