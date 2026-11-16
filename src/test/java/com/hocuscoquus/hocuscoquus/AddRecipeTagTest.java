package com.hocuscoquus.hocuscoquus;

import com.hocuscoquus.hocuscoquus.entity.Account;
import com.hocuscoquus.hocuscoquus.entity.Recipe;
import com.hocuscoquus.hocuscoquus.entity.RecipeTag;
import com.hocuscoquus.hocuscoquus.model.RecipeCreateRequest;
import com.hocuscoquus.hocuscoquus.model.RecipeIngredientCreateRequest;
import com.hocuscoquus.hocuscoquus.repository.AccountRepository;
import com.hocuscoquus.hocuscoquus.service.AccountService;
import com.hocuscoquus.hocuscoquus.service.RecipeService;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Before;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class AddRecipeTagTest {
    private long accountID;
    private final String email = "test" + UUID.randomUUID() + "@test.com";
    private final String password = "password";
    private long recipeID;
    private String tagName = "Pies";

    @Autowired
    RecipeService recipeService;

    @Autowired
    AccountService accountService;

    @BeforeEach
    void before() {
//        // Create account.
//        Account account = accountService.createAccount("Test", email, password);
//        assertNotNull(account);
//        accountID = account.getAccountId();
//        // Create recipe.
//        List<RecipeIngredientCreateRequest> ingredientList = new ArrayList<>(1);
//        ingredientList.add(new RecipeIngredientCreateRequest("Dihydrogen Monoxide", 2, "Litres"));
//        Recipe recipe = recipeService.createRecipe(account, new RecipeCreateRequest(
//                "Test Recipe",
//                "This is a test recipe.",
//                "There are no instructions for this recipe.",
//                "test.jpg",
//                ingredientList
//        ));
//        assertNotNull(recipe);
//        recipeID = recipe.getRecipeId();
    }

    @Test
    void createRecipeTag() {
//        RecipeTag tag = recipeService.addRecipeTag(accountID, recipeID, tagName);
//        // Basic integrity check.
//        assertNotNull(tag);
//        assertEquals(tagName, tag.getTag());
//        assertEquals(accountID, tag.getAccount().getAccountId());
//        // Checking association with account.
//        RecipeTag retrievedTag = recipeService.getRecipeTag(accountID, tag.getRecipeTagId());
//        assertEquals(tagName, retrievedTag.getTag());
//        assertEquals(accountID, retrievedTag.getAccount().getAccountId());
//        // Remove tag.
//        recipeService.removeRecipeTag(accountID, tag.getRecipeTagId());
    }

    @AfterEach
    void after() {
//        recipeService.deleteRecipe(recipeID);
//        accountService.deleteAccount(accountID, email, password);
    }

}
