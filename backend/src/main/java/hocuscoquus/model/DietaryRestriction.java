package hocuscoquus.model;
import java.util.List;

public class DietaryRestriction {
    
	private long dietaryRestrictionId;
	private String dietaryRestrictionName;
	private List<Ingredient> ingredients;
	
	//Getters
	public long getDietaryRestrictionId() {
		return dietaryRestrictionId;
	}
	public String getDietaryRestrictionName() {
		return dietaryRestrictionName;
	}
	public List<Ingredient> getIngredients() {
		return ingredients;
	}
	
	//Setters
	public void setDietaryRestrictionId(long dietaryRestrictionId) {
		this.dietaryRestrictionId = dietaryRestrictionId;
	}
	public void setDietaryRestrictionName(String dietaryRestrictionName) {
		this.dietaryRestrictionName = dietaryRestrictionName;
	}
	public void setIngredients(List<Ingredient> ingredients) {
		this.ingredients = ingredients;
	}
	public void addIngredient(Ingredient ingredient) {
		this.ingredients.add(ingredient);
	}
	public void removeIngredient(Ingredient ingredient) {
		this.ingredients.remove(ingredient);
	}
	
}