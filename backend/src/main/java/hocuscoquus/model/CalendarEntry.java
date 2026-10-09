package hocuscoquus.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Represents an entry in a calendar.
 */
public class CalendarEntry {
    
    private int calendarEntryId;
    private Date dateTime;
    private List<Recipe> recipes;

    /**
     * Constructs a new CalendarEntry with a unique ID and date and time.
     * Initializes an empty list of recipes.
     *
     * @param calendarEntryId the unique ID of the calendar entry
     * @param dateTime the date and time associated with the calendar entry
     */
    public CalendarEntry(int calendarEntryId, Date dateTime) {
        this.calendarEntryId = calendarEntryId;
        this.dateTime = dateTime;
        this.recipes = new ArrayList<>();
    }

    /**
     * Returns the unique ID of the calendar entry.
     *
     * @return the calendar entry ID
     */
    public int getCalendarEntryId() {
        return calendarEntryId;
    }

    /**
     * Returns the date and time associated with the calendar entry.
     *
     * @return the date and time of the calendar entry
     */
    public Date getDateTime() {
        return dateTime;
    }

    /**
     * Returns the list of recipes associated with the calendar entry.
     *
     * @return the list of recipes
     */
    public List<Recipe> getRecipes() {
        return recipes;
    }

    /**
     * Updates the date and time of the calendar entry.
     *
     * @param dateTime the new date and time for the calendar entry
     */
    public void setDateTime(Date dateTime) {
        this.dateTime = dateTime;
    }

    /**
     * Adds a recipe to the calendar entry.
     *
     * @param recipe the recipe to add
     */
    public void addRecipe(Recipe recipe) {
        recipes.add(recipe);
    }

    /**
     * Removes the recipe with the matching unique ID from the calendar entry.
     * If no recipe with the given ID exists, the list remains unchanged.
     *
     * @param recipeId the ID of the recipe to remove
     */
    public void removeRecipe(int recipeId) {
        for (int i = 0; i < recipes.size(); i++) {
            if (recipes.get(i).getRecipeId() == recipeId) {
                recipes.remove(i);
                return;
            }
        }
    }

}
