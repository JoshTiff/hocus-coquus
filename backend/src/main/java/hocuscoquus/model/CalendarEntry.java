package hocuscoquus.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CalendarEntry {
    
    private int calendarEntryId;
    private Date dateTime;
    private List<Recipe> recipes;

    public CalendarEntry(int calendarEntryId, Date dateTime) {
        this.calendarEntryId = calendarEntryId;
        this.dateTime = dateTime;
        this.recipes = new ArrayList<>();
    }

    public int getCalendarEntryId() {
        return calendarEntryId;
    }

    public Date getDateTime() {
        return dateTime;
    }

    public void setDateTime(Date dateTime) {
        this.dateTime = dateTime;
    }

    public List<Recipe> getRecipes() {
        return recipes;
    }

    public void addRecipe(Recipe recipe) {
        recipes.add(recipe);
    }

    public void removeRecipe(int recipeId) {
        for (int i = 0; i < recipes.size(); i++) {
            if (recipes.get(i).getRecipeId() == recipeId) {
                recipes.remove(i);
                return;
            }
        }
    }

}
