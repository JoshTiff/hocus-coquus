package com.hocuscoquus.hocuscoquus.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hocuscoquus.hocuscoquus.entity.CalendarEntry;

public interface CalendarEntryRepository extends JpaRepository<CalendarEntry, Long> {
    CalendarEntry findById(long calendarEntryId);
    List<CalendarEntry> findByCalendar(long calendarId);
}
