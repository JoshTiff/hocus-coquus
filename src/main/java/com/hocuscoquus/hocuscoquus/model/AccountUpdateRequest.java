package com.hocuscoquus.hocuscoquus.model;

import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record AccountUpdateRequest(

    String currentPassword,

    String newPassword,

    List<@NotNull @Positive Long> ingredientIds,

    List<@NotBlank @Size(max = 255) String> dietaryRestrictionNames

) {}
