package com.hocuscoquus.hocuscoquus.model;
import java.util.ArrayList;
import java.util.List;

public class DietaryRestriction {
    
	private long dietaryRestrictionId;
	private String dietaryRestrictionName;
	private List<IngredientModel> ingredients = new ArrayList<>();

	// TODO: review/add constructor(s)
	public DietaryRestriction() {
	}
	
	//Getters
	public long getDietaryRestrictionId() {
		return dietaryRestrictionId;
	}
	public String getDietaryRestrictionName() {
		return dietaryRestrictionName;
	}
	public List<IngredientModel> getIngredients() {
		return ingredients;
	}
	
	//Setters
	public void setDietaryRestrictionName(String dietaryRestrictionName) {
		this.dietaryRestrictionName = dietaryRestrictionName;
	}
	
}
