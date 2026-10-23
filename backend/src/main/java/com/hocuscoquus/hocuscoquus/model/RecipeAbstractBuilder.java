package com.hocuscoquus.hocuscoquus.model;

import java.util.List;

public abstract class RecipeAbstractBuilder {
    protected String name;
    protected String description;
    protected List<Ingredient> ingredients = List.of();
    protected String instructions;
    protected String image;

    public RecipeAbstractBuilder buildRecipeName(String name) {
        // TODO: add implementation for this method

        this.name = name;
        return this;
    }

    public RecipeAbstractBuilder buildRecipeDescription(String description) {
        // TODO: add implementation for this method
        
        this.description = description;
        return this;
    }

    public RecipeAbstractBuilder buildIngredients(List<Ingredient> ingredients) {
        // TODO: add implementation for this method
        
        this.ingredients = List.copyOf(ingredients);
        return this;
    }

    public RecipeAbstractBuilder buildInstructions(String instructions) {
        // TODO: add implementation for this method
        
        this.instructions = instructions;
        return this;
    }

    public RecipeAbstractBuilder buildImage(String image) {
        // TODO: add implementation for this method
        
        this.image = image;
        return this;
    }

}
