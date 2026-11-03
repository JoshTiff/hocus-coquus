package com.hocuscoquus.hocuscoquus.entity;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "account")
public class Account {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long accountId;
	
	@Column(nullable = false)
	private String firstName;
	
	@Column(nullable = false)
	private String email;
	
	@Column(nullable = false)
	private String password;

	@OneToMany(mappedBy="account")
	private List<RecipeTag> recipeTags = new ArrayList<>();
	
	@OneToMany(mappedBy="account")
	private List<RecipeNote> recipeNotes = new ArrayList<>();
	
	@OneToMany(mappedBy="account")
	private List<Reaction> reactions = new ArrayList<>();
	
	@OneToMany(mappedBy="account")
	private List<Recipe> recipes = new ArrayList<>();
	
	
	
	public long getAccountId() {
		return accountId;
	}

	public void setAccountId(long accountId) {
		this.accountId = accountId;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public List<RecipeTag> getRecipeTags() {
		return recipeTags;
	}

	public void setRecipeTags(List<RecipeTag> recipeTags) {
		this.recipeTags = recipeTags;
	}

	public List<RecipeNote> getRecipeNotes() {
		return recipeNotes;
	}

	public void setRecipeNotes(List<RecipeNote> recipeNotes) {
		this.recipeNotes = recipeNotes;
	}

	public List<Reaction> getReactions() {
		return reactions;
	}

	public void setReactions(List<Reaction> reactions) {
		this.reactions = reactions;
	}

	public List<Recipe> getRecipes() {
		return recipes;
	}

	public void setRecipes(List<Recipe> recipes) {
		this.recipes = recipes;
	}
	
	

	
}
