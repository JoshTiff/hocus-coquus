package com.hocuscoquus.hocuscoquus.handler;


import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.hocuscoquus.hocuscoquus.entity.Recipe;
import com.hocuscoquus.hocuscoquus.service.RecipeExportService;
import com.hocuscoquus.hocuscoquus.service.RecipeService;

@Service
public class RecipeExportHandler {

	private final RecipeExportService recipeExportService;
	private final RecipeService recipeService;
	
	public RecipeExportHandler(RecipeExportService recipeExportService, RecipeService recipeService) {
		this.recipeExportService = recipeExportService;
		this.recipeService = recipeService;
	}
	
	
	/**
	 * 
	 * @param recipeId
	 * @return
	 */
	public ResponseEntity<byte[]> processRecipeDownloadRequest(Long recipeId) {

		Recipe r = recipeService.getRecipeByID(recipeId);
		byte[] file = recipeExportService.downloadRecipe(r);

		String filename = String.join("_", r.getRecipeName().split(" ")) + ".pdf";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", r.getRecipeName() + filename);
        headers.setContentLength(file.length);

        return ResponseEntity.ok()
                .headers(headers)
                .body(file);
	}
}
