package com.hocuscoquus.hocuscoquus.service;

import java.util.Objects;

import com.hocuscoquus.hocuscoquus.model.Calendar;
import com.hocuscoquus.hocuscoquus.model.CalendarEntry;
import com.hocuscoquus.hocuscoquus.repository.CalendarEntryRepository;
import com.hocuscoquus.hocuscoquus.repository.CalendarRepository;

public class CalendarService {
    private final CalendarRepository calendarRepository;
    private final CalendarEntryRepository entryRepository;

    public CalendarService(CalendarRepository calendarRepository, CalendarEntryRepository entryRepository) {
        this.calendarRepository = Objects.requireNonNull(calendarRepository);
        this.entryRepository = Objects.requireNonNull(entryRepository);
    }

    public Calendar createCalendar(String name) {
        // return calendarRepository.save(new Calendar(0L, name, ""));
        return new Calendar();
    }

    public void deleteCalendar(long calendarId) {
        // calendarRepository.deleteById(calendarId);
    }

    public CalendarEntry addEntry(long calendarId, CalendarEntry entry) {
        // Calendar calendar = calendarRepository.findById(calendarId);
        // if (calendar == null) {
        //     throw new IllegalArgumentException("Calendar not found: " + calendarId);
        // }
        // calendar.addEntry(entry);
        // calendarRepository.save(calendar);
        // return entryRepository.save(entry);
        return new CalendarEntry();
    }

    public void deleteEntry(long calendarEntryId) {
        // entryRepository.deleteById(calendarEntryId);
    }

    public CalendarEntry updateEntry(CalendarEntry entry) {
        // return entryRepository.save(Objects.requireNonNull(entry));
        return new CalendarEntry();
    }

    public boolean downloadCalendar(long calendarId) {
        // Calendar calendar = calendarRepository.findById(calendarId);
        // return calendar != null && calendar.downloadCalendar();
        return false;
    }

    public boolean syncGoogleAccount(long calendarId, String email) {
        // Calendar calendar = calendarRepository.findById(calendarId);
        // return calendar != null && calendar.syncGoogleAccount(email);
        return false;
    }
}
