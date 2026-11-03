package com.hocuscoquus.hocuscoquus.repository;

import java.util.List;

import com.hocuscoquus.hocuscoquus.entity.Calendar;

public interface CalendarRepository {
    Calendar findById(long calendarId);
    List<Calendar> findByAccount(long accountId);
}
