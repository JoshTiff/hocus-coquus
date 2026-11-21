package com.hocuscoquus.hocuscoquus.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record RecipeNoteUpdateRequest(
    @NotNull
    @Positive
    Long recipeNoteId,

    @NotBlank
    @Size(max = 255)
    String message
) {}
