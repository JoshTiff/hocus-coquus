package com.hocuscoquus.hocuscoquus.model;

/**
* Represents a specific reaction on a Recipe.
*/

public class Reaction {
    private long reactionId;
    private long recipeId;
    private long accountId;
    private ReactionType reactionType;

    // TODO: review/add constructor(s)

    /**
    * Constructs a new Reaction with a specified reactionType.
    * @param reactionType the type of reaction.
    */

    public Reaction(ReactionType reactionType){
        this.reactionType = reactionType;
    }

    public Reaction(long reactionId, long recipeId, long accountId, ReactionType reactionType) {
        this.reactionId = reactionId;
        this.recipeId = recipeId;
        this.accountId = accountId;
        this.reactionType = reactionType;
    }

    public ReactionType getReactionType(){
        return this.reactionType;
    }

    public long getReactionId() {
        return reactionId;
    }

    public long getRecipeId() {
        return recipeId;
    }

    public long getAccountId() {
        return accountId;
    }

    public void setReactionType(ReactionType reactionType) {
        this.reactionType = reactionType;
    }
    
}
