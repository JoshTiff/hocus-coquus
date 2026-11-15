package com.hocuscoquus.hocuscoquus.service;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.hocuscoquus.hocuscoquus.entity.Recipe;
import com.hocuscoquus.hocuscoquus.repository.RecipeRepository;
import com.hocuscoquus.hocuscoquus.service.export.RecipeExportAbstract;
import com.hocuscoquus.hocuscoquus.service.export.RecipeExportEmail;
import com.hocuscoquus.hocuscoquus.service.export.RecipeExportFactory;
import com.hocuscoquus.hocuscoquus.service.export.RecipeExportType;

@Service
public class RecipeExportService {
    private final RecipeRepository recipeRepository;
    private final RecipeExportFactory recipeExportFactory;

    public RecipeExportService(
            RecipeRepository recipeRepository,
            RecipeExportFactory recipeExportFactory) {
        this.recipeRepository = recipeRepository;
        this.recipeExportFactory = recipeExportFactory;
    }

    @Transactional(readOnly = true)
    public String shareRecipeByEmail(long recipeId, String recipientEmail) {
        Recipe recipe = recipeRepository.findByRecipeId(recipeId);
        if (recipe == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Recipe not found");
        }

        RecipeExportAbstract exporter = recipeExportFactory
                .getRecipeExportAbstraction(RecipeExportType.EMAIL, recipientEmail);
        if (!exporter.export(recipe)) {
            throw new IllegalStateException("Recipe email export failed");
        }
        if (!(exporter instanceof RecipeExportEmail emailExporter)) {
            throw new IllegalStateException("Email export did not provide a mailto URI");
        }
        return emailExporter.getMailtoUri();
    }
}
