package com.hocuscoquus.hocuscoquus.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.hocuscoquus.hocuscoquus.model.Ingredient;
import com.hocuscoquus.hocuscoquus.model.ReactionType;
import com.hocuscoquus.hocuscoquus.model.Recipe;
import com.hocuscoquus.hocuscoquus.repository.RecipeRepository;
import com.hocuscoquus.hocuscoquus.repository.RecipeTagRepository;

// @Service 
public class RecipeService {
    private final RecipeRepository recipeRepository;
    private final RecipeTagRepository recipeTagRepository;

    // TODO: review/add constructor(s)
    public RecipeService(RecipeRepository recipeRepository, RecipeTagRepository recipeTagRepository) {
        this.recipeRepository = Objects.requireNonNull(recipeRepository);
        this.recipeTagRepository = Objects.requireNonNull(recipeTagRepository);
    }

    public Recipe createRecipe(Recipe recipe) {
        // TODO: add implementation for this method

        return new Recipe(0, null, null, null);
    }

    public List<Recipe> searchRecipes() {
        // TODO: add implementation for this method

        List<Recipe> recipes = new ArrayList<>();
        return recipes;
    }

    public Recipe updateRecipe(Recipe recipe) {
        // TODO: add implementation for this method
        return new Recipe(0, null, null, null);
    }

    public void deleteRecipe(long recipeId) {
        // TODO: add implementation for this method
    }

    public Recipe getRandomRecipe() {
        // TODO: add implementation for this method
        return new Recipe(0, null, null, null);
    }

    public Recipe likeRecipe(long recipeId, long accountId) {
        // TODO: add implementation for this method
        return new Recipe(0, null, null, null);
    }

    public Recipe dislikeRecipe(long recipeId, long accountId) {
        // TODO: add implementation for this method
        return new Recipe(0, null, null, null);
    }

    /**
     * Used to compute the number of reactions in the {@code reactions} array of a specific type.
     * @param reactionType The type of reaction we want to count.
     * @return The number of reactions in {@code reactions} of the specified type.
     */
    public int computeNumReactions(long recipeId, ReactionType reactionType) {
        // TODO: add implementation for this method

        //     int reactionCount = 0;
        //     for (Reaction reaction : reactions) {
        //         if (reaction.getReactionType() == reactionType) {
        //             reactionCount++;
        //         }
        //     }
        //     return reactionCount;

        return 0;
    }

    public List<Recipe> searchRecipesByIngredients(List<Ingredient> ingredients) {
        // TODO: add implementation for this method

        return recipeRepository.findByIngredients(List.copyOf(ingredients));
    }

    public void scaleIngredients(long recipeId, long scalar) {
       // TODO: add implementation for this method
    }

    public void downloadRecipe() {
        // TODO: add implementation for this method
    }

    public void printRecipe() {
        // TODO: add implementation for this method
    }

    public void shareRecipeBySMS() {
        // TODO: add implementation for this method
    }
}
