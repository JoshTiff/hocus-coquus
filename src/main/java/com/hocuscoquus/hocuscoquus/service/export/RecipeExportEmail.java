package com.hocuscoquus.hocuscoquus.service.export;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

import org.springframework.web.util.UriUtils;

import com.hocuscoquus.hocuscoquus.entity.Recipe;
import com.hocuscoquus.hocuscoquus.entity.RecipeIngredient;

public class RecipeExportEmail extends RecipeExportAbstract {
    private final String recipientEmail;
    private String mailtoUri;

    public RecipeExportEmail(String recipientEmail) {
        if (recipientEmail == null || recipientEmail.isBlank()) {
            throw new IllegalArgumentException("Recipient email is required");
        }
        this.recipientEmail = recipientEmail.trim();
    }

    @Override
    public boolean export(Recipe recipe) {
        Objects.requireNonNull(recipe, "Recipe is required");
        String subject = "Recipe: " + recipe.getRecipeName();
        String body = formatRecipe(recipe);
        mailtoUri = "mailto:" + encodeRecipient(recipientEmail)
                + "?subject=" + encodeHeaderValue(subject)
                + "&body=" + encodeHeaderValue(body);
        return true;
    }

    public String getMailtoUri() {
        if (mailtoUri == null) {
            throw new IllegalStateException("Recipe email has not been exported");
        }
        return mailtoUri;
    }

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

    private void appendSection(StringBuilder body, String value) {
        if (value != null && !value.isBlank()) {
            body.append("\n\n").append(value);
        }
    }

    private void appendOptionalLine(StringBuilder body, String label, String value) {
        if (value != null && !value.isBlank()) {
            body.append("\n");
            if (label != null) {
                body.append(label).append(": ");
            }
            body.append(value);
        }
    }

    private void appendInline(StringBuilder body, String value) {
        if (value != null && !value.isBlank()) {
            body.append(' ').append(value);
        }
    }
}
