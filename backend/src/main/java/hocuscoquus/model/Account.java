package hocuscoquus.model;
import java.util.ArrayList;
import java.util.list;
import java.util.HashMap;
import hocuscoquus.model.Recipe;
import hocuscoquus.model.DietaryRestriction;
import hocuscoquus.model.RecipeTag;
import hocuscoquus.model.RecipeNote;
import hocuscoquus.model.Calendar;

public class Account {
    
	private long accountId;
	private String firstName;
	private String email;
	private String password;
	private List<Recipe> recipes;
	private List<Recipe> favorites;
	private List<DietaryRestriction> dietaryrestrictions;
	private HashMap<long, List<RecipeTag>> tags;
	private HashMap<long, List<RecipeNote>> recipeNotes;
	private List<Calendar> calendars;
	
	//Getters
	public long getAccountId() {
		return accountId;
	}
	public String getFirstName() {
		return firstName;
	}
	public String getEmail() {
		return email;
	}
	public String getPassword() {
		return password;
	}
	/*
	public ArrayList<Recipe> getRecipes() {
		return recipes;
	}
	*/
	public List<Recipe> getFavorites() {
		return favorites;
	}
	public List<DietaryRestriction> getDietaryrestrictions() {
		return dietaryrestrictions;
	}
	public HashMap<long, List<RecipeTag>> getTags() {
		return tags;
	}
	public HashMap<long, List<RecipeNote>> getRecipeNotes() {
		return recipeNotes;
	}
	public List<Calendar> getCalendars() {
		return calendars;
	}
	
	//Setters
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	/*
	public void setRecipes(ArrayList<Recipe> recipes) {
		this.recipes = recipes;
	}
	
	public void setFavorites(ArrayList<Recipe> favorites) {
		this.favorites = favorites;
	}
	public void setDietaryrestrictions(ArrayList<DietaryRestriction> dietaryrestrictions) {
		this.dietaryrestrictions = dietaryrestrictions;
	}
	public void setTags(HashMap<int, ArrayList<RecipeTag>> tags) {
		this.tags = tags;
	}
	public void setRecipeNotes(HashMap<int, ArrayList<RecipeNote>> recipeNotes) {
		this.recipeNotes = recipeNotes;
	}
	public void setCalendars(ArrayList<Calendar> calendars) {
		this.calendars = calendars;
	}
	*/
	
	//adds a reaction to the specified recipe
	public void addReactionToRecipe(Recipe recipe) {
		
	}
	
	//removes a reaction from the recipe with the specified Id
	public void removeReactionFromRecipe(long recipeId) {
		
	}
	
	//creates a calendar with the provided name
	public Calendar createCalendar(String name) {
		
	}
	
	//deletes the calendar with the specified Id
	public void deleteCalendar(long calendarID) {
		
	}
	
	//creates a dietary restriction for the calling account with the provided ingredient
	public void createDietaryRestriction(String ingredientName) {
		
	}
	
	//deletes the dietary restriction with the specified Id
	public void deleteDietaryRestriction(long dietaryRestrictionId) {
		
	}
	
	//adds a tag with the provided tag name to a recipe
	public void addTagToRecipe(String tag) {
		
	}
	
	//deletes the recipe tag with the specified Id
	public void deleteRecipeTag(long recipeTagId) {
		
	}
	
	//adds a note to a recipe with the provided message
	public void addRecipeNote(String message) {
		
	}
	
	//deletes the recipe note with the specified Id
	public void deleteRecipeNote(long recipeNoteId) {
		
	}
	
	//deletes the calling account from the hocuscoquus system
	public void deleteAccount() {
		
	}
	
}
