package hocuscoquus.model;

import java.util.ArrayList;
import java.util.List;

public class RecipeTag {
    private int recipeTagId;
    private int recipeId;
    private int accountId;
    private List<String> tags;

    public RecipeTag(int recipeTagId, int recipeId, int accountId, List<String> tags) {
        this.recipeTagId = recipeTagId;
        this.recipeId = recipeId;
        this.accountId = accountId;
        setRecipeTags(tags);
    }

    public int getRecipeTagId() {
        return this.recipeTagId;
    }

    public List<String> getRecipeTags() {
        return new ArrayList<>(this.tags);
    }

    public void setRecipeTags(List<String> tags) {
        if (tags == null) {
            this.tags = new ArrayList<>();
        } else {
            this.tags = new ArrayList<>(tags);
        }
    }

    public void addRecipeTag(String tag) {
        if (tag != null && !tag.strip().isEmpty() && !this.tags.contains(tag)) {
            this.tags.add(tag);
        }
    }

    public void removeRecipeTag(String tag) {
        if (tag != null) {
            this.tags.remove(tag);
        }
    }
}
