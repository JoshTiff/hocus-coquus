package com.hocuscoquus.hocuscoquus.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.hocuscoquus.hocuscoquus.entity.AccountDetails;
import com.hocuscoquus.hocuscoquus.handler.RecipeHandler;
import com.hocuscoquus.hocuscoquus.model.RecipeCreateRequest;
import com.hocuscoquus.hocuscoquus.model.RecipeEditRequest;
import com.hocuscoquus.hocuscoquus.model.RecipeEditRequest.IngredientRow;
import com.hocuscoquus.hocuscoquus.service.RecipeService;

import jakarta.validation.Valid;

@RestController
public class RecipeController {
    private final RecipeHandler recipeHandler;
    private final RecipeService recipeService;

    public RecipeController(RecipeHandler recipeHandler, RecipeService recipeService) {
        this.recipeHandler = recipeHandler;
        this.recipeService = recipeService;
    }

    @PostMapping("/api/recipe/create")
    public ResponseEntity<?> saveRecipe(@Valid @RequestBody RecipeCreateRequest request) {
        return recipeHandler.processRecipeCreateRequest(request);
    }

    @GetMapping("/api/recipe/view-recipe/{recipeId}")
    public ResponseEntity<?> viewRecipe(@PathVariable long recipeId) {
        return recipeHandler.getRecipeByID(recipeId);
    }

    @GetMapping("/edit/recipe")
    public ModelAndView editRecipe(@RequestParam(required = false) Long id,
            @AuthenticationPrincipal AccountDetails account, Model model) {
        if (id == null) {
            model.addAttribute("recipes", recipeService.listOwnedRecipes(account.getAccountId()));
        } else {
            model.addAttribute("recipeId", id);
            model.addAttribute("recipe", recipeService.loadRecipeForEdit(id, account.getAccountId()));
        }
        return new ModelAndView("edit-recipe", model.asMap());
    }

    @PostMapping(value = "/edit/recipe", params = "ingredientAction")
    public ModelAndView changeIngredientRows(@RequestParam long id, @RequestParam int ingredientAction,
            @ModelAttribute RecipeEditRequest request, @AuthenticationPrincipal AccountDetails account) {
        recipeService.getOwnedRecipe(id, account.getAccountId());
        List<IngredientRow> rows = new ArrayList<>(request.ingredients() == null ? List.of() : request.ingredients());
        if (ingredientAction == -1) {
            rows.add(new IngredientRow("", null, ""));
        } else if (ingredientAction >= 0 && ingredientAction < rows.size()) {
            rows.remove(ingredientAction);
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid ingredient row");
        }
        return new ModelAndView("edit-recipe")
                .addObject("recipeId", id)
                .addObject("recipe", request.withIngredients(rows));
    }

    @PostMapping(value = "/edit/recipe", params = "!ingredientAction")
    public ModelAndView saveRecipeEdits(@RequestParam long id,
            @RequestParam(defaultValue = "false") boolean editIngredients,
            @Valid @ModelAttribute RecipeEditRequest request,
            @AuthenticationPrincipal AccountDetails account, RedirectAttributes redirect) {
        if (editIngredients && request.ingredients() == null) {
            request = request.withIngredients(List.of());
        }
        recipeService.updateRecipe(id, account.getAccountId(), request);
        redirect.addAttribute("id", id);
        redirect.addFlashAttribute("saved", true);
        return new ModelAndView("redirect:/edit/recipe");
    }

    @PutMapping(value = "/api/recipe/{recipeId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> updateRecipe(@PathVariable long recipeId,
            @Valid @RequestBody RecipeEditRequest request,
            @AuthenticationPrincipal AccountDetails account) {
        recipeService.updateRecipe(recipeId, account.getAccountId(), request);
        return ResponseEntity.noContent().build();
    }
}
