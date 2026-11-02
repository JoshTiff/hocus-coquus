package com.hocuscoquus.hocuscoquus.entity;

import java.util.ArrayList;
import java.util.List;

import com.hocuscoquus.hocuscoquus.model.IngredientModel;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "dietaryRestriction")
public class DietaryRestriction {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long dietaryRestrictionId;
	private String dietaryRestrictionName;
	private List<IngredientModel> ingredients = new ArrayList<>();
}
