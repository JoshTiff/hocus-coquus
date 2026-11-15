package com.hocuscoquus.hocuscoquus;

import com.hocuscoquus.hocuscoquus.entity.Account;
import com.hocuscoquus.hocuscoquus.entity.Recipe;
import com.hocuscoquus.hocuscoquus.entity.RecipeTag;
import com.hocuscoquus.hocuscoquus.repository.AccountRepository;
import com.hocuscoquus.hocuscoquus.service.AccountService;
import com.hocuscoquus.hocuscoquus.service.RecipeService;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Before;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class AddRecipeTagTest {
    private long accountID = 68;
    private long recipeID = 29;
    private String tagName = "Pies";

    @Autowired
    RecipeService recipeService;

    @Test
    void createRecipeTag() {
        RecipeTag tag = recipeService.addRecipeTag(accountID, recipeID, tagName);
        // Basic integrity check.
        assertNotNull(tag);
        assertEquals(tagName, tag.getTag());
        assertEquals(accountID, tag.getAccount().getAccountId());
        // Checking association with account.
        RecipeTag retrievedTag = recipeService.getRecipeTag(accountID, tag.getRecipeTagId());
        assertEquals(tagName, retrievedTag.getTag());
        assertEquals(accountID, retrievedTag.getAccount().getAccountId());
        // Remove tag.
        recipeService.removeRecipeTag(accountID, tag.getRecipeTagId());
    }

}
