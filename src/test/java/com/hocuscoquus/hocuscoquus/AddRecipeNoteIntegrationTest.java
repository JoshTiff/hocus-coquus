package com.hocuscoquus.hocuscoquus;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import com.hocuscoquus.hocuscoquus.entity.Account;
import com.hocuscoquus.hocuscoquus.entity.Recipe;
import com.hocuscoquus.hocuscoquus.entity.RecipeNote;
import com.hocuscoquus.hocuscoquus.model.RecipeNoteCreateRequest;
import com.hocuscoquus.hocuscoquus.repository.RecipeNoteRepository;
import com.hocuscoquus.hocuscoquus.repository.RecipeRepository;
import com.hocuscoquus.hocuscoquus.service.AccountService;
import com.hocuscoquus.hocuscoquus.service.RecipeNoteService;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@SpringBootTest(properties = "spring.jpa.hibernate.ddl-auto=validate")
@Transactional
class AddRecipeNoteIntegrationTest {

    @Autowired
    private RecipeNoteService recipeNoteService;

    @Autowired
    private RecipeNoteRepository recipeNoteRepository;

    @Autowired
    private RecipeRepository recipeRepository;

    @Autowired
    private AccountService accountService;

    @PersistenceContext
    private EntityManager entityManager;

    private Account createAccount() {
        String email = "test-" + UUID.randomUUID() + "@example.com";

        return accountService.createAccount(
                "Test", email, "password");
    }

    private Recipe createRecipe(Account account) {
        Recipe recipe = Recipe.builder()
                .setRecipeName("Test Recipe")
                .setRecipeDescription("Test description")
                .setInstructions("Test instructions")
                .setAccount(account)
                .build();

        return recipeRepository.save(recipe);
    }

    private void flushAndClear() {
        entityManager.flush();
        entityManager.clear();
    }

    @Test
    void addRecipeNote_validRequest_persistsNote() {
        Account account = createAccount();
        Recipe recipe = createRecipe(account);

        long accountId = account.getAccountId();
        long recipeId = recipe.getRecipeId();

        RecipeNoteCreateRequest request =
                new RecipeNoteCreateRequest(
                        recipeId, "  Add more garlic  ");

        RecipeNote created =
                recipeNoteService.addRecipeNote(accountId, request);

        assertNotNull(created);
        assertTrue(created.getRecipeNoteId() > 0);

        long noteId = created.getRecipeNoteId();

        flushAndClear();

        RecipeNote persisted =
                entityManager.find(RecipeNote.class, noteId);

        assertNotNull(persisted);
        assertEquals("Add more garlic", persisted.getMessage());
        assertEquals(recipeId, persisted.getRecipe().getRecipeId());
        assertEquals(accountId, persisted.getAccount().getAccountId());
    }

    @Test
    void addRecipeNote_differentAccountsHaveSeparateNotes() {
        Account firstAccount = createAccount();
        Account secondAccount = createAccount();

        Recipe recipe = createRecipe(firstAccount);

        long firstAccountId = firstAccount.getAccountId();
        long secondAccountId = secondAccount.getAccountId();
        long recipeId = recipe.getRecipeId();

        RecipeNote firstNote = recipeNoteService.addRecipeNote(
                firstAccountId,
                new RecipeNoteCreateRequest(
                        recipeId, "First user's note"));

        RecipeNote secondNote = recipeNoteService.addRecipeNote(
                secondAccountId,
                new RecipeNoteCreateRequest(
                        recipeId, "Second user's note"));

        long firstNoteId = firstNote.getRecipeNoteId();
        long secondNoteId = secondNote.getRecipeNoteId();

        flushAndClear();

        List<RecipeNote> firstUserNotes =
                recipeNoteRepository
                        .findByAccount_AccountIdAndRecipe_RecipeId(
                                firstAccountId, recipeId);

        List<RecipeNote> secondUserNotes =
                recipeNoteRepository
                        .findByAccount_AccountIdAndRecipe_RecipeId(
                                secondAccountId, recipeId);

        assertEquals(1, firstUserNotes.size());
        assertEquals(1, secondUserNotes.size());

        assertEquals(firstNoteId,
                firstUserNotes.get(0).getRecipeNoteId());

        assertEquals(secondNoteId,
                secondUserNotes.get(0).getRecipeNoteId());

        assertEquals("First user's note",
                firstUserNotes.get(0).getMessage());

        assertEquals("Second user's note",
                secondUserNotes.get(0).getMessage());
    }

    @Test
    void addRecipeNote_nonexistentRecipe_doesNotSaveNote() {
        Account account = createAccount();

        RecipeNoteCreateRequest request =
                new RecipeNoteCreateRequest(
                        Long.MAX_VALUE, "Test note");

        assertThrows(NoSuchElementException.class,
                () -> recipeNoteService.addRecipeNote(
                        account.getAccountId(), request));

        flushAndClear();

        assertTrue(recipeNoteRepository
                .findByAccount_AccountId(account.getAccountId())
                .isEmpty());
    }

    @Test
    void addRecipeNote_nonexistentAccount_doesNotSaveNote() {
        Account account = createAccount();
        Recipe recipe = createRecipe(account);

        long recipeId = recipe.getRecipeId();

        RecipeNoteCreateRequest request =
                new RecipeNoteCreateRequest(recipeId, "Test note");

        assertThrows(NoSuchElementException.class,
                () -> recipeNoteService.addRecipeNote(
                        Long.MAX_VALUE, request));

        flushAndClear();

        assertTrue(recipeNoteRepository
                .findByRecipe_RecipeId(recipeId)
                .isEmpty());
    }

    @Test
    void addRecipeNote_blankMessage_doesNotSaveNote() {
        Account account = createAccount();
        Recipe recipe = createRecipe(account);

        long recipeId = recipe.getRecipeId();

        RecipeNoteCreateRequest request =
                new RecipeNoteCreateRequest(recipeId, "   ");

        assertThrows(IllegalArgumentException.class,
                () -> recipeNoteService.addRecipeNote(
                        account.getAccountId(), request));

        flushAndClear();

        assertTrue(recipeNoteRepository
                .findByRecipe_RecipeId(recipeId)
                .isEmpty());
    }
}
