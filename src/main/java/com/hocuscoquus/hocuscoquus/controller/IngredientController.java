package com.hocuscoquus.hocuscoquus.controller;

import org.springframework.stereotype.Controller;
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
	public String createIngredient(@ModelAttribute("ingredientCreateDto") IngredientCreateDto ingredientCreateDto) {
		
		ingredientService.createIngredient(ingredientCreateDto);
		
		return "index";
	}
}
