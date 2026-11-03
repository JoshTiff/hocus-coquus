package com.hocuscoquus.hocuscoquus.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "recipeTag")
public class RecipeTag {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long recipeTagId;
	
	@ManyToOne
	@JoinColumn(name="recipe_id")
    private Recipe recipe;
	
	@ManyToOne
	@JoinColumn(name="account_id")
    private Account account;
	
	@Column(nullable = false)
    private String tag;

	
	public long getRecipeTagId() {
		return recipeTagId;
	}

	public void setRecipeTagId(long recipeTagId) {
		this.recipeTagId = recipeTagId;
	}

	public Recipe getRecipe() {
		return recipe;
	}

	public void setRecipe(Recipe recipe) {
		this.recipe = recipe;
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
