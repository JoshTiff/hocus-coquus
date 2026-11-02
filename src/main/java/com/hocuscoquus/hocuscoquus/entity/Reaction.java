package com.hocuscoquus.hocuscoquus.entity;

import com.hocuscoquus.hocuscoquus.model.ReactionType;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "reaction")
public class Reaction {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long reactionId;
    private long recipeId;
    private long accountId;
    private ReactionType reactionType;
}
