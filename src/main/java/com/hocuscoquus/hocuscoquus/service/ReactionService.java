package com.hocuscoquus.hocuscoquus.service;

import java.util.Map;
import java.util.Objects;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.hocuscoquus.hocuscoquus.entity.Account;
import com.hocuscoquus.hocuscoquus.entity.AccountDetails;
import com.hocuscoquus.hocuscoquus.entity.Reaction;
import com.hocuscoquus.hocuscoquus.entity.Recipe;
import com.hocuscoquus.hocuscoquus.model.ReactionType;
import com.hocuscoquus.hocuscoquus.repository.ReactionRepository;
import com.hocuscoquus.hocuscoquus.repository.RecipeRepository;

@Service
public class ReactionService {
    private final ReactionRepository reactionRepository;
    private final RecipeRepository recipeRepository;

    // TODO: review/add constructor(s)
    public ReactionService(ReactionRepository reactionRepository, RecipeRepository recipeRepository) {
        this.reactionRepository = Objects.requireNonNull(reactionRepository);
        this.recipeRepository = Objects.requireNonNull(recipeRepository);
    }

    @Transactional
    public Map<ReactionType, Integer> addReactionToRecipe(long recipeId, ReactionType type) {
        Recipe recipe = findRecipe(recipeId);
        Account account = currentAccount();
        Reaction reaction = reactionRepository.findByRecipe_RecipeIdAndAccount_AccountId(
                recipeId, account.getAccountId());

        if (reaction == null) {
            reaction = new Reaction();
            reaction.setRecipe(recipe);
            reaction.setAccount(account);
            recipe.getReactions().add(reaction);
        }
        reaction.setReactionType(type);
        reactionRepository.save(reaction);
        return recipe.computeNumReactions();
    }

    @Transactional
    public Map<ReactionType, Integer> removeReactionFromRecipe(long recipeId) {
        Recipe recipe = findRecipe(recipeId);
        long authenticatedAccountId = currentAccount().getAccountId();
        Reaction reaction = reactionRepository.findByRecipe_RecipeIdAndAccount_AccountId(
                recipeId, authenticatedAccountId);
        if (reaction != null) {
            recipe.getReactions().remove(reaction);
            reactionRepository.delete(reaction);
        }
        return recipe.computeNumReactions();
    }

    private Recipe findRecipe(long recipeId) {
        Recipe recipe = recipeRepository.findByRecipeId(recipeId);
        if (recipe == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Recipe not found: " + recipeId);
        }
        return recipe;
    }

    private Account currentAccount() {
        return ((AccountDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getAccount();
    }
}
