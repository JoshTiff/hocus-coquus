package com.hocuscoquus.hocuscoquus.model;

import java.util.List;

import com.hocuscoquus.hocuscoquus.entity.DietaryRestriction;
import com.hocuscoquus.hocuscoquus.entity.Ingredient;

/**
 * Response body containing a restriction and its manually associated ingredients.
 *
 * @param dietaryRestrictionId saved restriction ID
 * @param dietaryRestrictionName saved restriction name
 * @param ingredientIds IDs of ingredients excluded by the restriction
 */
public record DietaryRestrictionRecord(
        long dietaryRestrictionId,
        String dietaryRestrictionName,
        List<Long> ingredientIds) {

    /**
     * Converts a persisted restriction entity to its API response shape.
     *
     * @param restriction restriction to convert
     * @return response record with the restriction's ingredient IDs
     */
    public static DietaryRestrictionRecord from(DietaryRestriction restriction) {
        List<Long> ingredientIds = restriction.getIngredients().stream()
                .map(Ingredient::getIngredientId)
                .toList();
        return new DietaryRestrictionRecord(
                restriction.getDietaryRestrictionId(),
                restriction.getDietaryRestrictionName(),
                ingredientIds);
    }
}
