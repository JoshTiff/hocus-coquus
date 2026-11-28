package com.hocuscoquus.hocuscoquus.model;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record CalendarEntryRecipeRequest(
        @NotNull @Positive Long recipeId
) {
}