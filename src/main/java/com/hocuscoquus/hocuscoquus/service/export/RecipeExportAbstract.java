package com.hocuscoquus.hocuscoquus.service.export;

import com.hocuscoquus.hocuscoquus.entity.Recipe;

/** Common contract for exporting a recipe in a supported format. */
public abstract class RecipeExportAbstract {

    /**
     * Exports the given recipe in the format provided by an implementation.
     *
     * @param recipe recipe to export
     * @return {@code true} if the export succeeds; otherwise {@code false}
     */
    public abstract boolean export(Recipe recipe);
    
}
