package com.hocuscoquus.hocuscoquus.service;

import java.util.Objects;

import com.hocuscoquus.hocuscoquus.model.CalendarEntry;
import com.hocuscoquus.hocuscoquus.model.Recipe;
import com.hocuscoquus.hocuscoquus.repository.CalendarEntryRepository;

// @Service
public class CalendarEntryService {
    private final CalendarEntryRepository repository;

    // TODO: review/add contructor(s)
    public CalendarEntryService(CalendarEntryRepository repository) {
        this.repository = Objects.requireNonNull(repository);
    }

    public CalendarEntry addRecipe(long calendarEntryId, Recipe recipe) {
       
        // TODO: add implementation for this method

        return new CalendarEntry();
    }

    public CalendarEntry removeRecipe(long calendarEntryId, long recipeId) {
       
        // TODO: add implementation for this method

        return new CalendarEntry();
    }
    
}
