package com.hocuscoquus.hocuscoquus.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import jakarta.persistence.PersistenceContext;

import com.hocuscoquus.hocuscoquus.entity.Account;
import com.hocuscoquus.hocuscoquus.entity.Calendar;
import com.hocuscoquus.hocuscoquus.entity.CalendarEntry;
import com.hocuscoquus.hocuscoquus.entity.Recipe;
import com.hocuscoquus.hocuscoquus.model.CalendarCreateDto;
import com.hocuscoquus.hocuscoquus.model.CalendarEntryRecipeRequest;
import com.hocuscoquus.hocuscoquus.model.CalendarEntryRequest;
import com.hocuscoquus.hocuscoquus.model.CalendarEntryResponse;
import com.hocuscoquus.hocuscoquus.model.CalendarScheduleRecipeRequest;

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

    @Transactional
    public CalendarEntryResponse addEntry(
            long accountId,
            long calendarId,
            CalendarEntryRequest request) {

        if (request == null || request.dateTime() == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Date and time are required");
        }
        Date dateTime = Date.from(request.dateTime());

        Calendar calendar = requireOwnedCalendarForUpdate(accountId, calendarId);

        if (calendar.getEntries().stream().anyMatch(
                e -> e.getDateTime().getTime() == dateTime.getTime())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "A calendar entry already exists at this date and time");
        }

        CalendarEntry entry = new CalendarEntry();
        entry.setCalendar(calendar);
        entry.setDateTime(dateTime);
        entityManager.persist(entry);
        calendar.getEntries().add(entry);

        return CalendarEntryResponse.fromEntity(entry);
    }

    @Transactional(readOnly = true)
    public List<CalendarEntryResponse> getEntries(
            long accountId, long calendarId) {

        if (getCalendarForAccount(calendarId, accountId) == null) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Calendar not found");
        }

        return entityManager.createQuery("""
                SELECT e FROM CalendarEntry e
                WHERE e.calendar.calendarId = :calendarId
                ORDER BY e.dateTime ASC, e.calendarEntryId ASC
                """, CalendarEntry.class)
                .setParameter("calendarId", calendarId)
                .getResultList()
                .stream()
                .map(CalendarEntryResponse::fromEntity)
                .toList();
    }

    @Transactional
    public void deleteEntry(
            long accountId,
            long calendarId,
            long calendarEntryId) {

        requireOwnedCalendarForUpdate(accountId, calendarId);

        CalendarEntry entry = requireCalendarEntry(
                calendarId, calendarEntryId);

        removeCalendarEntry(entry);
    }

    @Transactional
    public CalendarEntryResponse updateEntry(
            long accountId,
            long calendarId,
            long calendarEntryId,
            CalendarEntryRequest request) {

        if (request == null || request.dateTime() == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Date and time are required");
        }
        Date dateTime = Date.from(request.dateTime());

        Calendar calendar = requireOwnedCalendarForUpdate(accountId, calendarId);
        CalendarEntry entry = requireCalendarEntry(calendarId, calendarEntryId);

        if (calendar.getEntries().stream().anyMatch(e ->
                e.getCalendarEntryId() != calendarEntryId
                && e.getDateTime().getTime() == dateTime.getTime())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "A calendar entry already exists at this date and time");
        }

        entry.setDateTime(dateTime);
        return CalendarEntryResponse.fromEntity(entry);
    }

    @Transactional
    public CalendarEntryResponse addRecipe(
            long accountId,
            long calendarId,
            long calendarEntryId,
            CalendarEntryRecipeRequest request) {

        if (request == null || request.recipeId() == null || request.recipeId() <= 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Invalid recipe ID");
        }

        requireOwnedCalendarForUpdate(accountId, calendarId);
        CalendarEntry entry = requireCalendarEntry(calendarId, calendarEntryId);

        Recipe recipe = entityManager.find(Recipe.class, request.recipeId());
        if (recipe == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Recipe not found");
        }

        if (entry.getRecipes().stream().anyMatch(
                r -> r.getRecipeId() == recipe.getRecipeId())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Recipe is already scheduled for this date and time");
        }

        entry.getRecipes().add(recipe);
        return CalendarEntryResponse.fromEntity(entry);
    }

    @Transactional
    public void removeRecipe(
            long accountId,
            long calendarId,
            long calendarEntryId,
            long recipeId) {

        requireOwnedCalendarForUpdate(accountId, calendarId);
        CalendarEntry entry = requireCalendarEntry(calendarId, calendarEntryId);

        if (!entry.getRecipes().removeIf(recipe -> recipe.getRecipeId() == recipeId)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Recipe not found in calendar entry");
        }

        if (entry.getRecipes().isEmpty()) {
            removeCalendarEntry(entry);
        }
    }

    @Transactional
    public CalendarEntryResponse scheduleRecipe(
            long accountId,
            long calendarId,
            CalendarScheduleRecipeRequest request) {

        if (request == null || request.dateTime() == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Date and time are required");
        }
        if (request.recipeId() == null || request.recipeId() <= 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Invalid recipe ID");
        }
        Date dateTime = Date.from(request.dateTime());

        Calendar calendar = requireOwnedCalendarForUpdate(accountId, calendarId);

        Recipe recipe = entityManager.find(Recipe.class, request.recipeId());
        if (recipe == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Recipe not found");
        }

        CalendarEntry entry = calendar.getEntries().stream()
                .filter(e -> e.getDateTime().getTime() == dateTime.getTime())
                .findFirst()
                .orElse(null);

        if (entry == null) {
            entry = new CalendarEntry();
            entry.setCalendar(calendar);
            entry.setDateTime(dateTime);
            entityManager.persist(entry);
            calendar.getEntries().add(entry);
        }

        if (entry.getRecipes().stream().anyMatch(
                r -> r.getRecipeId() == recipe.getRecipeId())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Recipe is already scheduled for this date and time");
        }

        entry.getRecipes().add(recipe);
        return CalendarEntryResponse.fromEntity(entry);
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

    private void removeCalendarEntry(CalendarEntry entry) {
        entry.getRecipes().clear();
        entry.getCalendar().getEntries().remove(entry);
        entityManager.remove(entry);
    }

    private Calendar requireOwnedCalendarForUpdate(
            long accountId, long calendarId) {

        Calendar calendar = entityManager.find(
                Calendar.class,
                calendarId,
                LockModeType.PESSIMISTIC_WRITE);

        if (calendar == null
                || calendar.getAccount() == null
                || calendar.getAccountId() != accountId) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Calendar not found");
        }

        return calendar;
    }

    private CalendarEntry requireCalendarEntry(
            long calendarId, long calendarEntryId) {

        CalendarEntry entry = entityManager.find(
                CalendarEntry.class, calendarEntryId);

        if (entry == null
                || entry.getCalendar().getCalendarId() != calendarId) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Calendar entry not found");
        }

        return entry;
    }
}
