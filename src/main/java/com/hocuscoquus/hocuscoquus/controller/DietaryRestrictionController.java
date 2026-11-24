package com.hocuscoquus.hocuscoquus.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hocuscoquus.hocuscoquus.entity.AccountDetails;
import com.hocuscoquus.hocuscoquus.entity.DietaryRestriction;
import com.hocuscoquus.hocuscoquus.entity.Ingredient;
import com.hocuscoquus.hocuscoquus.model.DietaryRestrictionCreateRequest;
import com.hocuscoquus.hocuscoquus.model.DietaryRestrictionRecord;
import com.hocuscoquus.hocuscoquus.service.AccountDietaryRestrictionService;

/**
 * HTTP endpoints for managing the authenticated user's dietary restrictions.
 */
@RestController
@RequestMapping("/api/dietary-restrictions")
public class DietaryRestrictionController {
    private final AccountDietaryRestrictionService dietaryRestrictionService;

    /**
     * Creates the controller with the account-scoped restriction service.
     *
     * @param dietaryRestrictionService restriction lifecycle service
     */
    public DietaryRestrictionController(
            AccountDietaryRestrictionService dietaryRestrictionService) {
        this.dietaryRestrictionService = dietaryRestrictionService;
    }

    /**
     * Creates a restriction and associates the submitted saved ingredient IDs.
     *
     * @param accountDetails authenticated account, or {@code null} if unauthenticated
     * @param request restriction name and optional ingredient IDs
     * @return {@code 201} with the saved restriction, {@code 400} for invalid input,
     *         or {@code 401} when unauthenticated
     */
    @PostMapping
    public ResponseEntity<?> create(
            @AuthenticationPrincipal AccountDetails accountDetails,
            @jakarta.validation.Valid @RequestBody DietaryRestrictionCreateRequest request) {
        if (accountDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        DietaryRestriction restriction = new DietaryRestriction();
        restriction.setDietaryRestrictionName(request.dietaryRestrictionName());
        List<Ingredient> ingredients = new ArrayList<>();
        // The service reloads these ID-only references to validate and link saved ingredients.
        if (request.ingredientIds() != null) {
            for (Long ingredientId : request.ingredientIds()) {
                Ingredient ingredient = new Ingredient();
                ingredient.setIngredientId(ingredientId);
                ingredients.add(ingredient);
            }
        }
        restriction.setIngredients(ingredients);

        try {
            DietaryRestriction created =
                    dietaryRestrictionService.createDietaryRestriction(restriction);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(DietaryRestrictionRecord.from(created));
        } catch (IllegalArgumentException exception) {
            return ResponseEntity.badRequest().body(exception.getMessage());
        }
    }

    /**
     * Deletes a restriction belonging to the authenticated account.
     *
     * @param accountDetails authenticated account, or {@code null} if unauthenticated
     * @param dietaryRestrictionId ID of the restriction to delete
     * @return {@code 200} when deleted, {@code 404} if not found or not owned,
     *         or {@code 401} when unauthenticated
     */
    @DeleteMapping("/{dietaryRestrictionId}")
    public ResponseEntity<Boolean> delete(
            @AuthenticationPrincipal AccountDetails accountDetails,
            @PathVariable long dietaryRestrictionId) {
        if (accountDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        if (!dietaryRestrictionService.deleteDietaryRestriction(dietaryRestrictionId)) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(true);
    }
}
