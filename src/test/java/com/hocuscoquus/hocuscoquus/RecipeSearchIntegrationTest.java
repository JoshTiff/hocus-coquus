package com.hocuscoquus.hocuscoquus;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.hocuscoquus.hocuscoquus.entity.DietaryRestriction;
import com.hocuscoquus.hocuscoquus.entity.Ingredient;
import com.hocuscoquus.hocuscoquus.entity.Recipe;
import com.hocuscoquus.hocuscoquus.entity.RecipeIngredient;
import com.hocuscoquus.hocuscoquus.entity.Account;
import com.hocuscoquus.hocuscoquus.repository.AccountRepository;
import com.hocuscoquus.hocuscoquus.repository.IngredientRepository;
import com.hocuscoquus.hocuscoquus.repository.RecipeRepository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@SpringBootTest(properties = "spring.jpa.hibernate.ddl-auto=validate")
@Transactional
class RecipeSearchIntegrationTest {

    @Autowired
    private IngredientRepository ingredientRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private RecipeRepository recipeRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Test
    void searchByDietaryRestrictions_excludesRecipesWithAnyRestrictedIngredient() {
        // The user explicitly links each excluded ingredient to its restriction.
        DietaryRestriction peanutFree = createRestriction("peanut-free");
        DietaryRestriction dairyFree = createRestriction("dairy-free");

        Ingredient peanut = createIngredient("peanut");
        Ingredient peanutOil = createIngredient("peanut oil");
        Ingredient milk = createIngredient("milk");
        Ingredient rice = createIngredient("rice");
        associate(peanut, peanutFree);
        associate(peanutOil, peanutFree);
        associate(milk, dairyFree);

        Account recipeOwner = createAccount();
        Recipe riceBowl = createRecipe("rice bowl", recipeOwner, rice);
        Recipe peanutNoodles = createRecipe("peanut noodles", recipeOwner, peanut);
        Recipe peanutOilStirFry =
                createRecipe("peanut oil stir fry", recipeOwner, peanutOil, rice);
        Recipe milkRicePudding =
                createRecipe("milk rice pudding", recipeOwner, milk, rice);
        Recipe ownerlessRecipe = createRecipe("ownerless recipe", null, rice);

        entityManager.flush();

        List<Recipe> peanutFreeResults =
                recipeRepository.findRecipesWithoutDietaryRestrictions(
                        List.of(peanutFree));
        List<Recipe> allRestrictionResults =
                recipeRepository.findRecipesWithoutDietaryRestrictions(
                        List.of(peanutFree, dairyFree));

        // One restriction excludes peanut ingredients; combining restrictions excludes both sets.
        Set<Long> peanutFreeRecipeIds = recipeIds(peanutFreeResults);
        assertTrue(peanutFreeRecipeIds.contains(riceBowl.getRecipeId()));
        assertTrue(peanutFreeRecipeIds.contains(milkRicePudding.getRecipeId()));
        assertFalse(peanutFreeRecipeIds.contains(peanutNoodles.getRecipeId()));
        assertFalse(peanutFreeRecipeIds.contains(peanutOilStirFry.getRecipeId()));
        assertFalse(peanutFreeRecipeIds.contains(ownerlessRecipe.getRecipeId()));

        Set<Long> allRestrictionRecipeIds = recipeIds(allRestrictionResults);
        assertTrue(allRestrictionRecipeIds.contains(riceBowl.getRecipeId()));
        assertFalse(allRestrictionRecipeIds.contains(peanutNoodles.getRecipeId()));
        assertFalse(allRestrictionRecipeIds.contains(peanutOilStirFry.getRecipeId()));
        assertFalse(allRestrictionRecipeIds.contains(milkRicePudding.getRecipeId()));
        assertFalse(allRestrictionRecipeIds.contains(ownerlessRecipe.getRecipeId()));
    }

    private Account createAccount() {
        Account account = new Account();
        account.setFirstName("Recipe owner");
        account.setEmail("recipe-owner-" + UUID.randomUUID() + "@example.com");
        account.setPassword("not-a-real-password");
        return accountRepository.save(account);
    }

    private DietaryRestriction createRestriction(String name) {
        // Restrictions are persisted as children of accounts through AccountRepository.
        Account account = new Account();
        account.setFirstName("Restriction test");
        account.setEmail("restriction-test-" + UUID.randomUUID() + "@example.com");
        account.setPassword("not-a-real-password");
        accountRepository.save(account);

        DietaryRestriction restriction = new DietaryRestriction();
        restriction.setDietaryRestrictionName(name + "-" + UUID.randomUUID());
        restriction.setAccount(account);
        account.getDietaryRestrictions().add(restriction);
        Account savedAccount = accountRepository.saveAndFlush(account);
        String restrictionName = restriction.getDietaryRestrictionName();
        return savedAccount.getDietaryRestrictions().stream()
                .filter(saved -> saved.getDietaryRestrictionName()
                        .equals(restrictionName))
                .findFirst()
                .orElseThrow();
    }

    private Ingredient createIngredient(String name) {
        Ingredient ingredient = new Ingredient();
        ingredient.setIngredientName(name + "-" + UUID.randomUUID());
        return ingredientRepository.save(ingredient);
    }

    private void associate(Ingredient ingredient, DietaryRestriction restriction) {
        ingredient.getDietaryRestriction().add(restriction);
        ingredientRepository.save(ingredient);
    }

    private Recipe createRecipe(String name, Account account, Ingredient... ingredients) {
        // Each supplied ingredient becomes a separate recipe-ingredient row.
        Recipe recipe = Recipe.builder()
                .setRecipeName(name)
                .setAccount(account)
                .build();
        List<RecipeIngredient> recipeIngredients = new ArrayList<>();
        for (Ingredient ingredient : ingredients) {
            RecipeIngredient recipeIngredient = new RecipeIngredient();
            recipeIngredient.setRecipe(recipe);
            recipeIngredient.setIngredient(ingredient);
            recipeIngredients.add(recipeIngredient);
        }
        recipe.setRecipeIngredients(recipeIngredients);
        return recipeRepository.save(recipe);
    }

    private Set<Long> recipeIds(List<Recipe> recipes) {
        return recipes.stream()
                .map(Recipe::getRecipeId)
                .collect(Collectors.toSet());
    }
}
