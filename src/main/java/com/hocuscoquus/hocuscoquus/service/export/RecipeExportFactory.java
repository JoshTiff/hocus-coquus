package com.hocuscoquus.hocuscoquus.service.export;

import org.springframework.stereotype.Component;

/** Creates the exporter for a requested recipe export format. */
@Component
public class RecipeExportFactory {
    /**
     * Creates an exporter for the requested format.
     *
     * @param type export format to create
     * @return exporter for {@code type}
     * @throws UnsupportedOperationException if the requested format is not implemented
     */
    public RecipeExportAbstract getRecipeExportAbstraction(RecipeExportType type) {
        return switch (type) {
            case EMAIL -> new RecipeExportEmail();
            case PRINT -> new RecipeExportPrint();
            case DOWNLOAD -> throw new UnsupportedOperationException(
                    "Recipe export is not implemented: " + type);
        };
    }
}
