package com.hocuscoquus.hocuscoquus;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.Instant;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;

import com.hocuscoquus.hocuscoquus.config.SecurityConfig;
import com.hocuscoquus.hocuscoquus.controller.CalendarApiController;
import com.hocuscoquus.hocuscoquus.entity.Account;
import com.hocuscoquus.hocuscoquus.entity.AccountDetails;
import com.hocuscoquus.hocuscoquus.model.CalendarEntryRecipeRequest;
import com.hocuscoquus.hocuscoquus.model.CalendarEntryRequest;
import com.hocuscoquus.hocuscoquus.model.CalendarEntryResponse;
import com.hocuscoquus.hocuscoquus.model.CalendarScheduleRecipeRequest;
import com.hocuscoquus.hocuscoquus.service.CalendarService;

@WebMvcTest(CalendarApiController.class)
@Import(SecurityConfig.class)
class UpdateMealPlanningCalendarControllerTest {
    private static final String BASE = "/api/calendar/2";
    private static final Instant DATE = Instant.parse("2026-12-01T18:00:00Z");
    private static final String ENTRY_BODY = "{\"dateTime\":\"2026-12-01T18:00:00Z\"}";
    private static final String RECIPE_BODY = "{\"recipeId\":4}";
    private static final String SCHEDULE_BODY = "{\"recipeId\":4,\"dateTime\":\"2026-12-01T18:00:00Z\"}";

    @Autowired private MockMvc mockMvc;
    @MockitoBean private CalendarService service;

    private AccountDetails testUser() {
        Account account = new Account();
        account.setAccountId(1L);
        account.setFirstName("Test");
        account.setEmail("test@example.com");
        account.setPassword("storedHash");
        return new AccountDetails(account);
    }

    private CalendarEntryResponse response() {
        return new CalendarEntryResponse(3L, 2L, DATE,
                List.of(new CalendarEntryResponse.RecipeSummary(4L, "Pasta")));
    }

    @Test void getEntries_returnsRecipeSummaries() throws Exception {
        when(service.getEntries(1L, 2L)).thenReturn(List.of(response()));
        mockMvc.perform(get(BASE + "/entries").with(user(testUser())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].calendarEntryId").value(3))
                .andExpect(jsonPath("$[0].recipes[0].recipeName").value("Pasta"));
        verify(service).getEntries(1L, 2L);
    }

    @Test void addEntry_returnsCreated() throws Exception {
        when(service.addEntry(1L, 2L, new CalendarEntryRequest(DATE))).thenReturn(
                new CalendarEntryResponse(3L, 2L, DATE, List.of()));
        mockMvc.perform(post(BASE + "/entries/create").with(user(testUser())).with(csrf())
                .contentType(MediaType.APPLICATION_JSON).content(ENTRY_BODY))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.calendarEntryId").value(3))
                .andExpect(jsonPath("$.recipes").isEmpty());
        verify(service).addEntry(1L, 2L, new CalendarEntryRequest(DATE));
    }

    @Test void updateEntry_returnsOk() throws Exception {
        when(service.updateEntry(1L, 2L, 3L, new CalendarEntryRequest(DATE))).thenReturn(response());
        mockMvc.perform(put(BASE + "/entries/update/3").with(user(testUser())).with(csrf())
                .contentType(MediaType.APPLICATION_JSON).content(ENTRY_BODY))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.calendarEntryId").value(3));
        verify(service).updateEntry(1L, 2L, 3L, new CalendarEntryRequest(DATE));
    }

    @Test void deleteEntry_returnsNoContent() throws Exception {
        mockMvc.perform(delete(BASE + "/entries/delete/3").with(user(testUser())).with(csrf()))
                .andExpect(status().isNoContent());
        verify(service).deleteEntry(1L, 2L, 3L);
    }

    @Test void addRecipe_returnsOk() throws Exception {
        when(service.addRecipe(1L, 2L, 3L, new CalendarEntryRecipeRequest(4L))).thenReturn(response());
        mockMvc.perform(post(BASE + "/entries/3/recipes/add").with(user(testUser())).with(csrf())
                .contentType(MediaType.APPLICATION_JSON).content(RECIPE_BODY))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.recipes[0].recipeId").value(4));
        verify(service).addRecipe(1L, 2L, 3L, new CalendarEntryRecipeRequest(4L));
    }

    @Test void removeRecipe_returnsNoContent() throws Exception {
        mockMvc.perform(delete(BASE + "/entries/3/recipes/remove/4").with(user(testUser())).with(csrf()))
                .andExpect(status().isNoContent());
        verify(service).removeRecipe(1L, 2L, 3L, 4L);
    }

    @Test void scheduleRecipe_returnsCreated() throws Exception {
        when(service.scheduleRecipe(1L, 2L, new CalendarScheduleRecipeRequest(4L, DATE))).thenReturn(response());
        mockMvc.perform(post(BASE + "/recipes/schedule").with(user(testUser())).with(csrf())
                .contentType(MediaType.APPLICATION_JSON).content(SCHEDULE_BODY))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.calendarId").value(2))
                .andExpect(jsonPath("$.recipes[0].recipeId").value(4));
        verify(service).scheduleRecipe(1L, 2L, new CalendarScheduleRecipeRequest(4L, DATE));
    }

    @Test void addEntry_missingDate_returnsBadRequest() throws Exception {
        mockMvc.perform(post(BASE + "/entries/create").with(user(testUser())).with(csrf())
                .contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().isBadRequest());
        verifyNoInteractions(service);
    }

    @Test void updateEntry_invalidDate_returnsBadRequest() throws Exception {
        mockMvc.perform(put(BASE + "/entries/update/3").with(user(testUser())).with(csrf())
                .contentType(MediaType.APPLICATION_JSON).content("{\"dateTime\":\"invalid\"}"))
                .andExpect(status().isBadRequest());
        verifyNoInteractions(service);
    }

    @Test void addRecipe_invalidId_returnsBadRequest() throws Exception {
        mockMvc.perform(post(BASE + "/entries/3/recipes/add").with(user(testUser())).with(csrf())
                .contentType(MediaType.APPLICATION_JSON).content("{\"recipeId\":0}"))
                .andExpect(status().isBadRequest());
        verifyNoInteractions(service);
    }

    @Test void scheduleRecipe_missingRecipeId_returnsBadRequest() throws Exception {
        mockMvc.perform(post(BASE + "/recipes/schedule").with(user(testUser())).with(csrf())
                .contentType(MediaType.APPLICATION_JSON).content(ENTRY_BODY))
                .andExpect(status().isBadRequest());
        verifyNoInteractions(service);
    }

    @Test void scheduleRecipe_duplicate_returnsConflict() throws Exception {
        when(service.scheduleRecipe(1L, 2L, new CalendarScheduleRecipeRequest(4L, DATE)))
                .thenThrow(new ResponseStatusException(HttpStatus.CONFLICT));
        mockMvc.perform(post(BASE + "/recipes/schedule").with(user(testUser())).with(csrf())
                .contentType(MediaType.APPLICATION_JSON).content(SCHEDULE_BODY))
                .andExpect(status().isConflict());
    }

    @Test void getEntries_inaccessibleCalendar_returnsNotFound() throws Exception {
        when(service.getEntries(1L, 2L)).thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND));
        mockMvc.perform(get(BASE + "/entries").with(user(testUser())))
                .andExpect(status().isNotFound());
    }

    @Test void scheduleRecipe_unauthenticated_redirectsToLogin() throws Exception {
        mockMvc.perform(post(BASE + "/recipes/schedule").with(csrf())
                .contentType(MediaType.APPLICATION_JSON).content(SCHEDULE_BODY))
                .andExpect(status().isFound())
                .andExpect(redirectedUrl("/login"));
        verifyNoInteractions(service);
    }

    @Test void removeRecipe_unauthenticated_redirectsToLogin() throws Exception {
        mockMvc.perform(delete(BASE + "/entries/3/recipes/remove/4").with(csrf()))
                .andExpect(status().isFound())
                .andExpect(redirectedUrl("/login"));
        verifyNoInteractions(service);
    }
}
