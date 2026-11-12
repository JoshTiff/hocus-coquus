package com.hocuscoquus.hocuscoquus.entity;

import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

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
    private List<Recipe> recipes;
	
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
