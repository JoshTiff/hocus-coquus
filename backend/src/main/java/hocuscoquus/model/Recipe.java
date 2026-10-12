package hocuscoquus.model;

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
     * Used to set the ID of a recipe.
     * @param recipeID New recipe ID.
     */
    public void setRecipeID(long recipeID) {
        this.recipeID = recipeID;
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
     * Used to set the array of RecipeTags associated with the recipe.
     * @param recipeTags New array of RecipeTags to be associated with the recipe.
     */
    public void setRecipeTags(List<RecipeTag> recipeTags) {
        this.recipeTags.clear();
        this.recipeTags.addAll(recipeTags);
    }

    /**
     * @return Array of RecipeNotes associated with the recipe.
     */
    public List<RecipeNote> getRecipeNotes() {
        return recipeNotes;
    }

    /**
     * Used to set the list of recipe notes associated with the recipe.
     * @param recipeNotes New list of RecipeNotes to be associated with the recipe.
     */
    public void setRecipeNotes(List<RecipeNote> recipeNotes) {
        this.recipeNotes.clear();
        this.recipeNotes.addAll(recipeNotes);
    }

    /**
     * @return Array of reactions associated with the recipe.
     */
    public List<Reaction> getReactions() {
        return reactions;
    }

    /**
     * Used to set the array of reactions associated with the recipe.
     * @param reactions New array of reactions to be associated with the recipe.
     */
    public void setReactions(List<Reaction> reactions) {
        this.reactions.clear();
        this.reactions.addAll(reactions);
    }

    /**
     * Used to compute the number of reactions in the {@code reactions} array of a specific type.
     * @param reactionType The type of reaction we want to count.
     * @return The number of reactions in {@code reactions} of the specified type.
     */
    public int computeNumReactions(ReactionType reactionType) {
        int reactionCount = 0;
        for (Reaction reaction : reactions) {
            // TODO if reactionType == reaction.getType(), increment the count.
        }
        return reactionCount;
    }
}