package com.hocuscoquus.hocuscoquus.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.hocuscoquus.hocuscoquus.entity.DietaryRestriction;

public interface DietaryRestrictionRepository extends JpaRepository<DietaryRestriction, Long> {
    DietaryRestriction findById(long dietaryRestrictionId);
    
    
    List<DietaryRestriction> findByAccount_AccountId(long accountId);
}
