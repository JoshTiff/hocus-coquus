package hocuscoquus.model;

/**
 * Represents a note written about a recipe by a specific account.
 */
public class RecipeNote {
    private Long recipeNoteId;
    private Long recipeId;
    private Long accountId;
    private String message;

    /**
     * Constructs a new RecipeNote with a unique ID, recipe, account, and
     * and the note message.
     *
     * @param recipeNoteId the unique ID of the recipe note
     * @param recipeId the ID of the recipe the note belongs to
     * @param accountId the ID of the account that created the note
     * @param message the message of the note
     */
    public RecipeNote(Long recipeNoteId, Long recipeId, Long accountId, String message) {
        this.recipeNoteId = recipeNoteId;
        this.recipeId = recipeId;
        this.accountId = accountId;
        this.message = message;
    }

    /**
     * Returns the unique ID of the recipe note.
     *
     * @return the recipe note ID
     */
    public Long getRecipeNoteId() {
        return this.recipeNoteId;
    }

    /**
     * Returns the message of the note.
     *
     * @return the note message
     */
    public String getMessage() {
        return this.message;
    }

    /**
     * Updates the message of the note.
     *
     * @param message the new message for the note
     */
    public void setMessage(String message) {
        this.message = message;
    }
}