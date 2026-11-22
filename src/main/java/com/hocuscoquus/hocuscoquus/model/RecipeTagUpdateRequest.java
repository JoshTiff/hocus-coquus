package com.hocuscoquus.hocuscoquus.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RecipeTagUpdateRequest(
        @NotBlank
        @Size(max = 255)
        String tag
) {}
