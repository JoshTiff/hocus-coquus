package com.hocuscoquus.hocuscoquus.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hocuscoquus.hocuscoquus.entity.AccountDetails;
import com.hocuscoquus.hocuscoquus.entity.CalendarEntry;
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

    @DeleteMapping("/api/calendars/{calendarId}")
    @ResponseBody
    public void deleteCalendar(
            @AuthenticationPrincipal AccountDetails details,
            @PathVariable long calendarId) {
        calendarService.deleteCalendar(details.getAccountId(), calendarId);
    }

    @PostMapping("/api/calendars/{calendarId}/entries")
    @ResponseBody
    public CalendarEntry addEntry(@PathVariable long calendarId, @RequestBody CalendarEntry entry) {
        return calendarService.addEntry(calendarId, entry);
    }

    @DeleteMapping("/api/calendars/entries/{calendarEntryId}")
    @ResponseBody
    public void deleteEntry(
            @AuthenticationPrincipal AccountDetails details,
            @PathVariable long calendarEntryId) {
        calendarService.deleteEntry(details.getAccountId(), calendarEntryId);
    }

    @PutMapping("/api/calendars/entries")
    @ResponseBody
    public CalendarEntry updateEntry(@RequestBody CalendarEntry entry) {
        return calendarService.updateEntry(entry);
    }

    // need printRecipe, downloadCalendar, syncGoogleAccount?

    // add way to go cycle months (Aidan can do this later)

}
