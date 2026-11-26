package com.hocuscoquus.hocuscoquus.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.hocuscoquus.hocuscoquus.entity.Account;
import com.hocuscoquus.hocuscoquus.entity.DietaryRestriction;

public interface AccountRepository extends JpaRepository<Account, Long> {
    
	Account findById(long accountId);
    
    Account findByEmail(String email);

    boolean existsByEmail(String email);

    /**
     * Finds the dietary restrictions belonging to an account.
     *
     * @param accountId ID of the account that owns the restrictions
     * @return the account's restrictions, or an empty list if it has none
     */
    @Query("""
            SELECT restriction
            FROM Account account
            JOIN account.dietaryRestrictions restriction
            WHERE account.accountId = :accountId
            """)
    List<DietaryRestriction> findDietaryRestrictionsByAccountId(
            @Param("accountId") long accountId);
}