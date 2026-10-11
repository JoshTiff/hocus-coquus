package hocuscoquus.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a set of tags associated with a recipe by an account.
 */
public class RecipeTag {
    private int recipeTagId;
    private int recipeId;
    private int accountId;
    private List<String> tags;

    /**
     * Constructs a new RecipeTag with a unique ID, recipe, account, and
     * an initial list of tags.
     *
     * @param recipeTagId the unique ID of the recipe tag
     * @param recipeId the ID of the recipe the tags belong to
     * @param accountId the ID of the account that created the tags
     * @param tags the initial list of tags
     */
    public RecipeTag(int recipeTagId, int recipeId, int accountId, List<String> tags) {
        this.recipeTagId = recipeTagId;
        this.recipeId = recipeId;
        this.accountId = accountId;
        setRecipeTags(tags);
    }

    /**
     * Returns the unique ID of the recipe tag.
     *
     * @return the recipe tag ID
     */
    public int getRecipeTagId() {
        return this.recipeTagId;
    }

    /**
     * Returns a copy of the list of tags.
     *
     * @return a copy of the list of tags
     */
    public List<String> getRecipeTags() {
        return new ArrayList<>(this.tags);
    }

    /**
     * Replaces the current tags with a copy of the given list.
     *
     * @param tags the new list of tags
     */
    public void setRecipeTags(List<String> tags) {
        if (tags == null) {
            this.tags = new ArrayList<>();
        } else {
            this.tags = new ArrayList<>(tags);
        }
    }

    /**
     * Adds a tag to the list of tags.
     *
     * @param tag the tag to add
     */
    public void addRecipeTag(String tag) {
        if (tag != null && !tag.strip().isEmpty() && !this.tags.contains(tag)) {
            this.tags.add(tag);
        }
    }

    /**
     * Removes the given tag from the list of tags.
     *
     * @param tag the tag to remove
     */
    public void removeRecipeTag(String tag) {
        if (tag != null) {
            this.tags.remove(tag);
        }
    }
}