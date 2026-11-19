package com.hocuscoquus.hocuscoquus.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * Request data for sharing a recipe by email.
 *
 * @param recipientEmail address that should receive the prefilled recipe email
 */
public record RecipeEmailShareRequest(
        @NotBlank @Email String recipientEmail) {
}
