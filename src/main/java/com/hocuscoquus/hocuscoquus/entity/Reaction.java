package com.hocuscoquus.hocuscoquus.entity;

import com.hocuscoquus.hocuscoquus.model.ReactionType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "reaction")
public class Reaction {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long reactionId;
	
	@ManyToOne
	@JoinColumn(name="recipe_id")
    private Recipe recipe;
    
	@ManyToOne
	@JoinColumn(name="account_id")
	private Account account;
	
	@Column(nullable = false)
    private ReactionType reactionType;

	public long getReactionId() {
		return reactionId;
	}

	public void setReactionId(long reactionId) {
		this.reactionId = reactionId;
	}

	public Recipe getRecipe() {
		return recipe;
	}

	public void setRecipe(Recipe recipe) {
		this.recipe = recipe;
	}

	public Account getAccount() {
		return account;
	}

	public void setAccount(Account account) {
		this.account = account;
	}

	public ReactionType getReactionType() {
		return reactionType;
	}

	public void setReactionType(ReactionType reactionType) {
		this.reactionType = reactionType;
	}
	
	
}
