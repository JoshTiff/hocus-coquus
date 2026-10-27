package com.hocuscoquus.hocuscoquus.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Main Recipe Class for Hocus Coquus
 * Stores information regarding recipe including ingredients, instructions, tags, notes, reactions, and more.
 */
public class Recipe {
    private long recipeID;
    private String recipeName;
    private String recipeDescription;
    private String instructions;
    private String image;
    private final List<Ingredient> ingredients = new ArrayList<>();
    private final List<RecipeTag> recipeTags = new ArrayList<>();
    private final List<RecipeNote> recipeNotes = new ArrayList<>();
    private final List<Reaction> reactions = new ArrayList<>();

    // TODO: review/add constructor(s)
    public Recipe(long recipeID, String recipeName, String recipeDescription, String instructions) {
        this.recipeID = recipeID;
        this.recipeName = recipeName;
        this.recipeDescription = recipeDescription;
        this.instructions = instructions;
        this.image = null;
    }

    public Recipe(long recipeID, String recipeName, String recipeDescription, String instructions, String image) {
        this.recipeID = recipeID;
        this.recipeName = recipeName;
        this.recipeDescription = recipeDescription;
        this.instructions = instructions;
        this.image = image;
    }

    /**
     * @return Recipe's Unique ID
     */
    public long getRecipeID() {
        return recipeID;
    }

    /**
     * @return Recipe's Name
     */
    public String getRecipeName() {
        return recipeName;
    }

    /**
     * Used to set the name of a recipe.
     * @param recipeName New recipe name.
     */
    public void setRecipeName(String recipeName) {
        this.recipeName = recipeName;
    }

    /**
     * @return Recipe's Description
     */
    public String getRecipeDescription() {
        return recipeDescription;
    }


    /**
     * Used to set the description of a recipe.
     * @param recipeDescription New recipe description.
     */
    public void setRecipeDescription(String recipeDescription) {
        this.recipeDescription = recipeDescription;
    }

    /**
     * @return List of ingredient sused in the recipe.
     */
    public List<Ingredient> getIngredients() {
        return ingredients;
    }

    /**
     * Used to set the ingredients of a recipe.
     * @param ingredients New recipe ingredients array.
     */
    public void setIngredients(List<Ingredient> ingredients) {
        this.ingredients.clear();
        this.ingredients.addAll(ingredients);
    }

    /**
     * @return Recipe's instructions.
     */
    public String getInstructions() {
        return instructions;
    }

    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }

    /**
     * @return Recipe image filepath or null if it does not have one.
     */
    public String getImage() {
        return image;
    }

    /**
     * Used to set the filepath for the recipe image.
     * @param image New image filepath.
     */
    public void setImage(String image) {
        this.image = image;
    }

    /**
     * @return Array of RecipeTags associated with the recipe.
     */
    public List<RecipeTag> getRecipeTags() {
        return recipeTags;
    }

    /**
     * @return Array of RecipeNotes associated with the recipe.
     */
    public List<RecipeNote> getRecipeNotes() {
        return recipeNotes;
    }

    /**
     * @return Array of reactions associated with the recipe.
     */
    public List<Reaction> getReactions() {
        return reactions;
    }

}