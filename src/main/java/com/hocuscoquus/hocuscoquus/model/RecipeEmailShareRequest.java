package com.hocuscoquus.hocuscoquus.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record RecipeEmailShareRequest(
        @NotBlank @Email String recipientEmail) {
}
