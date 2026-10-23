package com.hocuscoquus.hocuscoquus.service;

import java.util.Objects;

import com.hocuscoquus.hocuscoquus.model.CalendarEntry;
import com.hocuscoquus.hocuscoquus.model.Recipe;
import com.hocuscoquus.hocuscoquus.repository.CalendarEntryRepository;

public class CalendarEntryService {
    private final CalendarEntryRepository repository;

    public CalendarEntryService(CalendarEntryRepository repository) {
        this.repository = Objects.requireNonNull(repository);
    }

    public CalendarEntry addRecipe(long calendarEntryId, Recipe recipe) {
        CalendarEntry entry = requireEntry(calendarEntryId);
        //entry.addRecipe(Objects.requireNonNull(recipe));
        // return repository.save(entry);
        return new CalendarEntry();
    }

    public CalendarEntry removeRecipe(long calendarEntryId, long recipeId) {
        CalendarEntry entry = requireEntry(calendarEntryId);
        // entry.removeRecipe(recipeId);
        // return repository.save(entry);
        return new CalendarEntry();
    }

    private CalendarEntry requireEntry(long calendarEntryId) {
        CalendarEntry entry = repository.findById(calendarEntryId);
        if (entry == null) {
            throw new IllegalArgumentException("Calendar entry not found: " + calendarEntryId);
        }
        return entry;
    }
}
