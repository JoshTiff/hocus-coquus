package com.hocuscoquus.hocuscoquus.controller;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.hocuscoquus.hocuscoquus.model.Calendar;
import com.hocuscoquus.hocuscoquus.model.CalendarEntry;
import com.hocuscoquus.hocuscoquus.service.CalendarService;

@RestController
@RequestMapping("/api/calendars")
public class CalendarController {
    private final CalendarService calendarService;

    public CalendarController(CalendarService calendarService) {
        this.calendarService = calendarService;
    }

    @PostMapping
    public Calendar createCalendar(@RequestParam String name) {
        return calendarService.createCalendar(name);
    }

    @DeleteMapping("/{calendarId}")
    public void deleteCalendar(@PathVariable long calendarId) {
        calendarService.deleteCalendar(calendarId);
    }

    @PostMapping("/{calendarId}/entries")
    public CalendarEntry addEntry(@PathVariable long calendarId, @RequestBody CalendarEntry entry) {
        return calendarService.addEntry(calendarId, entry);
    }

    @DeleteMapping("/entries/{calendarEntryId}")
    public void deleteEntry(@PathVariable long calendarEntryId) {
        calendarService.deleteEntry(calendarEntryId);
    }

    @PutMapping("/entries")
    public CalendarEntry updateEntry(@RequestBody CalendarEntry entry) {
        return calendarService.updateEntry(entry);
    }

    // need printRecipe, downloadCalendar, syncGoogleAccount?
    
}
