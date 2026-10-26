package com.hocuscoquus.hocuscoquus.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a set of tags associated with a recipe by an account.
 */
public class RecipeTag {
    private long recipeTagId;
    private long recipeId;
    private long accountId;
    private List<String> tags;

    // TODO: review/add constructor(s)

    /**
     * Constructs a new RecipeTag with a unique ID, recipe, account, and
     * an initial list of tags.
     *
     * @param recipeTagId the unique ID of the recipe tag
     * @param recipeId the ID of the recipe the tags belong to
     * @param accountId the ID of the account that created the tags
     * @param tags the initial list of tags
     */
    public RecipeTag(long recipeTagId, long recipeId, long accountId, List<String> tags) {
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
    public long getRecipeTagId() {
        return this.recipeTagId;
    }

    public long getRecipeId() {
        return recipeId;
    }

    public long getAccountId() {
        return accountId;
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

}