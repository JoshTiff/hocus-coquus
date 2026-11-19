package com.hocuscoquus.hocuscoquus;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import com.hocuscoquus.hocuscoquus.entity.Recipe;
import com.hocuscoquus.hocuscoquus.repository.RecipeRepository;
import com.hocuscoquus.hocuscoquus.service.RecipeExportService;
import com.hocuscoquus.hocuscoquus.service.export.RecipeExportEmail;
import com.hocuscoquus.hocuscoquus.service.export.RecipeExportFactory;
import com.hocuscoquus.hocuscoquus.service.export.RecipeExportType;

/** Tests recipe lookup and email-export delegation in the export service. */
@ExtendWith(MockitoExtension.class)
class RecipeExportServiceTest {

    @Mock
    private RecipeRepository recipeRepository;

    @Mock
    private RecipeExportFactory recipeExportFactory;

    @Mock
    private RecipeExportEmail recipeExport;

    /** Ensures an existing recipe is passed to the email exporter and its URI is returned. */
    @Test
    void shareRecipeByEmail_usesEmailExportForExistingRecipe() {
        Recipe recipe = Recipe.builder().setRecipeName("Pancakes").build();
        when(recipeRepository.findByRecipeId(7L)).thenReturn(recipe);
        when(recipeExportFactory.getRecipeExportAbstraction(RecipeExportType.EMAIL))
                .thenReturn(recipeExport);
        when(recipeExport.export(recipe))
                .thenReturn(true);
        when(recipeExport.getMailtoUri())
                .thenReturn("mailto:friend@example.com?subject=Recipe");

        RecipeExportService service = new RecipeExportService(recipeRepository, recipeExportFactory);

        assertEquals(
                "mailto:friend@example.com?subject=Recipe",
                service.shareRecipeByEmail(7L, "friend@example.com"));
        verify(recipeExportFactory).getRecipeExportAbstraction(RecipeExportType.EMAIL);
        verify(recipeExport).setRecipientEmail("friend@example.com");
    }

    /** Ensures a missing recipe produces a not-found response. */
    @Test
    void shareRecipeByEmail_missingRecipeReturnsNotFound() {
        when(recipeRepository.findByRecipeId(999L)).thenReturn(null);
        RecipeExportService service = new RecipeExportService(recipeRepository, recipeExportFactory);

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> service.shareRecipeByEmail(999L, "friend@example.com"));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
    }
}
