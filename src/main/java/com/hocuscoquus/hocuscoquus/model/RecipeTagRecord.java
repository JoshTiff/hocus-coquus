package com.hocuscoquus.hocuscoquus.model;

public record RecipeTagRecord(
        long recipeTagId,
        long recipeId,
        long accountId,
        String tag
) {}