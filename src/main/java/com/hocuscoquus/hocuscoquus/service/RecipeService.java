package com.hocuscoquus.hocuscoquus.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.hocuscoquus.hocuscoquus.entity.*;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import com.hocuscoquus.hocuscoquus.model.IngredientCreateDto;
import com.hocuscoquus.hocuscoquus.model.IngredientModel;
import com.hocuscoquus.hocuscoquus.model.ReactionType;
import com.hocuscoquus.hocuscoquus.model.RecipeCreateRequest;
import com.hocuscoquus.hocuscoquus.model.RecipeIngredientCreateRequest;
import com.hocuscoquus.hocuscoquus.model.RecipeEditRequest;
import com.hocuscoquus.hocuscoquus.model.RecipeEditRequest.IngredientRow;
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

    public List<Recipe> listOwnedRecipes(long accountId) {
        return recipeRepository.findByAccount_AccountIdOrderByRecipeNameAsc(accountId);
    }

    public Recipe getOwnedRecipe(long recipeId, long accountId) {
        return recipeRepository.findByRecipeIdAndAccount_AccountId(recipeId, accountId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Recipe not found"));
    }

    @Transactional(readOnly = true)
    public RecipeEditRequest loadRecipeForEdit(long recipeId, long accountId) {
        Recipe recipe = getOwnedRecipe(recipeId, accountId);
        List<IngredientRow> ingredients = recipe.getRecipeIngredients().stream()
                .map(row -> new IngredientRow(row.getIngredient().getIngredientName(),
                        row.getQuantity(), row.getMeasurement()))
                .toList();
        return new RecipeEditRequest(recipe.getRecipeName(), recipe.getRecipeDescription(),
                recipe.getInstructions(), recipe.getImage(), ingredients);
    }

    @Transactional
    public void updateRecipe(long recipeId, long accountId, RecipeEditRequest request) {
        Recipe recipe = recipeRepository.findForUpdateByRecipeIdAndAccount_AccountId(recipeId, accountId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Recipe not found"));
        recipe.setRecipeName(request.recipeName());
        recipe.setRecipeDescription(request.recipeDescription());
        recipe.setInstructions(request.instructions());
        if (request.image() != null) {
            recipe.setImage(request.image().isBlank() ? null : request.image().strip());
        }
        if (request.ingredients() != null) {
            replaceRecipeIngredients(recipe, request.ingredients());
        }
    }

    private void replaceRecipeIngredients(Recipe recipe, List<IngredientRow> inputs) {
        List<RecipeIngredient> rows = new ArrayList<>();
        for (IngredientRow input : inputs) {
            if (!Double.isFinite(input.quantity())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid ingredient quantity");
            }
            String name = input.ingredient().strip().toLowerCase();
            Ingredient ingredient = ingredientRepository.findByIngredientName(name);
            if (ingredient == null) {
                IngredientCreateDto dto = new IngredientCreateDto();
                dto.setIngredientName(name);
                ingredient = ingredientService.createIngredient(dto);
            }

            RecipeIngredient row = new RecipeIngredient();
            row.setRecipe(recipe);
            row.setIngredient(ingredient);
            row.setQuantity(input.quantity());
            row.setMeasurement(input.measurement());
            rows.add(row);
        }
        recipe.getRecipeIngredients().clear();
        recipe.getRecipeIngredients().addAll(rows);
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
        Recipe recipe = recipeRepository.getRecipeByRecipeId(recipeId);
        int reactionCount = 0;
        for (Reaction reaction : recipe.getReactions())
            if (reaction.getReactionType() == reactionType)
                reactionCount++;
        return reactionCount;
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
