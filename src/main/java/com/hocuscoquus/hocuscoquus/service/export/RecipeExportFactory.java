package com.hocuscoquus.hocuscoquus.service.export;

import org.springframework.stereotype.Component;

@Component
public class RecipeExportFactory {
    public RecipeExportAbstract getRecipeExportAbstraction(
            RecipeExportType type, String recipientEmail) {
        return switch (type) {
            case EMAIL -> new RecipeExportEmail(recipientEmail);
            case PRINT, DOWNLOAD -> throw new UnsupportedOperationException(
                    "Recipe export is not implemented: " + type);
        };
    }
}
