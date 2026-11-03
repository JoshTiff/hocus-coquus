package com.hocuscoquus.hocuscoquus.entity;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "ingredient")
public class Ingredient {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long ingredientId;
	
	@Column(nullable = false)
	private String ingredientName;
	
	@Column(nullable = false)
	private int quantity;
	
	@Column
	private String measurement;
	
	
	@ManyToMany
	@JoinTable(
			name = "ingredient_recipe", 
			joinColumns = @JoinColumn(name = "ingredient_id"), 
			inverseJoinColumns = @JoinColumn(name = "recipe_id"))
	private List<Recipe> recipes = new ArrayList<>();
	
	@ManyToMany
	@JoinTable(
			name = "ingredient_account", 
			joinColumns = @JoinColumn(name = "ingredient_id"), 
			inverseJoinColumns = @JoinColumn(name = "account_id"))
	private List<Account> accounts = new ArrayList<>();
	
	@ManyToMany
	@JoinTable(
			name = "ingredient_dietaryRestriction", 
			joinColumns = @JoinColumn(name = "ingredientId"), 
			inverseJoinColumns = @JoinColumn(name = "dietary_restriction_id"))
	private List<DietaryRestriction> dietaryRestriction = new ArrayList<>();

	public Ingredient() {
		
	}
	
	public long getIngredientId() {
		return ingredientId;
	}

	public void setIngredientId(long ingredientId) {
		this.ingredientId = ingredientId;
	}

	public String getIngredientName() {
		return ingredientName;
	}

	public void setIngredientName(String ingredientName) {
		this.ingredientName = ingredientName;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	public String getMeasurement() {
		return measurement;
	}

	public void setMeasurement(String measurement) {
		this.measurement = measurement;
	}

	public List<Recipe> getRecipes() {
		return recipes;
	}

	public void setRecipes(List<Recipe> recipes) {
		this.recipes = recipes;
	}

	public List<DietaryRestriction> getDietaryRestriction() {
		return dietaryRestriction;
	}

	public void setDietaryRestriction(List<DietaryRestriction> dietaryRestriction) {
		this.dietaryRestriction = dietaryRestriction;
	}

	public List<Account> getAccounts() {
		return accounts;
	}

	public void setAccounts(List<Account> accounts) {
		this.accounts = accounts;
	}
	
	
	
	
}
