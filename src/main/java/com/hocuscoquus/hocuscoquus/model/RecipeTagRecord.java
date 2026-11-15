package com.hocuscoquus.hocuscoquus.model;

import com.hocuscoquus.hocuscoquus.entity.Recipe;
import com.hocuscoquus.hocuscoquus.entity.RecipeTag;

import java.util.ArrayList;
import java.util.List;

/**
 * Data transfer object to deliver information about a recipe tag to the user.
 * @param recipeTagId ID of recipe {@link RecipeTag}.
 * @param recipes List of associated {@link RecipeRecord}.
 * @param accountId ID of the {@link com.hocuscoquus.hocuscoquus.entity.Account} that owns the tag.
 * @param tag Tag name.
 */
public record RecipeTagRecord(
        long recipeTagId,
        java.util.List<RecipeRecord> recipes,
        long accountId,
        String tag
) {
    /**
     * Static builder method to construct a {@link RecipeTagRecord} from a {@link RecipeTag}.
     * @param recipeTag {@link RecipeTag} from which to construct the {@link RecipeTagRecord} object.
     * @return {@link RecipeTagRecord} object representing the given {@link RecipeTag}.
     */
    public static RecipeTagRecord getRecord(RecipeTag recipeTag) {
        List<RecipeRecord> recipeRecordList = new ArrayList<>(recipeTag.getRecipes().size());
        for (Recipe recipe : recipeTag.getRecipes()) {
            recipeRecordList.add(RecipeRecord.getRecord(recipe));
        }
        return new RecipeTagRecord(recipeTag.getRecipeTagId(),
                recipeRecordList, recipeTag.getAccount().getAccountId(),
                recipeTag.getTag());
    }
}