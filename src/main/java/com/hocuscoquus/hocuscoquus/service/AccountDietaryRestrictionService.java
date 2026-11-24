package com.hocuscoquus.hocuscoquus.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hocuscoquus.hocuscoquus.entity.Account;
import com.hocuscoquus.hocuscoquus.entity.AccountDetails;
import com.hocuscoquus.hocuscoquus.entity.DietaryRestriction;
import com.hocuscoquus.hocuscoquus.entity.Ingredient;
import com.hocuscoquus.hocuscoquus.repository.AccountRepository;
import com.hocuscoquus.hocuscoquus.repository.IngredientRepository;

/**
 * Creates and deletes dietary restrictions owned by the authenticated account.
 */
@Service
public class AccountDietaryRestrictionService {
    private final AccountRepository accountRepository;
    private final IngredientRepository ingredientRepository;

    /**
     * Creates a restriction and links its manually selected saved ingredients.
     *
     * @param accountRepository account and restriction persistence
     * @param ingredientRepository lookup and persistence for linked ingredients
     */
    public AccountDietaryRestrictionService(
            AccountRepository accountRepository,
            IngredientRepository ingredientRepository) {
        this.accountRepository = accountRepository;
        this.ingredientRepository = ingredientRepository;
    }

    /**
     * Saves a new restriction for the authenticated account.
     *
     * @param restriction restriction name and saved ingredient IDs to associate
     * @return the persisted restriction with its manually selected ingredients
     * @throws IllegalArgumentException if the restriction or ingredient IDs are invalid,
     *         or the account already has a restriction with that name
     * @throws AccessDeniedException if no authenticated account is available
     * @throws IllegalStateException if the authenticated account cannot be found or
     *         the new restriction was not persisted
     */
    @Transactional
    public DietaryRestriction createDietaryRestriction(DietaryRestriction restriction) {
        if (restriction == null
                || restriction.getDietaryRestrictionId() != 0
                || restriction.getDietaryRestrictionName() == null
                || restriction.getDietaryRestrictionName().isBlank()
                || restriction.getDietaryRestrictionName().length() > 255) {
            throw new IllegalArgumentException("Invalid dietary restriction");
        }

        long accountId = getAuthenticatedAccountId();
        Account account = accountRepository.findById(accountId);
        if (account == null) {
            throw new IllegalStateException("Authenticated account no longer exists");
        }

        String name = restriction.getDietaryRestrictionName().trim();
        if (account.getDietaryRestrictions().stream()
                .anyMatch(existing -> name.equalsIgnoreCase(
                        existing.getDietaryRestrictionName()))) {
            throw new IllegalArgumentException("Dietary restriction already exists: " + name);
        }

        List<Ingredient> ingredients = resolveIngredients(restriction.getIngredients());
        restriction.setDietaryRestrictionName(name);
        restriction.setAccount(account);
        restriction.setIngredients(ingredients);
        account.getDietaryRestrictions().add(restriction);
        // The account cascade persists the restriction before ingredient links are saved.
        Account savedAccount = accountRepository.saveAndFlush(account);

        DietaryRestriction savedRestriction = savedAccount.getDietaryRestrictions().stream()
                .filter(saved -> name.equalsIgnoreCase(
                        saved.getDietaryRestrictionName()))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException(
                        "Dietary restriction was not persisted"));
        savedRestriction.setIngredients(ingredients);

        // Ingredients own the join table, so add the restriction on that side.
        for (Ingredient ingredient : ingredients) {
            ingredient.getDietaryRestriction().add(savedRestriction);
        }
        ingredientRepository.saveAll(ingredients);
        return savedRestriction;
    }

    /**
     * Removes one of the authenticated account's restrictions and its ingredient links.
     *
     * @param dietaryRestrictionId ID of the restriction to remove
     * @return {@code true} if it belonged to the account and was removed; otherwise
     *         {@code false}
     * @throws AccessDeniedException if no authenticated account is available
     */
    @Transactional
    public boolean deleteDietaryRestriction(long dietaryRestrictionId) {
        if (dietaryRestrictionId <= 0) {
            return false;
        }

        long accountId = getAuthenticatedAccountId();
        Account account = accountRepository.findById(accountId);
        if (account == null) {
            return false;
        }

        DietaryRestriction toDelete = account.getDietaryRestrictions().stream()
                .filter(restriction -> restriction.getDietaryRestrictionId()
                        == dietaryRestrictionId)
                .findFirst()
                .orElse(null);
        if (toDelete == null) {
            return false;
        }

        // Clear the owning-side links before orphan removal deletes the restriction.
        List<Ingredient> ingredients = toDelete.getIngredients();
        for (Ingredient ingredient : ingredients) {
            ingredient.getDietaryRestriction().removeIf(
                    linkedRestriction -> linkedRestriction.getDietaryRestrictionId()
                            == dietaryRestrictionId);
        }
        account.getDietaryRestrictions().remove(toDelete);
        accountRepository.save(account);
        ingredientRepository.saveAll(ingredients);
        return true;
    }

    /**
     * Loads the saved ingredients named by a restriction request.
     *
     * @param requestedIngredients ingredient references containing database IDs
     * @return the persisted ingredients matching the supplied IDs
     * @throws IllegalArgumentException if an ID is missing, repeated, or invalid
     */
    private List<Ingredient> resolveIngredients(List<Ingredient> requestedIngredients) {
        if (requestedIngredients == null || requestedIngredients.isEmpty()) {
            return new ArrayList<>();
        }

        List<Long> ingredientIds = new ArrayList<>(requestedIngredients.size());
        Set<Long> uniqueIds = new HashSet<>();
        for (Ingredient ingredient : requestedIngredients) {
            if (ingredient == null
                    || ingredient.getIngredientId() <= 0
                    || !uniqueIds.add(ingredient.getIngredientId())) {
                throw new IllegalArgumentException("Dietary restriction ingredients must be unique saved ingredients");
            }
            ingredientIds.add(ingredient.getIngredientId());
        }

        List<Ingredient> ingredients = ingredientRepository.findAllById(ingredientIds);
        if (ingredients.size() != ingredientIds.size()) {
            throw new IllegalArgumentException("One or more dietary restriction ingredients do not exist");
        }
        return ingredients;
    }

    /**
     * Gets the current account ID from Spring Security's authenticated principal.
     *
     * @return ID of the authenticated account
     * @throws AccessDeniedException if the principal is not an account user
     */
    private long getAuthenticatedAccountId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()
                || !(authentication.getPrincipal() instanceof AccountDetails accountDetails)) {
            throw new AccessDeniedException("An authenticated account is required");
        }
        return accountDetails.getAccountId();
    }
}
