package com.hocuscoquus.hocuscoquus.model;

import java.time.Instant;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record CalendarScheduleRecipeRequest(
        @NotNull @Positive Long recipeId,
        @NotNull Instant dateTime
) {
}