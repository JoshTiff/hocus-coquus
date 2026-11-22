package com.hocuscoquus.hocuscoquus.service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.hocuscoquus.hocuscoquus.entity.Recipe;
import com.hocuscoquus.hocuscoquus.repository.RecipeRepository;
import com.hocuscoquus.hocuscoquus.service.export.RecipeExportAbstract;
import com.hocuscoquus.hocuscoquus.service.export.RecipeExportEmail;
import com.hocuscoquus.hocuscoquus.service.export.RecipeExportPrint;
import com.hocuscoquus.hocuscoquus.service.export.RecipeExportFactory;
import com.hocuscoquus.hocuscoquus.service.export.RecipeExportType;

/** Coordinates loading a recipe and creating its email export. */
@Service
public class RecipeExportService {
    private final RecipeRepository recipeRepository;
    private final RecipeExportFactory recipeExportFactory;

    /**
     * Creates the service with its recipe repository and exporter factory.
     *
     * @param recipeRepository repository used to load recipes
     * @param recipeExportFactory factory that creates the requested exporter
     */
    public RecipeExportService(
            RecipeRepository recipeRepository,
            RecipeExportFactory recipeExportFactory) {
        this.recipeRepository = recipeRepository;
        this.recipeExportFactory = recipeExportFactory;
    }

    /**
     * Builds a prefilled mailto URI for a recipe.
     *
     * @param recipeId ID of the recipe to share
     * @param recipientEmail email address to place in the mailto URI
     * @return encoded mailto URI with the recipe details
     * @throws ResponseStatusException if the recipe does not exist
     * @throws IllegalStateException if the email exporter fails or does not provide a URI
     */
    @Transactional(readOnly = true)
    public String shareRecipeByEmail(long recipeId, String recipientEmail) {
        Recipe recipe = recipeRepository.findByRecipeId(recipeId);
        if (recipe == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Recipe not found");
        }

        RecipeExportAbstract exporter = recipeExportFactory
                .getRecipeExportAbstraction(RecipeExportType.EMAIL);
        if (!(exporter instanceof RecipeExportEmail emailExporter)) {
            throw new IllegalStateException("Email export did not provide a mailto URI");
        }
        emailExporter.setRecipientEmail(recipientEmail);
        if (!exporter.export(recipe)) {
            throw new IllegalStateException("Recipe email export failed");
        }
        return emailExporter.getMailtoUri();
    }

    /**
     * Builds a printable HTML page for a recipe.
     *
     * @param recipeId ID of the recipe to print
     * @return HTML page with the recipe details
     * @throws ResponseStatusException if the recipe does not exist
     * @throws IllegalStateException if the print exporter fails or does not provide HTML
     */
    @Transactional(readOnly = true)
    public String printRecipe(long recipeId) {
        Recipe recipe = recipeRepository.findByRecipeId(recipeId);
        if (recipe == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Recipe not found");
        }

        RecipeExportAbstract exporter = recipeExportFactory
                .getRecipeExportAbstraction(RecipeExportType.PRINT);
        if (!(exporter instanceof RecipeExportPrint printExporter)) {
            throw new IllegalStateException("Print export did not provide HTML");
        }
        if (!exporter.export(recipe)) {
            throw new IllegalStateException("Recipe print export failed");
        }
        return printExporter.getPrintHtml();
    }
}
