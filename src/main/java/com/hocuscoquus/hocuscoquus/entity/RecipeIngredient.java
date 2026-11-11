package com.hocuscoquus.hocuscoquus.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class RecipeIngredient {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long recipeIngredientId;
	
	@ManyToOne
	@JoinColumn(name="recipe_id")
	private Recipe recipe;
	
	@ManyToOne
	@JoinColumn(name = "ingredient_id")
	private Ingredient ingredient;
	
	@Column
	private double quantity;
	@Column
	private String measurement;
	
	public RecipeIngredient() {
		
	}

	
	public long getRecipeIngredientId() {
		return recipeIngredientId;
	}

	public void setRecipeIngredientId(long recipeIngredientId) {
		this.recipeIngredientId = recipeIngredientId;
	}

	public Recipe getRecipe() {
		return recipe;
	}

	public void setRecipe(Recipe recipe) {
		this.recipe = recipe;
	}

	public Ingredient getIngredient() {
		return ingredient;
	}

	public void setIngredient(Ingredient ingredient) {
		this.ingredient = ingredient;
	}

	public double getQuantity() {
		return quantity;
	}

	public void setQuantity(double quantity) {
		this.quantity = quantity;
	}

	public String getMeasurement() {
		return measurement;
	}

	public void setMeasurement(String measurement) {
		this.measurement = measurement;
	}
	
	
	
	
}
