package com.hocuscoquus.hocuscoquus.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hocuscoquus.hocuscoquus.entity.Reaction;

public interface ReactionRepository extends JpaRepository<Reaction, Long> {
    Reaction findById(long reactionId);
    
    List<Reaction> findByRecipe(long recipeId);
    List<Reaction> findByAccount(long accountId);
}
