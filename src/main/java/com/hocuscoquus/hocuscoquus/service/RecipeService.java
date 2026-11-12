package com.hocuscoquus.hocuscoquus.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.hocuscoquus.hocuscoquus.entity.Account;
import com.hocuscoquus.hocuscoquus.entity.AccountDetails;
import com.hocuscoquus.hocuscoquus.entity.Ingredient;
import com.hocuscoquus.hocuscoquus.entity.Recipe;
import com.hocuscoquus.hocuscoquus.entity.RecipeIngredient;
import com.hocuscoquus.hocuscoquus.model.IngredientCreateDto;
import com.hocuscoquus.hocuscoquus.model.IngredientModel;
import com.hocuscoquus.hocuscoquus.model.ReactionType;
import com.hocuscoquus.hocuscoquus.model.RecipeCreateRequest;
import com.hocuscoquus.hocuscoquus.model.RecipeEditRequest;
import com.hocuscoquus.hocuscoquus.model.RecipeIngredientCreateRequest;
import com.hocuscoquus.hocuscoquus.repository.IngredientRepository;
import com.hocuscoquus.hocuscoquus.repository.RecipeIngredientRepository;
import com.hocuscoquus.hocuscoquus.repository.RecipeRepository;
import com.hocuscoquus.hocuscoquus.repository.RecipeRepository.RecipeSummary;

@Service
public class RecipeService {
	
	private final IngredientService ingredientService;
    private final RecipeRepository recipeRepository;
    private final IngredientRepository ingredientRepository;

    // TODO: review/add constructor(s)
    public RecipeService(IngredientService ingredientService, RecipeRepository recipeRepository, RecipeIngredientRepository recipeIngredientRepository, IngredientRepository ingredientRepository) {
    	this.ingredientService = Objects.requireNonNull(ingredientService);
    	this.recipeRepository = Objects.requireNonNull(recipeRepository);
    	this.ingredientRepository = Objects.requireNonNull(ingredientRepository);
    }

    
    @Transactional
    public Recipe createRecipe(RecipeCreateRequest request) {
        Recipe recipe = Recipe.builder()
                .setRecipeName(request.recipeName())
                .setRecipeDescription(request.recipeDescription())
                .setInstructions(request.instructions())
                .setImage(request.image())
                .setAccount(currentAccount())
                .build();

        addIngredients(recipe, request.ingredients());
        return recipeRepository.save(recipe);
    }

    private void addIngredients(Recipe recipe, List<RecipeIngredientCreateRequest> ingredients) {
        for (RecipeIngredientCreateRequest input : ingredients) {
            String name = input.ingredient().trim().toLowerCase();
            Ingredient ingredient = ingredientRepository.findByIngredientName(name);
            if (ingredient == null) {
                IngredientCreateDto dto = new IngredientCreateDto();
                dto.setIngredientName(name);
                ingredient = ingredientService.createIngredient(dto);
            }

            RecipeIngredient recipeIngredient = new RecipeIngredient();
            recipeIngredient.setIngredient(ingredient);
            recipeIngredient.setMeasurement(input.measurement());
            recipeIngredient.setQuantity(input.quantity());
            recipeIngredient.setRecipe(recipe);
            recipe.getRecipeIngredients().add(recipeIngredient);
        }
    }

    public List<Recipe> searchRecipes() {
        // TODO: add implementation for this method

        List<Recipe> recipes = new ArrayList<>();
        return recipes;
    }

    @Transactional(readOnly = true)
    public List<RecipeSummary> listOwnedRecipes() {
        return recipeRepository.findByAccount_AccountIdOrderByRecipeNameAscRecipeIdAsc(
                currentAccount().getAccountId());
    }

    @Transactional(readOnly = true)
    public RecipeEditRequest getRecipeForEdit(long recipeId) {
        Recipe recipe = recipeRepository.findByRecipeIdAndAccount_AccountId(
                recipeId, currentAccount().getAccountId()).orElseThrow(RecipeService::recipeNotFound);
        List<RecipeIngredientCreateRequest> ingredients = recipe.getRecipeIngredients().stream()
                .map(item -> new RecipeIngredientCreateRequest(
                        item.getIngredient().getIngredientName(), item.getQuantity(), item.getMeasurement()))
                .toList();

        return new RecipeEditRequest(recipe.getRecipeName(), recipe.getRecipeDescription(),
                recipe.getInstructions(), recipe.getImage(), recipe.getNumberServings(), ingredients);
    }

    @Transactional
    public void updateRecipe(long recipeId, RecipeEditRequest request) {
        Recipe recipe = recipeRepository.findForUpdateByRecipeIdAndAccount_AccountId(
                recipeId, currentAccount().getAccountId()).orElseThrow(RecipeService::recipeNotFound);

        recipe.setRecipeName(request.recipeName());
        recipe.setRecipeDescription(request.recipeDescription());
        recipe.setInstructions(request.instructions());
        recipe.setImage(request.image());
        recipe.setNumberServings(request.numberServings());
        recipe.getRecipeIngredients().clear();
        addIngredients(recipe, request.ingredients());
        recipeRepository.save(recipe);
    }

    private Account currentAccount() {
        return ((AccountDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal())
                .getAccount();
    }

    private static ResponseStatusException recipeNotFound() {
        return new ResponseStatusException(HttpStatus.NOT_FOUND, "Recipe not found");
    }

    public void deleteRecipe(long recipeId) {
        // TODO: add implementation for this method
    }

    public Recipe getRandomRecipe() {
        // TODO: add implementation for this method
        return null;
    }

    public Recipe likeRecipe(long recipeId, long accountId) {
        // TODO: add implementation for this method
        return null;
    }

    public Recipe dislikeRecipe(long recipeId, long accountId) {
        // TODO: add implementation for this method
        return null;
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

    public List<Recipe> searchRecipesByIngredients(List<IngredientModel> ingredients) {
        // TODO: add implementation for this method

        //return recipeRepository.findByIngredients(List.copyOf(ingredients));
    	return null;
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
