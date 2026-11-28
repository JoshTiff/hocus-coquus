package com.hocuscoquus.hocuscoquus;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.server.ResponseStatusException;

import com.hocuscoquus.hocuscoquus.entity.Account;
import com.hocuscoquus.hocuscoquus.entity.Calendar;
import com.hocuscoquus.hocuscoquus.entity.CalendarEntry;
import com.hocuscoquus.hocuscoquus.entity.Recipe;
import com.hocuscoquus.hocuscoquus.model.CalendarEntryRecipeRequest;
import com.hocuscoquus.hocuscoquus.model.CalendarEntryRequest;
import com.hocuscoquus.hocuscoquus.model.CalendarEntryResponse;
import com.hocuscoquus.hocuscoquus.model.CalendarScheduleRecipeRequest;
import com.hocuscoquus.hocuscoquus.service.CalendarService;

import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import jakarta.persistence.TypedQuery;

@ExtendWith(MockitoExtension.class)
class UpdateMealPlanningCalendarUnitTest {
    private static final long ACCOUNT_ID = 1L;
    private static final long CALENDAR_ID = 2L;
    private static final long ENTRY_ID = 3L;
    private static final long RECIPE_ID = 4L;
    private static final Instant DATE = Instant.parse("2026-12-01T18:00:00Z");
    private static final Instant LATER = DATE.plusSeconds(3600);

    @Mock private EntityManager entityManager;
    @Mock private TypedQuery<Calendar> calendarQuery;
    @Mock private TypedQuery<CalendarEntry> entryQuery;

    private CalendarService service;
    private Calendar calendar;

    @BeforeEach
    void setUp() {
        service = new CalendarService();
        ReflectionTestUtils.setField(service, "entityManager", entityManager);
        Account account = new Account();
        account.setAccountId(ACCOUNT_ID);
        calendar = new Calendar();
        calendar.setCalendarId(CALENDAR_ID);
        calendar.setAccount(account);
    }

    private void stubOwnedCalendar() {
        when(entityManager.find(Calendar.class, CALENDAR_ID,
                LockModeType.PESSIMISTIC_WRITE)).thenReturn(calendar);
    }

    private CalendarEntry entry(long id, Instant date) {
        CalendarEntry entry = new CalendarEntry();
        entry.setCalendarEntryId(id);
        entry.setCalendar(calendar);
        entry.setDateTime(Date.from(date));
        calendar.getEntries().add(entry);
        return entry;
    }

    private CalendarEntry existingEntry() {
        CalendarEntry entry = entry(ENTRY_ID, DATE);
        when(entityManager.find(CalendarEntry.class, ENTRY_ID)).thenReturn(entry);
        return entry;
    }

    private Recipe recipe(long id) {
        Recipe recipe = Recipe.builder().setRecipeName("Recipe " + id).build();
        recipe.setRecipeId(id);
        return recipe;
    }

    private Recipe stubRecipe() {
        Recipe recipe = recipe(RECIPE_ID);
        when(entityManager.find(Recipe.class, RECIPE_ID)).thenReturn(recipe);
        return recipe;
    }

    private void stubCalendarLookup(List<Calendar> calendars) {
        when(entityManager.createQuery(any(String.class),
                org.mockito.ArgumentMatchers.eq(Calendar.class))).thenReturn(calendarQuery);
        when(calendarQuery.setParameter("calendarId", CALENDAR_ID)).thenReturn(calendarQuery);
        when(calendarQuery.setParameter("accountId", ACCOUNT_ID)).thenReturn(calendarQuery);
        when(calendarQuery.getResultList()).thenReturn(calendars);
    }

    private void expectStatus(HttpStatus expected, org.junit.jupiter.api.function.Executable action) {
        assertEquals(expected, assertThrows(ResponseStatusException.class, action).getStatusCode());
    }

    @Test void addEntry_validRequest_createsEmptyEntry() {
        stubOwnedCalendar();
        CalendarEntryResponse response = service.addEntry(ACCOUNT_ID, CALENDAR_ID,
                new CalendarEntryRequest(DATE));
        assertEquals(CALENDAR_ID, response.calendarId());
        assertEquals(DATE, response.dateTime());
        assertTrue(response.recipes().isEmpty());
        assertEquals(1, calendar.getEntries().size());
        verify(entityManager).persist(calendar.getEntries().getFirst());
    }

    @Test void addEntry_duplicateTime_rejectsRequest() {
        stubOwnedCalendar();
        entry(ENTRY_ID, DATE);
        expectStatus(HttpStatus.CONFLICT, () -> service.addEntry(ACCOUNT_ID, CALENDAR_ID,
                new CalendarEntryRequest(DATE)));
        verify(entityManager, never()).persist(any());
    }

    @Test void addEntry_missingDate_rejectsRequest() {
        expectStatus(HttpStatus.BAD_REQUEST, () -> service.addEntry(ACCOUNT_ID, CALENDAR_ID,
                new CalendarEntryRequest(null)));
        verifyNoInteractions(entityManager);
    }

    @Test void addEntry_nonexistentCalendar_rejectsRequest() {
        expectStatus(HttpStatus.NOT_FOUND, () -> service.addEntry(ACCOUNT_ID, CALENDAR_ID,
                new CalendarEntryRequest(DATE)));
        verify(entityManager, never()).persist(any());
    }

    @Test void addEntry_otherAccountCalendar_rejectsRequest() {
        stubOwnedCalendar();
        expectStatus(HttpStatus.NOT_FOUND, () -> service.addEntry(99L, CALENDAR_ID,
                new CalendarEntryRequest(DATE)));
        verify(entityManager, never()).persist(any());
    }

    @Test void getEntries_ownedCalendar_returnsRecipeSummaries() {
        stubCalendarLookup(List.of(calendar));
        CalendarEntry entry = entry(ENTRY_ID, DATE);
        entry.getRecipes().add(recipe(RECIPE_ID));
        when(entityManager.createQuery(any(String.class),
                org.mockito.ArgumentMatchers.eq(CalendarEntry.class))).thenReturn(entryQuery);
        when(entryQuery.setParameter("calendarId", CALENDAR_ID)).thenReturn(entryQuery);
        when(entryQuery.getResultList()).thenReturn(List.of(entry));
        List<CalendarEntryResponse> results = service.getEntries(ACCOUNT_ID, CALENDAR_ID);
        assertEquals(1, results.size());
        assertEquals(ENTRY_ID, results.getFirst().calendarEntryId());
        assertEquals(RECIPE_ID, results.getFirst().recipes().getFirst().recipeId());
    }

    @Test void getEntries_inaccessibleCalendar_rejectsRequest() {
        stubCalendarLookup(List.of());
        expectStatus(HttpStatus.NOT_FOUND, () -> service.getEntries(ACCOUNT_ID, CALENDAR_ID));
    }

    @Test void updateEntry_validDate_preservesRecipes() {
        stubOwnedCalendar();
        CalendarEntry entry = existingEntry();
        entry.getRecipes().add(recipe(RECIPE_ID));
        CalendarEntryResponse response = service.updateEntry(ACCOUNT_ID, CALENDAR_ID, ENTRY_ID,
                new CalendarEntryRequest(LATER));
        assertEquals(LATER, response.dateTime());
        assertEquals(LATER, entry.getDateTime().toInstant());
        assertEquals(RECIPE_ID, response.recipes().getFirst().recipeId());
    }

    @Test void updateEntry_sameDate_succeeds() {
        stubOwnedCalendar();
        existingEntry();
        assertEquals(DATE, service.updateEntry(ACCOUNT_ID, CALENDAR_ID, ENTRY_ID,
                new CalendarEntryRequest(DATE)).dateTime());
    }

    @Test void updateEntry_occupiedDate_rejectsRequest() {
        stubOwnedCalendar();
        CalendarEntry entry = existingEntry();
        entry(9L, LATER);
        expectStatus(HttpStatus.CONFLICT, () -> service.updateEntry(ACCOUNT_ID, CALENDAR_ID,
                ENTRY_ID, new CalendarEntryRequest(LATER)));
        assertEquals(DATE, entry.getDateTime().toInstant());
    }

    @Test void updateEntry_missingDate_rejectsRequest() {
        expectStatus(HttpStatus.BAD_REQUEST, () -> service.updateEntry(ACCOUNT_ID, CALENDAR_ID,
                ENTRY_ID, new CalendarEntryRequest(null)));
        verifyNoInteractions(entityManager);
    }

    @Test void deleteEntry_removesEntryNotRecipe() {
        stubOwnedCalendar();
        CalendarEntry entry = existingEntry();
        Recipe recipe = recipe(RECIPE_ID);
        entry.getRecipes().add(recipe);
        service.deleteEntry(ACCOUNT_ID, CALENDAR_ID, ENTRY_ID);
        assertTrue(calendar.getEntries().isEmpty());
        assertTrue(entry.getRecipes().isEmpty());
        verify(entityManager).remove(entry);
        verify(entityManager, never()).remove(recipe);
    }

    @Test void deleteEntry_missingEntry_rejectsRequest() {
        stubOwnedCalendar();
        expectStatus(HttpStatus.NOT_FOUND, () -> service.deleteEntry(ACCOUNT_ID, CALENDAR_ID, ENTRY_ID));
        verify(entityManager, never()).remove(any());
    }

    @Test void deleteEntry_entryFromDifferentCalendar_rejectsRequest() {
        stubOwnedCalendar();
        CalendarEntry entry = existingEntry();
        Calendar differentCalendar = new Calendar();
        differentCalendar.setCalendarId(99L);
        entry.setCalendar(differentCalendar);
        expectStatus(HttpStatus.NOT_FOUND, () -> service.deleteEntry(ACCOUNT_ID, CALENDAR_ID, ENTRY_ID));
        verify(entityManager, never()).remove(any());
    }

    @Test void addRecipe_existingEntry_addsRecipe() {
        stubOwnedCalendar();
        CalendarEntry entry = existingEntry();
        Recipe recipe = stubRecipe();
        CalendarEntryResponse response = service.addRecipe(ACCOUNT_ID, CALENDAR_ID, ENTRY_ID,
                new CalendarEntryRecipeRequest(RECIPE_ID));
        assertEquals(List.of(recipe), entry.getRecipes());
        assertEquals(RECIPE_ID, response.recipes().getFirst().recipeId());
    }

    @Test void addRecipe_duplicateRecipe_rejectsRequest() {
        stubOwnedCalendar();
        CalendarEntry entry = existingEntry();
        entry.getRecipes().add(stubRecipe());
        expectStatus(HttpStatus.CONFLICT, () -> service.addRecipe(ACCOUNT_ID, CALENDAR_ID, ENTRY_ID,
                new CalendarEntryRecipeRequest(RECIPE_ID)));
        assertEquals(1, entry.getRecipes().size());
    }

    @Test void addRecipe_missingRecipe_rejectsRequest() {
        stubOwnedCalendar();
        CalendarEntry entry = existingEntry();
        expectStatus(HttpStatus.NOT_FOUND, () -> service.addRecipe(ACCOUNT_ID, CALENDAR_ID, ENTRY_ID,
                new CalendarEntryRecipeRequest(RECIPE_ID)));
        assertTrue(entry.getRecipes().isEmpty());
    }

    @Test void addRecipe_invalidId_rejectsRequest() {
        expectStatus(HttpStatus.BAD_REQUEST, () -> service.addRecipe(ACCOUNT_ID, CALENDAR_ID, ENTRY_ID,
                new CalendarEntryRecipeRequest(-1L)));
        verifyNoInteractions(entityManager);
    }

    @Test void removeRecipe_multipleRecipes_preservesEntryAndOtherRecipe() {
        stubOwnedCalendar();
        CalendarEntry entry = existingEntry();
        Recipe first = recipe(RECIPE_ID);
        Recipe second = recipe(5L);
        entry.getRecipes().addAll(List.of(first, second));
        service.removeRecipe(ACCOUNT_ID, CALENDAR_ID, ENTRY_ID, RECIPE_ID);
        assertEquals(List.of(second), entry.getRecipes());
        assertEquals(List.of(entry), calendar.getEntries());
        verify(entityManager, never()).remove(any());
    }

    @Test void removeRecipe_lastRecipe_deletesEmptyEntryOnly() {
        stubOwnedCalendar();
        CalendarEntry entry = existingEntry();
        Recipe recipe = recipe(RECIPE_ID);
        entry.getRecipes().add(recipe);
        service.removeRecipe(ACCOUNT_ID, CALENDAR_ID, ENTRY_ID, RECIPE_ID);
        assertTrue(calendar.getEntries().isEmpty());
        verify(entityManager).remove(entry);
        verify(entityManager, never()).remove(recipe);
    }

    @Test void removeRecipe_unscheduledRecipe_rejectsRequest() {
        stubOwnedCalendar();
        CalendarEntry entry = existingEntry();
        entry.getRecipes().add(recipe(5L));
        expectStatus(HttpStatus.NOT_FOUND, () -> service.removeRecipe(ACCOUNT_ID, CALENDAR_ID, ENTRY_ID, RECIPE_ID));
        assertEquals(1, entry.getRecipes().size());
        verify(entityManager, never()).remove(any());
    }

    @Test void scheduleRecipe_newTime_createsEntry() {
        stubOwnedCalendar();
        Recipe recipe = stubRecipe();
        CalendarEntryResponse response = service.scheduleRecipe(ACCOUNT_ID, CALENDAR_ID,
                new CalendarScheduleRecipeRequest(RECIPE_ID, DATE));
        assertEquals(DATE, response.dateTime());
        assertEquals(List.of(recipe), calendar.getEntries().getFirst().getRecipes());
        verify(entityManager).persist(calendar.getEntries().getFirst());
    }

    @Test void scheduleRecipe_existingTime_reusesEntry() {
        stubOwnedCalendar();
        CalendarEntry entry = entry(ENTRY_ID, DATE);
        entry.getRecipes().add(recipe(5L));
        stubRecipe();
        CalendarEntryResponse response = service.scheduleRecipe(ACCOUNT_ID, CALENDAR_ID,
                new CalendarScheduleRecipeRequest(RECIPE_ID, DATE));
        assertEquals(ENTRY_ID, response.calendarEntryId());
        assertEquals(2, response.recipes().size());
        assertEquals(1, calendar.getEntries().size());
        verify(entityManager, never()).persist(any());
    }

    @Test void scheduleRecipe_sameRecipeDifferentTime_succeeds() {
        stubOwnedCalendar();
        Recipe recipe = stubRecipe();
        entry(ENTRY_ID, DATE).getRecipes().add(recipe);
        service.scheduleRecipe(ACCOUNT_ID, CALENDAR_ID,
                new CalendarScheduleRecipeRequest(RECIPE_ID, LATER));
        assertEquals(2, calendar.getEntries().size());
        assertEquals(List.of(recipe), calendar.getEntries().getLast().getRecipes());
    }

    @Test void scheduleRecipe_duplicateRecipe_rejectsRequest() {
        stubOwnedCalendar();
        entry(ENTRY_ID, DATE).getRecipes().add(stubRecipe());
        expectStatus(HttpStatus.CONFLICT, () -> service.scheduleRecipe(ACCOUNT_ID, CALENDAR_ID,
                new CalendarScheduleRecipeRequest(RECIPE_ID, DATE)));
        verify(entityManager, never()).persist(any());
    }

    @Test void scheduleRecipe_missingRecipe_doesNotCreateEntry() {
        stubOwnedCalendar();
        expectStatus(HttpStatus.NOT_FOUND, () -> service.scheduleRecipe(ACCOUNT_ID, CALENDAR_ID,
                new CalendarScheduleRecipeRequest(RECIPE_ID, DATE)));
        assertTrue(calendar.getEntries().isEmpty());
        verify(entityManager, never()).persist(any());
    }

    @Test void scheduleRecipe_otherAccountCalendar_rejectsRequest() {
        stubOwnedCalendar();
        expectStatus(HttpStatus.NOT_FOUND, () -> service.scheduleRecipe(99L, CALENDAR_ID,
                new CalendarScheduleRecipeRequest(RECIPE_ID, DATE)));
        verify(entityManager, never()).persist(any());
    }

    @Test void scheduleRecipe_missingDate_rejectsRequest() {
        expectStatus(HttpStatus.BAD_REQUEST, () -> service.scheduleRecipe(ACCOUNT_ID, CALENDAR_ID,
                new CalendarScheduleRecipeRequest(RECIPE_ID, null)));
        verifyNoInteractions(entityManager);
    }

    @Test void scheduleRecipe_invalidRecipeId_rejectsRequest() {
        expectStatus(HttpStatus.BAD_REQUEST, () -> service.scheduleRecipe(ACCOUNT_ID, CALENDAR_ID,
                new CalendarScheduleRecipeRequest(0L, DATE)));
        verifyNoInteractions(entityManager);
    }
}
