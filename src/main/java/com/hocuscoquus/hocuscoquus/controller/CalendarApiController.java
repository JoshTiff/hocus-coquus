package com.hocuscoquus.hocuscoquus.controller;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hocuscoquus.hocuscoquus.entity.AccountDetails;
import com.hocuscoquus.hocuscoquus.model.CalendarCreateDto;
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
}
