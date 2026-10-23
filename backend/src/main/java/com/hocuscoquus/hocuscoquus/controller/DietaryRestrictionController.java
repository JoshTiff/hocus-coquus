package com.hocuscoquus.hocuscoquus.controller;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hocuscoquus.hocuscoquus.model.DietaryRestriction;
import com.hocuscoquus.hocuscoquus.service.DietaryRestrictionService;

@RestController
@RequestMapping("/api/dietary-restrictions")
public class DietaryRestrictionController {
    private final DietaryRestrictionService dietaryRestrictionService;

    public DietaryRestrictionController(DietaryRestrictionService dietaryRestrictionService) {
        this.dietaryRestrictionService = dietaryRestrictionService;
    }

    @PostMapping
    public DietaryRestriction create(@RequestBody DietaryRestriction restriction) {
        return dietaryRestrictionService.create(restriction);
    }

    @DeleteMapping("/{dietaryRestrictionId}")
    public void delete(@PathVariable long dietaryRestrictionId) {
        dietaryRestrictionService.delete(dietaryRestrictionId);
    }
}
