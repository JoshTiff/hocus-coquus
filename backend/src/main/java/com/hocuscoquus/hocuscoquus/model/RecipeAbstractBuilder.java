package com.hocuscoquus.hocuscoquus.model;

import java.util.List;

public abstract class RecipeAbstractBuilder {
    protected String name;
    protected String description;
    protected List<Ingredient> ingredients = List.of();
    protected String instructions;
    protected String image;

    public RecipeAbstractBuilder buildRecipeName(String name) {
        this.name = name;
        return this;
    }

    public RecipeAbstractBuilder buildRecipeDescription(String description) {
        this.description = description;
        return this;
    }

    public RecipeAbstractBuilder buildIngredients(List<Ingredient> ingredients) {
        this.ingredients = List.copyOf(ingredients);
        return this;
    }

    public RecipeAbstractBuilder buildInstructions(String instructions) {
        this.instructions = instructions;
        return this;
    }

    public RecipeAbstractBuilder buildImage(String image) {
        this.image = image;
        return this;
    }

    // public abstract Recipe build();
}
