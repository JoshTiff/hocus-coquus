package com.hocuscoquus.hocuscoquus.controller;

import com.hocuscoquus.hocuscoquus.model.DietaryRestriction;
import com.hocuscoquus.hocuscoquus.service.DietaryRestrictionService;

public class DietaryRestrictionController {
    private final DietaryRestrictionService dietaryRestrictionService;

    public DietaryRestrictionController(DietaryRestrictionService dietaryRestrictionService) {
        this.dietaryRestrictionService = dietaryRestrictionService;
    }

    public DietaryRestriction create(DietaryRestriction restriction) {
        return dietaryRestrictionService.create(restriction);
    }

    public void delete(long restrictionId) {
        dietaryRestrictionService.delete(restrictionId);
    }
}
