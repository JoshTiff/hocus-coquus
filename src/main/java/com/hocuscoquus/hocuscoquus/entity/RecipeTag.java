package com.hocuscoquus.hocuscoquus.entity;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;

@Entity
@Table(name = "recipeTag")
public class RecipeTag {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long recipeTagId;
	
	@ManyToMany
	@JoinTable(
			name = "recipeTag_recipe", 
			joinColumns = @JoinColumn(name = "recipeTagId"), 
			inverseJoinColumns = @JoinColumn(name = "recipe_id"))
    private List<Recipe> recipes = new ArrayList<>();
	
	@ManyToOne
	@JoinColumn(name="account_id")
    private Account account;
	
	@Column(nullable = false)
    private String tag;

	public RecipeTag() {
		
	}
	
	public long getRecipeTagId() {
		return recipeTagId;
	}

	public void setRecipeTagId(long recipeTagId) {
		this.recipeTagId = recipeTagId;
	}

	public List<Recipe> getRecipes() {
		return recipes;
	}

	public void setRecipes(List<Recipe> recipe) {
		this.recipes = recipe;
	}

	public Account getAccount() {
		return account;
	}

	public void setAccount(Account account) {
		this.account = account;
	}

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}
	
	
}
