package com.hocuscoquus.hocuscoquus.entity;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "dietaryRestriction")
public class DietaryRestriction {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long dietaryRestrictionId;
	
	@Column(nullable = false)
	private String dietaryRestrictionName;
	
	@ManyToOne
	@JoinColumn(name="account_id")
	private Account account;
	
	@ManyToMany(mappedBy="dietaryRestriction")
	private List<Ingredient> ingredients = new ArrayList<>();

	public DietaryRestriction() {
		
	}
	
	public long getDietaryRestrictionId() {
		return dietaryRestrictionId;
	}

	public void setDietaryRestrictionId(long dietaryRestrictionId) {
		this.dietaryRestrictionId = dietaryRestrictionId;
	}

	public String getDietaryRestrictionName() {
		return dietaryRestrictionName;
	}

	public void setDietaryRestrictionName(String dietaryRestrictionName) {
		this.dietaryRestrictionName = dietaryRestrictionName;
	}

	public List<Ingredient> getIngredients() {
		return ingredients;
	}

	public void setIngredients(List<Ingredient> ingredients) {
		this.ingredients = ingredients;
	}
	
	
}
