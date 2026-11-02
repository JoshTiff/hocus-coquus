package com.hocuscoquus.hocuscoquus.repository;

import java.util.List;

import com.hocuscoquus.hocuscoquus.entity.DietaryRestriction;

public interface DietaryRestrictionRepository {
    DietaryRestriction findById(long dietaryRestrictionId);
    List<DietaryRestriction> findByAccount(long accountId);
}
