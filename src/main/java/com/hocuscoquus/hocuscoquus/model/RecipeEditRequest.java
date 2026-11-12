package com.hocuscoquus.hocuscoquus.model;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record RecipeEditRequest(
        @NotBlank @Size(max = 255) String recipeName,
        @Size(max = 255) String recipeDescription,
        @Size(max = 255) String instructions,
        @Size(max = 255) String image,
        @Positive Integer numberServings,
        @NotNull List<@NotNull @Valid RecipeIngredientCreateRequest> ingredients
) {}
