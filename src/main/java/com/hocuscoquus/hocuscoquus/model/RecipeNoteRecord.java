package com.hocuscoquus.hocuscoquus.model;

import com.hocuscoquus.hocuscoquus.entity.RecipeNote;

/**
 * Data transfer object to deliver information about a recipe note to the user.
 * @param recipeNoteId ID of the {@link RecipeNote}.
 * @param recipeId ID of the recipe the note belongs to.
 * @param accountId ID of the account that owns the note.
 * @param message The note message.
 */
public record RecipeNoteRecord(
        long recipeNoteId,
        long recipeId,
        long accountId,
        String message
) {
    /**
     * Static builder method to construct a {@link RecipeNoteRecord} from a {@link RecipeNote}.
     * @param note {@link RecipeNote} from which to construct the record.
     * @return {@link RecipeNoteRecord} representing the given {@link RecipeNote}.
     */
    public static RecipeNoteRecord getRecord(RecipeNote note) {
        return new RecipeNoteRecord(
                note.getRecipeNoteId(),
                note.getRecipe().getRecipeId(),
                note.getAccount().getAccountId(),
                note.getMessage());
    }
}
