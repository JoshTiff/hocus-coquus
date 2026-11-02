package com.hocuscoquus.hocuscoquus.controller;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class CalendarController {

	private static final YearMonth FIRST_CALENDAR_MONTH = YearMonth.of(2025, 12);
	private static final YearMonth LAST_CALENDAR_MONTH = YearMonth.of(2027, 12);
	private static final DateTimeFormatter MONTH_FORMATTER = DateTimeFormatter.ofPattern("MMMM", Locale.US);

	@GetMapping("/calendar")
	public String calendar(
			@RequestParam(required = false) Integer year,
			@RequestParam(required = false) Integer month,
			Model model) {
		YearMonth selectedMonth = selectedMonth(year, month);
		YearMonth previousMonth = selectedMonth.minusMonths(1);
		YearMonth nextMonth = selectedMonth.plusMonths(1);

		model.addAttribute("monthName", selectedMonth.format(MONTH_FORMATTER));
		model.addAttribute("year", selectedMonth.getYear());
		model.addAttribute("days", daysFor(selectedMonth));
		model.addAttribute("previousMonth", previousMonth);
		model.addAttribute("nextMonth", nextMonth);
		model.addAttribute("canGoPrevious", selectedMonth.isAfter(FIRST_CALENDAR_MONTH));
		model.addAttribute("canGoNext", selectedMonth.isBefore(LAST_CALENDAR_MONTH));

		return "calendar";
	}

	private YearMonth selectedMonth(Integer year, Integer month) {
		YearMonth currentMonth = YearMonth.now();
		YearMonth fallbackMonth = currentMonth.isBefore(FIRST_CALENDAR_MONTH)
				? FIRST_CALENDAR_MONTH
				: currentMonth.isAfter(LAST_CALENDAR_MONTH) ? LAST_CALENDAR_MONTH : currentMonth;

		if (year == null || month == null) {
			return fallbackMonth;
		}

		try {
			YearMonth requestedMonth = YearMonth.of(year, month);
			if (requestedMonth.isBefore(FIRST_CALENDAR_MONTH)) {
				return FIRST_CALENDAR_MONTH;
			}
			if (requestedMonth.isAfter(LAST_CALENDAR_MONTH)) {
				return LAST_CALENDAR_MONTH;
			}
			return requestedMonth;
		} catch (DateTimeException exception) {
			return fallbackMonth;
		}
	}

	private List<LocalDate> daysFor(YearMonth month) {
		List<LocalDate> days = new ArrayList<>();
		int leadingBlankDays = month.atDay(1).getDayOfWeek().getValue() % 7;

		for (int day = 0; day < leadingBlankDays; day++) {
			days.add(null);
		}
		for (int day = 1; day <= month.lengthOfMonth(); day++) {
			days.add(month.atDay(day));
		}
		while (days.size() % 7 != 0) {
			days.add(null);
		}

		return days;
	}
}
