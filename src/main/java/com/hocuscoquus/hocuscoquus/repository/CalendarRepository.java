package com.hocuscoquus.hocuscoquus.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hocuscoquus.hocuscoquus.entity.Calendar;

public interface CalendarRepository extends JpaRepository<Calendar, Long> {
    Calendar findById(long calendarId);

    Calendar findByCalendarIdAndAccount_AccountId(long calendarId, long accountId);
    
    List<Calendar> findByAccount_AccountId(long accountId);
}
