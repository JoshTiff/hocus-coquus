package com.hocuscoquus.hocuscoquus.service.export;

import java.math.BigDecimal;
import java.util.Objects;

import org.springframework.web.util.HtmlUtils;

import com.hocuscoquus.hocuscoquus.entity.Recipe;
import com.hocuscoquus.hocuscoquus.entity.RecipeIngredient;

/** Formats a recipe as a simple HTML page for the browser's print dialog. */
public class RecipeExportPrint extends RecipeExportAbstract {
    private String printHtml;

    /**
     * Builds an HTML page containing the recipe name, ingredients, and instructions.
     *
     * @param recipe recipe to print
     * @return {@code true} after the HTML is built
     * @throws NullPointerException if {@code recipe} is null
     */
    @Override
    public boolean export(Recipe recipe) {
        Objects.requireNonNull(recipe, "Recipe is required");
        printHtml = formatRecipe(recipe);
        return true;
    }

    /**
     * Returns the HTML produced by {@link #export(Recipe)}.
     *
     * @return generated printable HTML
     * @throws IllegalStateException if {@link #export(Recipe)} has not been called
     */
    public String getPrintHtml() {
        if (printHtml == null) {
            throw new IllegalStateException("Recipe print page has not been exported");
        }
        return printHtml;
    }

    /**
     * Formats the recipe name, ingredients, and instructions as an HTML page.
     *
     * @param recipe recipe to format
     * @return HTML page with escaped recipe text
     */
    private String formatRecipe(Recipe recipe) {
        String name = HtmlUtils.htmlEscape(recipe.getRecipeName());

        StringBuilder html = new StringBuilder("<!DOCTYPE html>\n<html lang=\"en\">\n<head>\n")
                .append("<meta charset=\"UTF-8\">\n")
                .append("<title>").append(name).append("</title>\n")
                .append("</head>\n<body>\n")
                .append("<h1>").append(name).append("</h1>\n");

        html.append("<h2>Ingredients</h2>\n<ul>\n");
        for (RecipeIngredient ingredient : recipe.getRecipeIngredients()) {
            html.append("<li>").append(formatIngredient(ingredient)).append("</li>\n");
        }
        html.append("</ul>\n");

        html.append("<h2>Instructions</h2>\n<ul>\n");
        appendInstructions(html, recipe.getInstructions());
        html.append("</ul>\n");

        html.append("</body>\n</html>");
        return html.toString();
    }

    /**
     * Formats one ingredient line as escaped text.
     *
     * @param ingredient ingredient to format
     * @return quantity, measurement, and ingredient name
     */
    private String formatIngredient(RecipeIngredient ingredient) {
        StringBuilder line = new StringBuilder(
                BigDecimal.valueOf(ingredient.getQuantity()).stripTrailingZeros().toPlainString());
        appendInline(line, ingredient.getMeasurement());
        if (ingredient.getIngredient() != null) {
            appendInline(line, ingredient.getIngredient().getIngredientName());
        }
        return HtmlUtils.htmlEscape(line.toString());
    }

    /**
     * Adds each nonblank line of the instructions as a list item.
     *
     * @param html page being assembled
     * @param instructions recipe instructions; may be {@code null}
     */
    private void appendInstructions(StringBuilder html, String instructions) {
        if (instructions == null) {
            return;
        }
        for (String step : instructions.split("\\R")) {
            if (!step.isBlank()) {
                html.append("<li>").append(HtmlUtils.htmlEscape(step.trim())).append("</li>\n");
            }
        }
    }

    /**
     * Adds a nonblank value to the current line.
     *
     * @param line line being assembled
     * @param value detail to append, if nonblank
     */
    private void appendInline(StringBuilder line, String value) {
        if (value != null && !value.isBlank()) {
            line.append(' ').append(value);
        }
    }
}
