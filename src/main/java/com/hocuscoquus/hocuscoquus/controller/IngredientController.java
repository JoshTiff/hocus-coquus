package com.hocuscoquus.hocuscoquus.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.hocuscoquus.hocuscoquus.model.IngredientCreateDto;
import com.hocuscoquus.hocuscoquus.service.IngredientService;

@Controller
@RequestMapping("/api/ingredient")
public class IngredientController {

	private final IngredientService ingredientService;
	public IngredientController(IngredientService ingredientService) {
		this.ingredientService = ingredientService;
	}
	
	
	@PostMapping("/create")
	public String createIngredient(Model model, @ModelAttribute("ingredientCreateDto") IngredientCreateDto ingredientCreateDto) {
		
		try {
			ingredientService.createIngredient(ingredientCreateDto);
		} catch (IllegalArgumentException ex) {
			model.addAttribute("ingredientCreateError", ex.getMessage());
			model.addAttribute(ingredientCreateDto);
			return "ingredient-create";
		}
		
		
		return "index";
	}
}
