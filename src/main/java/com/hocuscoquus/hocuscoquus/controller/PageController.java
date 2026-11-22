package com.hocuscoquus.hocuscoquus.controller;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.hocuscoquus.hocuscoquus.entity.AccountDetails;
import com.hocuscoquus.hocuscoquus.model.IngredientCreateDto;
import com.hocuscoquus.hocuscoquus.service.RecipeService;

@Controller
public class PageController {
	private final RecipeService recipeService;

	public PageController(RecipeService recipeService) {
		this.recipeService = recipeService;
	}

	@GetMapping({ "/", "/index" })
	public String index() {
		return "index";
	}

	@GetMapping("/signup")
	public String signup() {
		return "signup";
	}

	@GetMapping("/account")
	public String account() {
		return "account";
	}

	@GetMapping("/account/delete")
	public String deleteAccount() {
		return "account-delete";
	}

	@GetMapping("/account/liked")
	public String likedRecipes() {
		return "account-liked";
	}

	@GetMapping("/account/tagged")
	public String taggedRecipes() {
		return "account-tagged";
	}

	@GetMapping("/search")
	public String search() {
		return "search";
	}

	@GetMapping("/recipe/{recipeId}")
	public String viewRecipe(@PathVariable long recipeId,
			@AuthenticationPrincipal AccountDetails account,
			Model model) {
		model.addAttribute("recipe", recipeService.getRecipeByID(
				recipeId, account == null ? null : account.getAccountId()));
		model.addAttribute("recipeId", recipeId);
		return "view-recipe";
	}

	@GetMapping("/recipe/random")
	public String viewRandomRecipe(@AuthenticationPrincipal AccountDetails account,
			 Model model) {
		model.addAttribute("recipe", recipeService.getRandomRecipe(
				 account == null ? null : account.getAccountId()));
		return "view-recipe";
	}

	@GetMapping("/login")
	public String login() {
		return "login";
	}

	@GetMapping("/404")
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public String notFound() {
		return "error/404";
	}

	@GetMapping("/calendar-create")
	public String createCalendar() {
		return "calendar-create";
	}
	
	@GetMapping("/ingredient-create")
	public String createIngredient(Model model) {
		model.addAttribute("ingredientCreateDto", new IngredientCreateDto());
		return "ingredient-create";
	}
}
