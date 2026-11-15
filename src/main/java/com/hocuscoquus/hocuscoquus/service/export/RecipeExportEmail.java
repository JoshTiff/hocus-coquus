package com.hocuscoquus.hocuscoquus.service.export;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

import org.springframework.web.util.UriUtils;

import com.hocuscoquus.hocuscoquus.entity.Recipe;
import com.hocuscoquus.hocuscoquus.entity.RecipeIngredient;

/** Formats a recipe as an encoded mailto URI for sharing by email. */
public class RecipeExportEmail extends RecipeExportAbstract {
    private String recipientEmail;
    private String mailtoUri;

    /**
     * Sets the address to include in the generated mailto URI.
     *
     * @param recipientEmail address to include in the generated mailto URI
     * @throws IllegalArgumentException if the address is null or blank
     */
    public void setRecipientEmail(String recipientEmail) {
        if (recipientEmail == null || recipientEmail.isBlank()) {
            throw new IllegalArgumentException("Recipient email is required");
        }
        this.recipientEmail = recipientEmail.trim();
    }

    /**
     * Builds a mailto URI with the recipe name as the subject and recipe details as the body.
     *
     * @param recipe recipe whose details will be shared
     * @return {@code true} after the mailto URI is built
     * @throws NullPointerException if {@code recipe} is null
     * @throws IllegalStateException if no recipient address has been set
     * @throws IllegalArgumentException if the recipient address is malformed
     */
    @Override
    public boolean export(Recipe recipe) {
        Objects.requireNonNull(recipe, "Recipe is required");
        if (recipientEmail == null) {
            throw new IllegalStateException("Recipient email has not been set");
        }
        String subject = "Recipe: " + recipe.getRecipeName();
        String body = formatRecipe(recipe);
        mailtoUri = "mailto:" + encodeRecipient(recipientEmail)
                + "?subject=" + encodeHeaderValue(subject)
                + "&body=" + encodeHeaderValue(body);
        return true;
    }

    /**
     * Returns the URI produced by {@link #export(Recipe)}.
     *
     * @return generated mailto URI
     * @throws IllegalStateException if {@link #export(Recipe)} has not been called
     */
    public String getMailtoUri() {
        if (mailtoUri == null) {
            throw new IllegalStateException("Recipe email has not been exported");
        }
        return mailtoUri;
    }

    /**
     * Encodes the recipient's local part and domain while preserving the address separator.
     *
     * @param recipientEmail recipient address to encode
     * @return encoded address with its {@code @} separator preserved
     * @throws IllegalArgumentException if the address has no local part or domain
     */
    private String encodeRecipient(String recipientEmail) {
        int separator = recipientEmail.lastIndexOf('@');
        if (separator <= 0 || separator == recipientEmail.length() - 1) {
            throw new IllegalArgumentException("Recipient email is invalid");
        }
        return UriUtils.encodePathSegment(recipientEmail.substring(0, separator), StandardCharsets.UTF_8)
                + "@"
                + UriUtils.encodePathSegment(recipientEmail.substring(separator + 1), StandardCharsets.UTF_8);
    }

    /**
     * Percent-encodes a mailto subject or body value so its contents cannot be
     * mistaken for URI query syntax, such as the separator between headers.
     * Only RFC 3986 unreserved ASCII characters remain literal; all other UTF-8
     * bytes are encoded.
     *
     * @param value the subject or body text to encode
     * @return the encoded value, safe to place in a mailto header parameter
     */
    private String encodeHeaderValue(String value) {
        StringBuilder encoded = new StringBuilder();
        for (byte character : value.getBytes(StandardCharsets.UTF_8)) {
            int unsignedCharacter = character & 0xFF;
            if ((unsignedCharacter >= 'a' && unsignedCharacter <= 'z')
                    || (unsignedCharacter >= 'A' && unsignedCharacter <= 'Z')
                    || (unsignedCharacter >= '0' && unsignedCharacter <= '9')
                    || unsignedCharacter == '-' || unsignedCharacter == '.'
                    || unsignedCharacter == '_' || unsignedCharacter == '~') {
                encoded.append((char) unsignedCharacter);
            } else {
                encoded.append('%')
                        .append("0123456789ABCDEF".charAt(unsignedCharacter >>> 4))
                        .append("0123456789ABCDEF".charAt(unsignedCharacter & 0x0F));
            }
        }
        return encoded.toString();
    }

    /**
     * Formats the recipe name, description, image, ingredients, and instructions as email text.
     *
     * @param recipe recipe to format
     * @return readable, unencoded recipe text for the email body
     */
    private String formatRecipe(Recipe recipe) {
        StringBuilder body = new StringBuilder(recipe.getRecipeName());
        appendSection(body, recipe.getRecipeDescription());
        appendOptionalLine(body, "Image", recipe.getImage());

        body.append("\n\nIngredients:");
        for (RecipeIngredient ingredient : recipe.getRecipeIngredients()) {
            body.append("\n- ")
                    .append(BigDecimal.valueOf(ingredient.getQuantity()).stripTrailingZeros().toPlainString());
            appendInline(body, ingredient.getMeasurement());
            if (ingredient.getIngredient() != null) {
                appendInline(body, ingredient.getIngredient().getIngredientName());
            }
        }

        body.append("\n\nInstructions:");
        appendOptionalLine(body, null, recipe.getInstructions());
        return body.toString();
    }

    /**
     * Adds a nonblank paragraph to the recipe body.
     *
     * @param body email body being assembled
     * @param value paragraph to append, if nonblank
     */
    private void appendSection(StringBuilder body, String value) {
        if (value != null && !value.isBlank()) {
            body.append("\n\n").append(value);
        }
    }

    /**
     * Adds a nonblank line, optionally prefixed with a label.
     *
     * @param body email body being assembled
     * @param label optional line label; may be {@code null}
     * @param value line content to append, if nonblank
     */
    private void appendOptionalLine(StringBuilder body, String label, String value) {
        if (value != null && !value.isBlank()) {
            body.append("\n");
            if (label != null) {
                body.append(label).append(": ");
            }
            body.append(value);
        }
    }

    /**
     * Adds a nonblank value to the current ingredient line.
     *
     * @param body email body being assembled
     * @param value ingredient detail to append, if nonblank
     */
    private void appendInline(StringBuilder body, String value) {
        if (value != null && !value.isBlank()) {
            body.append(' ').append(value);
        }
    }
}
