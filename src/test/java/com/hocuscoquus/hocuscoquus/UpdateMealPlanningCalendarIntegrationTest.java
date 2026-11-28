package com.hocuscoquus.hocuscoquus;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.hocuscoquus.hocuscoquus.entity.Account;
import com.hocuscoquus.hocuscoquus.entity.Calendar;
import com.hocuscoquus.hocuscoquus.entity.CalendarEntry;
import com.hocuscoquus.hocuscoquus.entity.Recipe;
import com.hocuscoquus.hocuscoquus.model.CalendarEntryRecipeRequest;
import com.hocuscoquus.hocuscoquus.model.CalendarEntryRequest;
import com.hocuscoquus.hocuscoquus.model.CalendarEntryResponse;
import com.hocuscoquus.hocuscoquus.model.CalendarScheduleRecipeRequest;
import com.hocuscoquus.hocuscoquus.service.AccountService;
import com.hocuscoquus.hocuscoquus.service.CalendarService;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@SpringBootTest(properties = "spring.jpa.hibernate.ddl-auto=validate")
@Transactional
class UpdateMealPlanningCalendarIntegrationTest {
    private static final Instant DATE = Instant.parse("2026-12-01T18:00:00Z");
    private static final Instant LATER = DATE.plusSeconds(3600);

    @Autowired private CalendarService service;
    @Autowired private AccountService accountService;
    @PersistenceContext private EntityManager entityManager;

    private Account createAccount() {
        return accountService.createAccount("Test", "test-" + UUID.randomUUID() + "@example.com", "password");
    }

    private Calendar createCalendar(Account account) {
        Calendar calendar = new Calendar();
        calendar.setAccount(account);
        calendar.setCalendarName("Test calendar");
        entityManager.persist(calendar);
        return calendar;
    }

    private Recipe createRecipe(Account account, String name) {
        Recipe recipe = Recipe.builder().setRecipeName(name)
                .setRecipeDescription("Test description")
                .setInstructions("Test instructions").setAccount(account).build();
        entityManager.persist(recipe);
        return recipe;
    }

    private void flushAndClear() {
        entityManager.flush();
        entityManager.clear();
    }

    @Test void scheduleRecipe_persistsAssociationToAnotherUsersRecipe() {
        Account owner = createAccount();
        Account author = createAccount();
        long calendarId = createCalendar(owner).getCalendarId();
        long recipeId = createRecipe(author, "Pasta").getRecipeId();
        long ownerId = owner.getAccountId();
        flushAndClear();

        CalendarEntryResponse response = service.scheduleRecipe(ownerId, calendarId,
                new CalendarScheduleRecipeRequest(recipeId, DATE));
        assertTrue(response.calendarEntryId() > 0);
        flushAndClear();

        CalendarEntry persisted = entityManager.find(CalendarEntry.class, response.calendarEntryId());
        assertNotNull(persisted);
        assertEquals(calendarId, persisted.getCalendar().getCalendarId());
        assertEquals(DATE.toEpochMilli(), persisted.getDateTime().getTime());
        assertEquals(recipeId, persisted.getRecipes().getFirst().getRecipeId());
    }

    @Test void scheduleRecipe_sameTimeReusesEntryAndDifferentTimeCreatesEntry() {
        Account owner = createAccount();
        long ownerId = owner.getAccountId();
        long calendarId = createCalendar(owner).getCalendarId();
        long firstId = createRecipe(owner, "Pasta").getRecipeId();
        long secondId = createRecipe(owner, "Soup").getRecipeId();
        flushAndClear();

        long entryId = service.scheduleRecipe(ownerId, calendarId,
                new CalendarScheduleRecipeRequest(firstId, DATE)).calendarEntryId();
        flushAndClear();
        assertEquals(entryId, service.scheduleRecipe(ownerId, calendarId,
                new CalendarScheduleRecipeRequest(secondId, DATE)).calendarEntryId());
        flushAndClear();
        service.scheduleRecipe(ownerId, calendarId, new CalendarScheduleRecipeRequest(firstId, LATER));
        flushAndClear();

        List<CalendarEntryResponse> entries = service.getEntries(ownerId, calendarId);
        assertEquals(2, entries.size());
        assertEquals(DATE, entries.getFirst().dateTime());
        assertEquals(2, entries.getFirst().recipes().size());
        assertEquals(LATER, entries.getLast().dateTime());
        assertEquals(firstId, entries.getLast().recipes().getFirst().recipeId());
    }

    @Test void addAndUpdateEntry_persistsDateAndRecipes() {
        Account owner = createAccount();
        long ownerId = owner.getAccountId();
        long calendarId = createCalendar(owner).getCalendarId();
        long recipeId = createRecipe(owner, "Pasta").getRecipeId();
        flushAndClear();

        long entryId = service.addEntry(ownerId, calendarId, new CalendarEntryRequest(DATE)).calendarEntryId();
        flushAndClear();
        service.addRecipe(ownerId, calendarId, entryId, new CalendarEntryRecipeRequest(recipeId));
        flushAndClear();
        service.updateEntry(ownerId, calendarId, entryId, new CalendarEntryRequest(LATER));
        flushAndClear();

        CalendarEntry persisted = entityManager.find(CalendarEntry.class, entryId);
        assertNotNull(persisted);
        assertEquals(LATER.toEpochMilli(), persisted.getDateTime().getTime());
        assertEquals(recipeId, persisted.getRecipes().getFirst().getRecipeId());
    }

    @Test void removeRecipe_preservesOtherRecipesThenDeletesEmptyEntry() {
        Account owner = createAccount();
        long ownerId = owner.getAccountId();
        long calendarId = createCalendar(owner).getCalendarId();
        long firstId = createRecipe(owner, "Pasta").getRecipeId();
        long secondId = createRecipe(owner, "Soup").getRecipeId();
        flushAndClear();

        long entryId = service.scheduleRecipe(ownerId, calendarId,
                new CalendarScheduleRecipeRequest(firstId, DATE)).calendarEntryId();
        service.addRecipe(ownerId, calendarId, entryId, new CalendarEntryRecipeRequest(secondId));
        long otherEntryId = service.scheduleRecipe(ownerId, calendarId,
                new CalendarScheduleRecipeRequest(firstId, LATER)).calendarEntryId();
        flushAndClear();

        service.removeRecipe(ownerId, calendarId, entryId, firstId);
        flushAndClear();
        CalendarEntry remaining = entityManager.find(CalendarEntry.class, entryId);
        assertNotNull(remaining);
        assertEquals(1, remaining.getRecipes().size());
        assertEquals(secondId, remaining.getRecipes().getFirst().getRecipeId());
        service.removeRecipe(ownerId, calendarId, entryId, secondId);
        flushAndClear();

        assertNull(entityManager.find(CalendarEntry.class, entryId));
        assertNotNull(entityManager.find(Recipe.class, firstId));
        assertNotNull(entityManager.find(Recipe.class, secondId));
        assertEquals(firstId, entityManager.find(CalendarEntry.class, otherEntryId)
                .getRecipes().getFirst().getRecipeId());
    }

    @Test void deleteEntry_preservesRecipeAndOtherEntries() {
        Account owner = createAccount();
        long ownerId = owner.getAccountId();
        long calendarId = createCalendar(owner).getCalendarId();
        long recipeId = createRecipe(owner, "Pasta").getRecipeId();
        flushAndClear();

        long firstId = service.scheduleRecipe(ownerId, calendarId,
                new CalendarScheduleRecipeRequest(recipeId, DATE)).calendarEntryId();
        long secondId = service.scheduleRecipe(ownerId, calendarId,
                new CalendarScheduleRecipeRequest(recipeId, LATER)).calendarEntryId();
        flushAndClear();
        service.deleteEntry(ownerId, calendarId, firstId);
        flushAndClear();

        assertNull(entityManager.find(CalendarEntry.class, firstId));
        assertNotNull(entityManager.find(CalendarEntry.class, secondId));
        assertNotNull(entityManager.find(Recipe.class, recipeId));
        assertEquals(1, service.getEntries(ownerId, calendarId).size());
    }

    @Test void scheduleRecipe_otherAccountCannotModifyCalendar() {
        Account owner = createAccount();
        Account other = createAccount();
        long calendarId = createCalendar(owner).getCalendarId();
        long recipeId = createRecipe(owner, "Pasta").getRecipeId();
        long otherId = other.getAccountId();
        flushAndClear();

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> service.scheduleRecipe(otherId, calendarId,
                        new CalendarScheduleRecipeRequest(recipeId, DATE)));
        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
        flushAndClear();
        assertTrue(entityManager.find(Calendar.class, calendarId).getEntries().isEmpty());
    }

    @Test void removeRecipe_otherAccountCannotRemoveScheduledRecipe() {
        Account owner = createAccount();
        Account other = createAccount();
        long ownerId = owner.getAccountId();
        long otherId = other.getAccountId();
        long calendarId = createCalendar(owner).getCalendarId();
        long recipeId = createRecipe(owner, "Pasta").getRecipeId();
        flushAndClear();
        long entryId = service.scheduleRecipe(ownerId, calendarId,
                new CalendarScheduleRecipeRequest(recipeId, DATE)).calendarEntryId();
        flushAndClear();

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> service.removeRecipe(otherId, calendarId, entryId, recipeId));
        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
        flushAndClear();
        assertEquals(recipeId, entityManager.find(CalendarEntry.class, entryId)
                .getRecipes().getFirst().getRecipeId());
    }
}
