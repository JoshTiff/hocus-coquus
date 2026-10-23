package com.hocuscoquus.hocuscoquus.service;

import java.util.List;
import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

import com.hocuscoquus.hocuscoquus.model.Ingredient;
import com.hocuscoquus.hocuscoquus.model.Reaction;
import com.hocuscoquus.hocuscoquus.model.ReactionType;
import com.hocuscoquus.hocuscoquus.model.Recipe;
import com.hocuscoquus.hocuscoquus.model.RecipeTag;
import com.hocuscoquus.hocuscoquus.repository.RecipeRepository;
import com.hocuscoquus.hocuscoquus.repository.RecipeTagRepository;

public class RecipeService {
    private final RecipeRepository recipeRepository;
    private final RecipeTagRepository recipeTagRepository;

    public RecipeService(RecipeRepository recipeRepository, RecipeTagRepository recipeTagRepository) {
        this.recipeRepository = Objects.requireNonNull(recipeRepository);
        this.recipeTagRepository = Objects.requireNonNull(recipeTagRepository);
    }

    public Recipe createRecipe(Recipe recipe) {
        // return recipeRepository.save(Objects.requireNonNull(recipe));
        return new Recipe(0, null, null, null);
    }

    public List<Recipe> searchRecipes() {
        // return recipeRepository.findAll();
        List<Recipe> recipes = new ArrayList<>();
        return recipes;
    }

    public Recipe updateRecipe(Recipe recipe) {
        // return recipeRepository.save(Objects.requireNonNull(recipe));
        return new Recipe(0, null, null, null);
    }

    public void deleteRecipe(long recipeId) {
        // recipeRepository.deleteById(recipeId);
    }

    public Recipe getRandomRecipe() {
        // List<Recipe> recipes = recipeRepository.findAll();
        // return recipes.isEmpty() ? null : recipes.get(ThreadLocalRandom.current().nextInt(recipes.size()));
        return new Recipe(0, null, null, null);
    }

    public Recipe likeRecipe(long recipeId, long accountId) {
        // return addReaction(recipeId, accountId, ReactionType.POSITIVE);
        return new Recipe(0, null, null, null);
    }

    public Recipe dislikeRecipe(long recipeId, long accountId) {
        // return addReaction(recipeId, accountId, ReactionType.NEGATIVE);
        return new Recipe(0, null, null, null);
    }

    public int computeNumReactions(long recipeId, ReactionType reactionType) {
        // Recipe recipe = recipeRepository.findById(recipeId);
        // return recipe == null ? 0 : recipe.computeNumReactions(reactionType);
        return 0;
    }

    public List<Recipe> searchRecipesByIngredients(List<Ingredient> ingredients) {
        return recipeRepository.findByIngredients(List.copyOf(ingredients));
    }

    public void scaleIngredients(long recipeId, long scalar) {
        // Recipe recipe = recipeRepository.findById(recipeId);
        // if (recipe == null) {
        //     return;
        // }
        // for (Ingredient ingredient : recipe.getIngredients()) {
        //     ingredient.setQuantity(Math.toIntExact(Math.multiplyExact(ingredient.getQuantity(), scalar)));
        // }
        // recipeRepository.save(recipe);
    }

    public void downloadRecipe() {

    }

    public void printRecipe() {

    }

    public void shareRecipeBySMS() {

    }
}
