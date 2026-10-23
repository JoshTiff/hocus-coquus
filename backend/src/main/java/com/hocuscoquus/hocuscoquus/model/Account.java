package com.hocuscoquus.hocuscoquus.model;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class Account {
    
	private long accountId;
	private String firstName;
	private String email;
	private String password;
	private List<Recipe> recipes = new ArrayList<>();
	private List<Recipe> favorites = new ArrayList<>();
	private List<DietaryRestriction> dietaryRestrictions = new ArrayList<>();
	private HashMap<Long, List<RecipeTag>> tags = new HashMap<>();
	private HashMap<Long, List<RecipeNote>> recipeNotes = new HashMap<>();
	private List<Calendar> calendars = new ArrayList<>();
	
	//Getters
	public long getAccountId() {
		return accountId;
	}
	public String getFirstName() {
		return firstName;
	}
	public String getEmail() {
		return email;
	}
	public String getPassword() {
		return password;
	}

	/*
	public List<Recipe> getRecipes() {
		return recipes;
	}
	*/

	public List<Recipe> getFavorites() {
		return favorites;
	}
	public List<DietaryRestriction> getDietaryrestrictions() {
		return dietaryRestrictions;
	}
	public HashMap<Long, List<RecipeTag>> getTags() {
		return tags;
	}
	public HashMap<Long, List<RecipeNote>> getRecipeNotes() {
		return recipeNotes;
	}
	public List<Calendar> getCalendars() {
		return calendars;
	}
	
	//Setters
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	
}
