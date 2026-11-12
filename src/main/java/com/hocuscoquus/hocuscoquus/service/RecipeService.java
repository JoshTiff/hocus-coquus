package com.hocuscoquus.hocuscoquus.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hocuscoquus.hocuscoquus.entity.Account;
import com.hocuscoquus.hocuscoquus.entity.AccountDetails;
import com.hocuscoquus.hocuscoquus.entity.Ingredient;
import com.hocuscoquus.hocuscoquus.entity.Recipe;
import com.hocuscoquus.hocuscoquus.entity.RecipeIngredient;
import com.hocuscoquus.hocuscoquus.model.IngredientCreateDto;
import com.hocuscoquus.hocuscoquus.model.IngredientModel;
import com.hocuscoquus.hocuscoquus.model.ReactionType;
import com.hocuscoquus.hocuscoquus.model.RecipeCreateRequest;
import com.hocuscoquus.hocuscoquus.model.RecipeIngredientCreateRequest;
import com.hocuscoquus.hocuscoquus.repository.IngredientRepository;
import com.hocuscoquus.hocuscoquus.repository.RecipeIngredientRepository;
import com.hocuscoquus.hocuscoquus.repository.RecipeRepository;

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

    	Account account = ((AccountDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getAccount();
    	
    	List<RecipeIngredient> recipeIngredientList = new ArrayList<>();
    	
    	Recipe r = Recipe.builder()
    			.setRecipeName(request.recipeName())
    			.setRecipeDescription(request.recipeDescription())
    			.setInstructions(request.instructions())
    			.setImage(request.image())
    			.setRecipeIngredients(recipeIngredientList)
    			.setAccount(account)
    			.build();
    	 
    	for (RecipeIngredientCreateRequest i : request.ingredients()) {
    		
    		//check if ingredient exists
    		Ingredient ingredient = ingredientRepository.findByIngredientName(i.ingredient().trim().toLowerCase());
    		
    		//if it doesn't, create it
    		if (ingredient == null) {
    			IngredientCreateDto ic = new IngredientCreateDto();
    			ic.setIngredientName(i.ingredient().trim().toLowerCase());
    			ingredient = ingredientService.createIngredient(ic);
    		}
    		
    		//create a new RecipeIngredient
    		RecipeIngredient ri = new RecipeIngredient();
    		ri.setIngredient(ingredient);
    		ri.setMeasurement(i.measurement());
    		ri.setQuantity(i.quantity());
    		ri.setRecipe(r);
    		//add it to our Recipe's Ingredient List
    		r.getRecipeIngredients().add(ri);
    	}
    	
    	
    	return recipeRepository.save(r);
    }


    public List<Recipe> searchRecipes() {
        // TODO: add implementation for this method

        List<Recipe> recipes = new ArrayList<>();
        return recipes;
    }

    public Recipe updateRecipe(Recipe recipe) {
        // TODO: add implementation for this method
        return null;
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
        Recipe recipe = recipeRepository.findByRecipeId(recipeId);
        if (recipe == null) {
            throw new IllegalArgumentException("Recipe not found: " + recipeId);
        }
        return recipe.computeNumReactions().get(reactionType);
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

    public Recipe getRecipeByID(long recipeID) {
        return recipeRepository.getRecipeByRecipeId(recipeID);
    }
}
