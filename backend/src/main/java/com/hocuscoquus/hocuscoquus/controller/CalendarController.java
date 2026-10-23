package com.hocuscoquus.hocuscoquus.controller;

import com.hocuscoquus.hocuscoquus.model.Calendar;
import com.hocuscoquus.hocuscoquus.model.CalendarEntry;
import com.hocuscoquus.hocuscoquus.service.CalendarService;

public class CalendarController {
    private final CalendarService calendarService;

    public CalendarController(CalendarService calendarService) {
        this.calendarService = calendarService;
    }

    public Calendar createCalendar(String name) {
        return calendarService.createCalendar(name);
    }

    public void deleteCalendar(long calendarId) {
        calendarService.deleteCalendar(calendarId);
    }

    public CalendarEntry addEntry(long calendarId, CalendarEntry entry) {
        return calendarService.addEntry(calendarId, entry);
    }

    public void deleteEntry(long calendarEntryId) {
        calendarService.deleteEntry(calendarEntryId);
    }

    public CalendarEntry updateEntry(CalendarEntry entry) {
        return calendarService.updateEntry(entry);
    }
}
