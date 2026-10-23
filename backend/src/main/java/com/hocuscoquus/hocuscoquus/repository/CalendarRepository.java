package com.hocuscoquus.hocuscoquus.repository;

import java.util.List;

import com.hocuscoquus.hocuscoquus.model.Calendar;

public interface CalendarRepository {
    Calendar findById(long calendarId);
    List<Calendar> findByAccount(long accountId);
}
