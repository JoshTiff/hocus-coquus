package com.hocuscoquus.hocuscoquus.model;

import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

/**
 * Request body for creating a dietary restriction with manually selected ingredients.
 *
 * @param dietaryRestrictionName human-readable restriction name, such as "peanut-free"
 * @param ingredientIds saved ingredient IDs that the restriction excludes
 */
public record DietaryRestrictionCreateRequest(
        @NotBlank @Size(max = 255) String dietaryRestrictionName,
        List<@NotNull @Positive Long> ingredientIds) {
}
