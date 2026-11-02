package com.hocuscoquus.hocuscoquus.repository;

import java.util.List;

import com.hocuscoquus.hocuscoquus.entity.CalendarEntry;

public interface CalendarEntryRepository {
    CalendarEntry findById(long calendarEntryId);
    List<CalendarEntry> findByCalendar(long calendarId);
}
