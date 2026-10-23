package com.hocuscoquus.hocuscoquus.service;

import java.util.Objects;

import com.hocuscoquus.hocuscoquus.model.DietaryRestriction;
import com.hocuscoquus.hocuscoquus.repository.DietaryRestrictionRepository;

public class DietaryRestrictionService {
    private final DietaryRestrictionRepository repository;

    // TODO: review/add constructor(s)
    public DietaryRestrictionService(DietaryRestrictionRepository repository) {
        this.repository = Objects.requireNonNull(repository);
    }

    public DietaryRestriction create(DietaryRestriction restriction) {
        // TODO: add implementation for this method
        return new DietaryRestriction();
    }

    public void delete(long restrictionId) {
        // TODO: add implementation for this method
    }
}
