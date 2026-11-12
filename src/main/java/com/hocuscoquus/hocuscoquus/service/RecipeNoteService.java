package com.hocuscoquus.hocuscoquus.service;

import java.util.NoSuchElementException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hocuscoquus.hocuscoquus.entity.Account;
import com.hocuscoquus.hocuscoquus.entity.Recipe;
import com.hocuscoquus.hocuscoquus.entity.RecipeNote;
import com.hocuscoquus.hocuscoquus.model.RecipeNoteCreateRequest;
import com.hocuscoquus.hocuscoquus.repository.AccountRepository;
import com.hocuscoquus.hocuscoquus.repository.RecipeNoteRepository;
import com.hocuscoquus.hocuscoquus.repository.RecipeRepository;

@Service
public class RecipeNoteService {

    private final RecipeNoteRepository recipeNoteRepository;
    private final RecipeRepository recipeRepository;
    private final AccountRepository accountRepository;

    public RecipeNoteService(
            RecipeNoteRepository recipeNoteRepository,
            RecipeRepository recipeRepository,
            AccountRepository accountRepository) {

        this.recipeNoteRepository = recipeNoteRepository;
        this.recipeRepository = recipeRepository;
        this.accountRepository = accountRepository;
    }

    @Transactional
    public RecipeNote addRecipeNote(
            long accountId, RecipeNoteCreateRequest request) {

        if (request == null
                || request.recipeId() == null
                || request.recipeId() <= 0
                || request.message() == null
                || request.message().isBlank()
                || request.message().length() > 255) {
            throw new IllegalArgumentException("Invalid recipe note");
        }

        Recipe recipe = recipeRepository.findById(request.recipeId())
        .orElseThrow(() -> new NoSuchElementException("Recipe not found"));

        Account account = accountRepository.findById(accountId);

        if (account == null) {
            throw new NoSuchElementException("Account not found");
        }

        RecipeNote note = new RecipeNote();
        note.setRecipe(recipe);
        note.setAccount(account);
        note.setMessage(request.message().trim());

        return recipeNoteRepository.save(note);
    }

    public RecipeNote updateRecipeNote(RecipeNote note) {
        // TODO: add implementation for this method
        return null;
    }

    public void deleteRecipeNote(long recipeNoteId) {
        // TODO: add implementation for this method
    }
}
