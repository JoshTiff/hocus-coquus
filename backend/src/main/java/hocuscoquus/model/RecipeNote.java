package hocuscoquus.model;

public class RecipeNote {
    private int recipeNoteId;
    private int recipeId;
    private int accountId;
    private String message;

    public RecipeNote(int recipeNoteId, int recipeId, int accountId, String message) {
        this.recipeNoteId = recipeNoteId;
        this.recipeId = recipeId;
        this.accountId = accountId;
        this.message = message;
    }

    public int getRecipeNoteId() {
        return this.recipeNoteId;
    }

    public String getMessage() {
        return this.message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
