package com.hocuscoquus.hocuscoquus.handler;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.hocuscoquus.hocuscoquus.entity.Account;
import com.hocuscoquus.hocuscoquus.entity.AccountDetails;
import com.hocuscoquus.hocuscoquus.entity.DietaryRestriction;
import com.hocuscoquus.hocuscoquus.entity.Ingredient;
import com.hocuscoquus.hocuscoquus.entity.Recipe;
import com.hocuscoquus.hocuscoquus.model.RecipeRecord;
import com.hocuscoquus.hocuscoquus.service.AccountService;
import com.hocuscoquus.hocuscoquus.service.IngredientService;
import com.hocuscoquus.hocuscoquus.service.RecipeSearchService;

@Service
public class RecipeSearchHandler {

	private final RecipeSearchService recipeSearchService;
	private final IngredientService ingredientService;
	private final AccountService accountService;
	
	public RecipeSearchHandler(RecipeSearchService recipeSearchService, IngredientService ingredientService, AccountService accountService) {
		this.recipeSearchService = recipeSearchService;
		this.ingredientService = ingredientService;
		this.accountService = accountService;
	}
	
	public ResponseEntity<?> processSearchByIngredientsRequest(List<String> request, boolean applyRestrictions, boolean usePantry) {
		try {
			
			List<Ingredient> ingredients = new ArrayList<>();
			List<DietaryRestriction> restrictions = new ArrayList<>();
			
			//if there is a logged in user, add their ingredients to the list/query
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
			if ((applyRestrictions || usePantry) && !(auth instanceof AnonymousAuthenticationToken)) {
				Account act = ((AccountDetails) auth.getPrincipal()).getAccount();
				//have to load account fresh from repository because ingredients is lazy-load
				Account freshAccount = accountService.getAccountByID(act.getAccountId()); 
				
				if (usePantry) {
					List<Ingredient> actIngredients = freshAccount.getIngredients();
					ingredients.addAll(actIngredients);
				}
				
			    if (applyRestrictions) {
			    	restrictions.addAll(freshAccount.getDietaryRestrictions());
			    }
			    
			}
			
			//The user doesn't necessarily need to supply ingredients if they have some saved to their account
			if (null != request && !request.isEmpty()) {
				//ensure ingredients exist
				for(String i : request) {
					ingredients.add(ingredientService.getIngredientByName(i.trim().toLowerCase()));
				}
			}
			
			//if ingredients is empty at this point, return a bad request
			if(ingredients.isEmpty()) {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("At least one ingredient must be included");
			}
			
			//search for the recipes with those ingredients
			List<Recipe> recipes = new ArrayList<>();
			if (applyRestrictions) {
				recipes = recipeSearchService.searchRecipesByIngredientsWithRestrictions(ingredients, restrictions);
			} else {
				recipes = recipeSearchService.searchRecipesByIngredients(ingredients);
			}
			
			
			if (recipes.isEmpty()) {
				return ResponseEntity.ok("No recipes found with those ingredients");
			}
			//construct the response
			List<RecipeRecord> response = new ArrayList<>();
			for(Recipe r : recipes) {
				response.add(RecipeRecord.getRecord(r));
			}
			
			return ResponseEntity.ok(response);
		} catch(RuntimeException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
		}
	}
}
