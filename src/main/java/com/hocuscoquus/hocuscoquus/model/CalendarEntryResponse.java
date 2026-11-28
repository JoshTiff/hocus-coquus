package com.hocuscoquus.hocuscoquus.model;

import java.time.Instant;
import java.util.List;

import com.hocuscoquus.hocuscoquus.entity.CalendarEntry;

public record CalendarEntryResponse(
        long calendarEntryId,
        long calendarId,
        Instant dateTime,
        List<RecipeSummary> recipes
) {
    public record RecipeSummary(
            long recipeId,
            String recipeName
    ) {
    }

    public static CalendarEntryResponse fromEntity(CalendarEntry entry) {
        return new CalendarEntryResponse(
                entry.getCalendarEntryId(),
                entry.getCalendar().getCalendarId(),
                Instant.ofEpochMilli(entry.getDateTime().getTime()),
                entry.getRecipes().stream()
                        .map(recipe -> new RecipeSummary(
                                recipe.getRecipeId(),
                                recipe.getRecipeName()))
                        .toList()
        );
    }
}