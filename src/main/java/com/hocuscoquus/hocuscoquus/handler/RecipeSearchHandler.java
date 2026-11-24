package com.hocuscoquus.hocuscoquus.handler;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

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
	
	public ResponseEntity<?> processSearchByIngredientsRequest(List<String> request, boolean usePantry) {
		try {
			
			//list of ingredients that the searched list of ingredients and the list of ingredients tied to the user's account will be stored in
			List<Ingredient> ingredients = new ArrayList<>();
			
			//if there is a logged in user, add their ingredients to the list/query
			Authentication auth = SecurityContextHolder.getContext().getAuthentication();
			if (usePantry && !(auth instanceof AnonymousAuthenticationToken)) {
				Account act = ((AccountDetails) auth.getPrincipal()).getAccount();
				//have to load account fresh from repository because ingredients is lazy-load
				Account freshAccount = accountService.getAccountByID(act.getAccountId()); 
				
				List<Ingredient> actIngredients = freshAccount.getIngredients();
				ingredients.addAll(actIngredients);
			}
			
			//The user doesn't necessarily need to supply ingredients if they have some saved to their account
			if (null != request && !request.isEmpty()) {
				//ensure ingredients exist
				for(String i : request) {
					ingredients.add(ingredientService.getIngredientByName(
							i.trim().toLowerCase(Locale.ROOT)));
				}
			}
			
			//if ingredients is empty at this point, return a bad request
			if(ingredients.isEmpty()) {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("At least one ingredient must be included");
			}
			
			//search for the recipes with those ingredients
			List<Recipe> recipes = new ArrayList<>();
			recipes = recipeSearchService.searchRecipesByIngredients(ingredients);

			
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

	/**
	 * Searches with every dietary restriction saved to the authenticated account.
	 *
	 * @return matching recipes or an error response when the account is unavailable or
	 *         has no restrictions
	 */
	public ResponseEntity<?> processSearchByDietaryRestrictionsRequest() {
		return processSearchByDietaryRestrictionsRequest(null);
	}

	/**
	 * Searches with one selected restriction, or all account restrictions when the ID is null.
	 *
	 * @param dietaryRestrictionId optional restriction ID; it must belong to the account
	 * @return matching recipes, or an error response for unauthenticated users, missing
	 *         restrictions, or an unowned restriction ID
	 */
	public ResponseEntity<?> processSearchByDietaryRestrictionsRequest(
			Long dietaryRestrictionId) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth == null || !auth.isAuthenticated()
				|| !(auth.getPrincipal() instanceof AccountDetails accountDetails)) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}

		List<DietaryRestriction> accountRestrictions =
				accountService.getDietaryRestrictionsForAccount(accountDetails.getAccountId());
		if (accountRestrictions.isEmpty()) {
			return ResponseEntity.badRequest()
					.body("At least one dietary restriction must be saved to your account");
		}

		List<DietaryRestriction> dietaryRestrictions = accountRestrictions;
		if (dietaryRestrictionId != null) {
			// Only resolve IDs from this account's list to prevent cross-account searches.
			dietaryRestrictions = accountRestrictions.stream()
					.filter(restriction -> restriction.getDietaryRestrictionId()
							== dietaryRestrictionId)
					.findFirst()
					.map(List::of)
					.orElse(null);
			if (dietaryRestrictions == null) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND)
						.body("Dietary restriction not found");
			}
		}

		List<Recipe> recipes =
				recipeSearchService.searchRecipesByDietaryRestrictions(dietaryRestrictions);
		if (recipes.isEmpty()) {
			return ResponseEntity.ok("No recipes found that meet your dietary restrictions");
		}

		List<RecipeRecord> response = new ArrayList<>();
		for (Recipe recipe : recipes) {
			response.add(RecipeRecord.getRecord(recipe));
		}
		return ResponseEntity.ok(response);
	}
}
