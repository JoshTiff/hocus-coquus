package com.hocuscoquus.hocuscoquus.model;

/**
* Represents a specific reaction on a Recipe.
*/

public class Reaction {
    private ReactionType reactionType;

    /**
    * Constructs a new Reaction with a specified reactionType.
    * @param reactionType the type of reaction.
    */

    public Reaction(ReactionType reactionType){
        this.reactionType = reactionType;
    }

    /**
    * Returns the unique ID of the recipe note.
    * @return the reaction type.
    */

    public ReactionType getReactionType(){
        return this.reactionType;
    }
}
