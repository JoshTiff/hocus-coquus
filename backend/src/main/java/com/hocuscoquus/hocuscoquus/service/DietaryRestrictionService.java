package com.hocuscoquus.hocuscoquus.service;

import java.util.Objects;

import com.hocuscoquus.hocuscoquus.model.DietaryRestriction;
import com.hocuscoquus.hocuscoquus.repository.DietaryRestrictionRepository;

public class DietaryRestrictionService {
    private final DietaryRestrictionRepository repository;

    public DietaryRestrictionService(DietaryRestrictionRepository repository) {
        this.repository = Objects.requireNonNull(repository);
    }

    public DietaryRestriction create(DietaryRestriction restriction) {
        // return repository.save(Objects.requireNonNull(restriction));
        return new DietaryRestriction();
    }

    public void delete(long restrictionId) {
        // repository.deleteById(restrictionId);
    }
}
