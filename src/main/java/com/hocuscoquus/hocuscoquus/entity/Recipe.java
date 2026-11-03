package com.hocuscoquus.hocuscoquus.entity;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "recipe")
public class Recipe {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long recipeId;
	
	@Column(nullable = false)
    private String recipeName;
	
	@Column
    private String recipeDescription;
	
	@Column
    private String instructions;
	
	@Column
    private String image;
	
	@ManyToMany(mappedBy="recipes")
    private List<Ingredient> ingredients = new ArrayList<>();
    
	@OneToMany(mappedBy="recipe")
	private final List<RecipeTag> recipeTags = new ArrayList<>();
    
	@OneToMany(mappedBy="recipe")
	private final List<RecipeNote> recipeNotes = new ArrayList<>();
    
	@OneToMany(mappedBy="recipe")
	private final List<Reaction> reactions = new ArrayList<>();

    @ManyToMany(mappedBy="recipes")
    private List<CalendarEntry> calendarEntries;
    
    @ManyToOne
    @JoinColumn(name="account_id")
    private Account account;

    public Recipe() {
    	
    }
    
	public long getRecipeId() {
		return recipeId;
	}

	public void setRecipeID(long recipeId) {
		this.recipeId = recipeId;
	}

	public String getRecipeName() {
		return recipeName;
	}

	public void setRecipeName(String recipeName) {
		this.recipeName = recipeName;
	}

	public String getRecipeDescription() {
		return recipeDescription;
	}

	public void setRecipeDescription(String recipeDescription) {
		this.recipeDescription = recipeDescription;
	}

	public String getInstructions() {
		return instructions;
	}

	public void setInstructions(String instructions) {
		this.instructions = instructions;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public List<CalendarEntry> getCalendarEntries() {
		return calendarEntries;
	}

	public void setCalendarEntries(List<CalendarEntry> calendarEntries) {
		this.calendarEntries = calendarEntries;
	}

	public List<Ingredient> getIngredients() {
		return ingredients;
	}

	public List<RecipeTag> getRecipeTags() {
		return recipeTags;
	}

	public List<RecipeNote> getRecipeNotes() {
		return recipeNotes;
	}

	public List<Reaction> getReactions() {
		return reactions;
	}

	public Account getAccount() {
		return account;
	}

	public void setAccount(Account account) {
		this.account = account;
	}

	public void setRecipeId(long recipeId) {
		this.recipeId = recipeId;
	}

	public void setIngredients(List<Ingredient> ingredients) {
		this.ingredients = ingredients;
	}
    
	
    
    
}
