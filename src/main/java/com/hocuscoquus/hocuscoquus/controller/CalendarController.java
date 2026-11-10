package com.hocuscoquus.hocuscoquus.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.hocuscoquus.hocuscoquus.entity.AccountDetails;
import com.hocuscoquus.hocuscoquus.model.CalendarCreateDto;
import com.hocuscoquus.hocuscoquus.service.CalendarService;

@Controller
public class CalendarController {
    private final CalendarService calendarService;

    public CalendarController(CalendarService calendarService) {
        this.calendarService = calendarService;
    }

    @GetMapping("/calendar")
    public String calendar(
            @AuthenticationPrincipal AccountDetails details,
            @RequestParam(required = false) Long calendarId,
            Model model) {
        if (calendarId != null) {
            model.addAttribute("currentCalendar", calendarService.getCalendarForAccount(calendarId, details.getAccountId()));
        }
        return "calendar";
    }

    @GetMapping("/calendar/switch")
    public String switchCalendarPage(
            @AuthenticationPrincipal AccountDetails details,
            Model model) {
        model.addAttribute("calendars", calendarService.getCalendarsForAccount(details.getAccountId()));
        return "calendar-switch";
    }

    @PostMapping("/api/calendar/create")
    public String createCalendar(
            @AuthenticationPrincipal AccountDetails details,
            @ModelAttribute("calendarCreateDto") CalendarCreateDto calendarCreateDto,
            Model model) {
        try {
            long calendarId = calendarService.createCalendar(calendarCreateDto, details.getAccountId()).getCalendarId();
            return "redirect:/calendar?calendarId=" + calendarId;
        } catch (IllegalArgumentException ex) {
            model.addAttribute("calendarCreateError", ex.getMessage());
            return "calendar-create";
        }
    }

    @PostMapping("/api/calendar/switch")
    public String switchCalendar(
            @AuthenticationPrincipal AccountDetails details,
            @RequestParam long calendarId,
            Model model) {
        if (calendarService.getCalendarForAccount(calendarId, details.getAccountId()) == null) {
            model.addAttribute("calendarSwitchError", "Calendar not found.");
            model.addAttribute("calendars", calendarService.getCalendarsForAccount(details.getAccountId()));
            return "calendar-switch";
        }
        return "redirect:/calendar?calendarId=" + calendarId;
    }

}
