package com.hocuscoquus.hocuscoquus.service;

import java.util.Objects;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hocuscoquus.hocuscoquus.entity.Calendar;
import com.hocuscoquus.hocuscoquus.entity.CalendarEntry;
import com.hocuscoquus.hocuscoquus.repository.CalendarEntryRepository;
import com.hocuscoquus.hocuscoquus.repository.CalendarRepository;

@Service
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

    @Transactional
    public boolean deleteCalendar(long requesterId, long calendarId) {
        // Confirm calendar exists
        Calendar cal = calendarRepository.findById(calendarId);
        if (cal == null) {
            throw new IllegalArgumentException("Calendar not found for ID: " + calendarId);
        }

        // Confirm requesting account owns calendar to delete
        long ownerId = cal.getAccountId();
        if (requesterId != ownerId) {
            throw new IllegalArgumentException("Account with ID " + requesterId
                    + " attempting to delete calendar from account with ID " + ownerId);
        }

        // Delete calendar
        this.calendarEntryRepository.deleteByCalendar_CalendarId(calendarId);
        this.calendarRepository.deleteById(calendarId);
        return true;
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
    public boolean deleteEntry(long requesterId, long calendarEntryId) {
        // Confirm calendar entry exists
        CalendarEntry entry = calendarEntryRepository.findById(calendarEntryId);
        if (entry == null) {
            throw new IllegalArgumentException("Calendar entry not found for ID: " + calendarEntryId);
        }

        // Confirm requesting account owns calendar entry to delete
        long ownerId = entry.getCalendar().getAccountId();
        if (requesterId != ownerId) {
            throw new IllegalArgumentException("Account with ID " + requesterId
                    + " attempting to delete calendar from account with ID " + ownerId);
        }

        // Delete calendar entry
        this.calendarEntryRepository.deleteById(calendarEntryId);
        return true;
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
