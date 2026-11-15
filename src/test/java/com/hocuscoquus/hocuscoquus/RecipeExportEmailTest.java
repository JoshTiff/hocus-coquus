package com.hocuscoquus.hocuscoquus;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.hocuscoquus.hocuscoquus.entity.Ingredient;
import com.hocuscoquus.hocuscoquus.entity.Recipe;
import com.hocuscoquus.hocuscoquus.entity.RecipeIngredient;
import com.hocuscoquus.hocuscoquus.service.export.RecipeExportEmail;

/** Tests that recipe email exports build valid, encoded mailto URIs. */
class RecipeExportEmailTest {

    /** Ensures recipe content is included and encoded in the generated URI. */
    @Test
    void export_buildsPrefilledMailtoUriWithEncodedRecipeContents() {
        Ingredient tomato = new Ingredient();
        tomato.setIngredientName("tomato & basil");
        RecipeIngredient recipeIngredient = new RecipeIngredient();
        recipeIngredient.setIngredient(tomato);
        recipeIngredient.setQuantity(1.0);
        recipeIngredient.setMeasurement("cup");

        Recipe recipe = Recipe.builder()
                .setRecipeName("Tomato & Basil")
                .setRecipeDescription("A quick lunch")
                .setImage("https://example.com/image?a=1&b=2")
                .setInstructions("Mix & serve")
                .setRecipeIngredients(new ArrayList<>(List.of(recipeIngredient)))
                .build();

        RecipeExportEmail exporter = new RecipeExportEmail("friend@example.com");
        assertTrue(exporter.export(recipe));
        String mailtoUri = exporter.getMailtoUri();

        assertTrue(mailtoUri.startsWith("mailto:friend@example.com?subject="));
        assertTrue(mailtoUri.contains("Tomato%20%26%20Basil"));
        assertTrue(mailtoUri.contains("tomato%20%26%20basil"));
        assertTrue(mailtoUri.contains("https%3A%2F%2Fexample.com%2Fimage%3Fa%3D1%26b%3D2"));
        assertTrue(mailtoUri.contains("Mix%20%26%20serve"));
    }

    /** Ensures an exporter cannot be created without a recipient address. */
    @Test
    void export_requiresRecipientEmail() {
        org.junit.jupiter.api.Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> new RecipeExportEmail(" "));
    }
}
