package com.hocuscoquus.hocuscoquus.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RecipeEditRequest(
        @NotBlank @Size(max = 255) String recipeName,
        @Size(max = 255) String recipeDescription,
        @Size(max = 255) String instructions,
        @Size(max = 255) String image) {
}
