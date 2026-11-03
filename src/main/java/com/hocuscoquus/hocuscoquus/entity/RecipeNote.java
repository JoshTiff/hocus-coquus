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
@Table(name = "recipeNote")
public class RecipeNote {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long recipeNoteId;
	
	@ManyToOne
	@JoinColumn(name="recipe_id")
    private Recipe recipe;
	
	@ManyToOne
	@JoinColumn(name="account_id")
    private Account account;
    
	@Column(nullable = false)
    private String message;

	public RecipeNote() {
		
	}
	
	public long getRecipeNoteId() {
		return recipeNoteId;
	}

	public void setRecipeNoteId(long recipeNoteId) {
		this.recipeNoteId = recipeNoteId;
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

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
	
	
}
