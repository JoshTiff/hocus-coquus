package com.hocuscoquus.hocuscoquus.entity;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import com.hocuscoquus.hocuscoquus.model.ReactionType;

import jakarta.persistence.CascadeType;
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
	
	@OneToMany(mappedBy="recipe", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RecipeIngredient> recipeIngredients = new ArrayList<>();
    
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

    private Recipe() {
    	
    }
    
    private Recipe(String recipeName, String recipeDescription, String instructions, String image, List<RecipeIngredient> ingredients, Account account) {
		this.recipeName = recipeName;
		this.recipeDescription = recipeDescription;
		this.instructions = instructions;
		this.image = image;
		this.recipeIngredients = ingredients;
		this.account = account;
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

	public List<RecipeTag> getRecipeTags() {
		return recipeTags;
	}

	public List<RecipeNote> getRecipeNotes() {
		return recipeNotes;
	}

	public List<Reaction> getReactions() {
		return reactions;
	}

	public Map<ReactionType, Integer> computeNumReactions() {
		Map<ReactionType, Integer> counts = new EnumMap<>(ReactionType.class);
		for (ReactionType reactionType : ReactionType.values()) {
			counts.put(reactionType, 0);
		}
		for (Reaction reaction : reactions) {
			counts.merge(reaction.getReactionType(), 1, Integer::sum);
		}
		return counts;
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
	
	public List<RecipeIngredient> getRecipeIngredients() {
		return recipeIngredients;
	}

	public void setRecipeIngredients(List<RecipeIngredient> recipeIngredients) {
		this.recipeIngredients = recipeIngredients;
	}

	//Builder class
    public static RecipeBuilder builder() {
    	return new RecipeBuilder();
    }
    
	public static class RecipeBuilder {

	    private String recipeName;
	    private String recipeDescription;
	    private String instructions;
	    private String image;
	    private List<RecipeIngredient> recipeIngredients = new ArrayList<>();
	    private Account account;
	    

		public RecipeBuilder setRecipeName(String recipeName) {
			this.recipeName = recipeName;
			return this;
		}

		public RecipeBuilder setRecipeDescription(String recipeDescription) {
			this.recipeDescription = recipeDescription;
			return this;
		}

		public RecipeBuilder setInstructions(String instructions) {
			this.instructions = instructions;
			return this;
		}

		public RecipeBuilder setImage(String image) {
			this.image = image;
			return this;
		}

		public RecipeBuilder setRecipeIngredients(List<RecipeIngredient> recipeIngredients) {
			this.recipeIngredients = recipeIngredients;
			return this;
		}

		public RecipeBuilder setAccount(Account account) {
			this.account = account;
			return this;
		}
		
		public Recipe build() {
			return new Recipe(recipeName, recipeDescription, instructions, image, recipeIngredients, account);
		}
	    
	}
	
    
}
