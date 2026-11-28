package com.hocuscoquus.hocuscoquus.model;

import java.time.Instant;

import jakarta.validation.constraints.NotNull;

public record CalendarEntryRequest(
        @NotNull Instant dateTime
) {
}