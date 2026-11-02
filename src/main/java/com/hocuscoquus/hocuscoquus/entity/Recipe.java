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
	
	@ManyToMany
	@JoinTable(
			name = "recipe_ingredient", 
			joinColumns = @JoinColumn(name = "recipe_id"), 
			inverseJoinColumns = @JoinColumn(name = "ingredient_id"))
    private final List<Ingredient> ingredients = new ArrayList<>();
    
	
	private final List<RecipeTag> recipeTags = new ArrayList<>();
    
	
	private final List<RecipeNote> recipeNotes = new ArrayList<>();
    
	
	private final List<Reaction> reactions = new ArrayList<>();

    @ManyToMany(mappedBy="recipes")
    private List<CalendarEntry> calendarEntries;

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
    
    
    
}
