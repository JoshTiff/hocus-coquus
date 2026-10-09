package hocuscoquus.model;

public class Reaction {
    private ReactionType reactionType;

    public Reaction(ReactionType reactionType){
        this.reactionType = reactionType;
    }

    public ReactionType getReactionType(){
        return this.reactionType;
    }

}
