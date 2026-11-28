package com.hocuscoquus.hocuscoquus.controller;

import java.util.List;
import java.util.Map;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hocuscoquus.hocuscoquus.entity.AccountDetails;
import com.hocuscoquus.hocuscoquus.model.CalendarCreateDto;
import com.hocuscoquus.hocuscoquus.model.CalendarEntryRecipeRequest;
import com.hocuscoquus.hocuscoquus.model.CalendarEntryRequest;
import com.hocuscoquus.hocuscoquus.model.CalendarEntryResponse;
import com.hocuscoquus.hocuscoquus.model.CalendarScheduleRecipeRequest;
import com.hocuscoquus.hocuscoquus.service.CalendarService;

@RestController
@RequestMapping("/api/calendar")
public class CalendarApiController {
    private final CalendarService calendarService;

    public CalendarApiController(CalendarService calendarService) {
        this.calendarService = calendarService;
    }

    @PostMapping(path = "/create", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, ?>> createCalendar(
            @AuthenticationPrincipal AccountDetails details,
            @RequestBody CalendarCreateDto request) {
        try {
            long calendarId = calendarService.createCalendar(request, details.getAccountId()).getCalendarId();
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("calendarId", calendarId));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(Map.of("message", ex.getMessage()));
        }
    }

    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> getCalendars(
            @AuthenticationPrincipal AccountDetails details) {

        return ResponseEntity.ok(
                calendarService.getCalendarsForAccount(
                        details.getAccountId())
                        .stream()
                        .map(calendar -> Map.<String, Object>of(
                                "calendarId", calendar.getCalendarId(),
                                "calendarName", calendar.getCalendarName()))
                        .toList());
    }

    @GetMapping("/{calendarId}/entries")
    public ResponseEntity<List<CalendarEntryResponse>> getEntries(
            @AuthenticationPrincipal AccountDetails details,
            @PathVariable long calendarId) {

        return ResponseEntity.ok(
                calendarService.getEntries(
                        details.getAccountId(), calendarId));
    }

    @PostMapping("/{calendarId}/entries/create")
    public ResponseEntity<CalendarEntryResponse> addEntry(
            @AuthenticationPrincipal AccountDetails details,
            @PathVariable long calendarId,
            @Valid @RequestBody CalendarEntryRequest request) {

        CalendarEntryResponse response = calendarService.addEntry(
                details.getAccountId(), calendarId, request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{calendarId}/entries/update/{calendarEntryId}")
    public ResponseEntity<CalendarEntryResponse> updateEntry(
            @AuthenticationPrincipal AccountDetails details,
            @PathVariable long calendarId,
            @PathVariable long calendarEntryId,
            @Valid @RequestBody CalendarEntryRequest request) {

        return ResponseEntity.ok(
                calendarService.updateEntry(
                        details.getAccountId(),
                        calendarId,
                        calendarEntryId,
                        request));
    }

    @DeleteMapping("/{calendarId}/entries/delete/{calendarEntryId}")
    public ResponseEntity<Void> deleteEntry(
            @AuthenticationPrincipal AccountDetails details,
            @PathVariable long calendarId,
            @PathVariable long calendarEntryId) {

        calendarService.deleteEntry(
                details.getAccountId(),
                calendarId,
                calendarEntryId);

        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{calendarId}/entries/{calendarEntryId}/recipes/add")
    public ResponseEntity<CalendarEntryResponse> addRecipe(
            @AuthenticationPrincipal AccountDetails details,
            @PathVariable long calendarId,
            @PathVariable long calendarEntryId,
            @Valid @RequestBody CalendarEntryRecipeRequest request) {

        return ResponseEntity.ok(
                calendarService.addRecipe(
                        details.getAccountId(),
                        calendarId,
                        calendarEntryId,
                        request));
    }

    @DeleteMapping("/{calendarId}/entries/{calendarEntryId}/recipes/remove/{recipeId}")
    public ResponseEntity<Void> removeRecipe(
            @AuthenticationPrincipal AccountDetails details,
            @PathVariable long calendarId,
            @PathVariable long calendarEntryId,
            @PathVariable long recipeId) {

        calendarService.removeRecipe(
                details.getAccountId(),
                calendarId,
                calendarEntryId,
                recipeId);

        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{calendarId}/recipes/schedule")
    public ResponseEntity<CalendarEntryResponse> scheduleRecipe(
            @AuthenticationPrincipal AccountDetails details,
            @PathVariable long calendarId,
            @Valid @RequestBody CalendarScheduleRecipeRequest request) {

        return ResponseEntity.status(HttpStatus.CREATED).body(
                calendarService.scheduleRecipe(
                        details.getAccountId(),
                        calendarId,
                        request));
    }
}
