package com.hocuscoquus.hocuscoquus.model;

public class Ingredient {
    
	private long ingredientId;
	private String ingredientName;
	private int quantity;
	private String measurement;
	
	//Getters
	public long getIngredientId() {
		return ingredientId;
	}
	public String getIngredientName() {
		return ingredientName;
	}
	public int getQuantity() {
		return quantity;
	}
	public String getMeasurement() {
		return measurement;
	}
	
	//Setters
	public void setIngredientName(String ingredientName) {
		this.ingredientName = ingredientName;
	}
	
	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}
	
	public void setMeasurement(String measurement) {
		this.measurement = measurement;
	}
	
}
