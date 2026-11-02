package com.hocuscoquus.hocuscoquus.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "recipeNote")
public class RecipeNote {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long recipeNoteId;
    private long recipeId;
    private long accountId;
    private String message;
}
