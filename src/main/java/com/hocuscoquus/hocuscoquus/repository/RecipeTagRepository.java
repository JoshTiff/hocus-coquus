package com.hocuscoquus.hocuscoquus.repository;

import java.util.List;

import com.hocuscoquus.hocuscoquus.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;

import com.hocuscoquus.hocuscoquus.entity.RecipeTag;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface RecipeTagRepository extends JpaRepository<RecipeTag, Long> {
	
    RecipeTag findById(long recipeTagId);
    List<RecipeTag> findByAccount(Account account);
    RecipeTag findByAccountAndTag(Account account, String tag);
    RecipeTag findByRecipeTagId(long recipeTagId);
    void deleteByRecipeTagId(long recipeTagId);
}
