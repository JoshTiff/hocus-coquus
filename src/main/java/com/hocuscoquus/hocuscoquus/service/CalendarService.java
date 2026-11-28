package com.hocuscoquus.hocuscoquus.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import com.hocuscoquus.hocuscoquus.entity.Account;
import com.hocuscoquus.hocuscoquus.entity.Calendar;
import com.hocuscoquus.hocuscoquus.entity.CalendarEntry;
import com.hocuscoquus.hocuscoquus.model.CalendarCreateDto;

@Service
public class CalendarService {
    private static final int MAX_CALENDAR_TEXT_LENGTH = 255;

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    public Calendar createCalendar(
            CalendarCreateDto calendarCreateDto,
            long accountId) {
        String name = calendarCreateDto.getCalendarName();
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("A calendar name is required.");
        }
        name = name.trim();
        if (name.length() > MAX_CALENDAR_TEXT_LENGTH) {
            throw new IllegalArgumentException("A calendar name cannot exceed 255 characters.");
        }

        String description = calendarCreateDto.getDescription();
        if (description != null) {
            description = description.trim();
            if (description.length() > MAX_CALENDAR_TEXT_LENGTH) {
                throw new IllegalArgumentException("A calendar description cannot exceed 255 characters.");
            }
        }

        Account account = entityManager.find(Account.class, accountId);
        if (account == null) {
            throw new IllegalArgumentException("Account not found for ID: " + accountId);
        }

        Calendar calendar = new Calendar();
        calendar.setCalendarName(name);
        calendar.setDescription(description == null || description.isBlank() ? null : description);
        calendar.setAccount(account);
        entityManager.persist(calendar);
        return calendar;
    }

    @Transactional(readOnly = true)
    public Calendar getCalendarForAccount(long calendarId, long accountId) {
        return entityManager.createQuery("""
                SELECT c FROM Calendar c
                WHERE c.calendarId = :calendarId
                AND c.account.accountId = :accountId
                """, Calendar.class)
                .setParameter("calendarId", calendarId)
                .setParameter("accountId", accountId)
                .getResultList()
                .stream()
                .findFirst()
                .orElse(null);
    }

    @Transactional(readOnly = true)
    public List<Calendar> getCalendarsForAccount(long accountId) {
        return entityManager.createQuery("""
                SELECT c FROM Calendar c
                WHERE c.account.accountId = :accountId
                """, Calendar.class)
                .setParameter("accountId", accountId)
                .getResultList();
    }

    @Transactional
    public boolean deleteCalendar(long requesterId, long calendarId) {
        Calendar calendar = entityManager.find(Calendar.class, calendarId);

        if (calendar == null) {
            throw new IllegalArgumentException(
                    "Calendar not found for ID: " + calendarId);
        }

        if (calendar.getAccountId() != requesterId) {
            throw new IllegalArgumentException(
                    "Account with ID " + requesterId
                    + " attempting to delete calendar from account with ID "
                    + calendar.getAccountId());
        }

        for (CalendarEntry entry : new ArrayList<>(calendar.getEntries())) {
            removeCalendarEntry(entry);
        }

        entityManager.remove(calendar);

        return true;
    }

    /**
     * Adds a calendar entry to the calendar.
     *
     * @param calendarId the specified calendar id
     * @param entry the calendar entry to add
     */
    public CalendarEntry addEntry(long calendarId, CalendarEntry entry) {
        return new CalendarEntry();
    }

    private void removeCalendarEntry(CalendarEntry entry) {
        entry.getRecipes().clear();
        entry.getCalendar().getEntries().remove(entry);
        entityManager.remove(entry);
    }

    @Transactional
    public boolean deleteEntry(long requesterId, long calendarEntryId) {
        CalendarEntry entry = entityManager.find(
                CalendarEntry.class, calendarEntryId);

        if (entry == null) {
            throw new IllegalArgumentException(
                    "Calendar entry not found for ID: " + calendarEntryId);
        }

        long ownerId = entry.getCalendar().getAccountId();

        if (requesterId != ownerId) {
            throw new IllegalArgumentException(
                    "Account with ID " + requesterId
                    + " attempting to delete calendar entry from account with ID "
                    + ownerId);
        }

        removeCalendarEntry(entry);

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
