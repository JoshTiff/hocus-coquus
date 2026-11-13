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
        List<@NotNull @Valid IngredientRow> ingredients) {

    public RecipeEditRequest(String recipeName, String recipeDescription, String instructions, String image) {
        this(recipeName, recipeDescription, instructions, image, null);
    }

    public RecipeEditRequest withIngredients(List<IngredientRow> rows) {
        return new RecipeEditRequest(recipeName, recipeDescription, instructions, image, rows);
    }

    public record IngredientRow(
            @NotBlank @Size(max = 255) String ingredient,
            @NotNull @Positive Double quantity,
            @Size(max = 255) String measurement) {
    }
}
