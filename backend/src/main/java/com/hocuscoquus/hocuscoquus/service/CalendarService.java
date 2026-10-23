package com.hocuscoquus.hocuscoquus.service;

import java.util.Objects;

import com.hocuscoquus.hocuscoquus.model.Calendar;
import com.hocuscoquus.hocuscoquus.model.CalendarEntry;
import com.hocuscoquus.hocuscoquus.repository.CalendarEntryRepository;
import com.hocuscoquus.hocuscoquus.repository.CalendarRepository;

public class CalendarService {
    private final CalendarRepository calendarRepository;
    private final CalendarEntryRepository calendarEntryRepository;


    // TODO: review/add constructor(s)

    public CalendarService(CalendarRepository calendarRepository, CalendarEntryRepository calendarEntryRepository) {
        this.calendarRepository = Objects.requireNonNull(calendarRepository);
        this.calendarEntryRepository = Objects.requireNonNull(calendarEntryRepository);
    }

    public Calendar createCalendar(String name) {
        // TODO: add implementation for this method
        return new Calendar();
    }

    public void deleteCalendar(long calendarId) {
        // TODO: add implementation for this method
    }

    /**
     * Adds a calendar entry to the calendar.
     *
     * @param calendarId the specified calendar id
     * @param entry the calendar entry to add
     */
    public CalendarEntry addEntry(long calendarId, CalendarEntry entry) {
        // TODO: add implementation for this method

        // Calendar calendar = calendarRepository.findById(calendarId);
        // if (calendar == null) {
        //     throw new IllegalArgumentException("Calendar not found: " + calendarId);
        // }
        // calendar.addEntry(entry);
        // calendarRepository.save(calendar);
        // return calendarEntryRepository.save(entry);
        return new CalendarEntry();
    }

    /**
     * Removes the calendar entry with the matching unique ID.
     * If no entry with the given ID exists, the list remains unchanged.
     *
     * @param calendarEntryId the ID of the calendar entry to remove
     */
    public void deleteEntry(long calendarEntryId) {
        // for (int i = 0; i < entries.size(); i++) {
        //     if (entries.get(i).getCalendarEntryId() == calendarEntryId) {
        //         entries.remove(i);
        //         return;
        //     }
        // }
    }

    public CalendarEntry updateEntry(CalendarEntry entry) {

        // TODO: add implementation for this method

        return new CalendarEntry();
    }

    /**
     * Downloads the calendar.
     *
     * @return true when calendar download is successful, false otherwise
     */
    public boolean downloadCalendar(long calendarId) {
        
        // TODO: add implementation for this method

        return false;
    }


    /**
     * Synchronizes the calendar with the specified Google account.
     *
     * @param email the email address of the Google account to synchronize with
     * @return true when sync with Google Account is successful, false otherwise
     */
    public boolean syncGoogleAccount(long calendarId, String email) {
        
        // TODO: add implementation for this method
        
        return false;
    }
}
