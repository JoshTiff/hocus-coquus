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
import com.hocuscoquus.hocuscoquus.repository.RecipeRepository;
import com.hocuscoquus.hocuscoquus.service.AccountService;
import com.hocuscoquus.hocuscoquus.service.RecipeService;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@SpringBootTest(properties = "spring.jpa.hibernate.ddl-auto=validate")
@Transactional
class AddRecipeNoteIntegrationTest {

    @Autowired
    private RecipeService recipeService;

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

    private List<RecipeNote> findNotesForAccountAndRecipe(
            long accountId, long recipeId) {

        return entityManager.createQuery(
                "SELECT n FROM RecipeNote n " +
                "WHERE n.account.accountId = :accountId " +
                "AND n.recipe.recipeId = :recipeId",
                RecipeNote.class)
                .setParameter("accountId", accountId)
                .setParameter("recipeId", recipeId)
                .getResultList();
    }

    private long countNotesForAccount(long accountId) {
        return entityManager.createQuery(
                "SELECT COUNT(n) FROM RecipeNote n " +
                "WHERE n.account.accountId = :accountId",
                Long.class)
                .setParameter("accountId", accountId)
                .getSingleResult();
    }

    private long countNotesForRecipe(long recipeId) {
        return entityManager.createQuery(
                "SELECT COUNT(n) FROM RecipeNote n " +
                "WHERE n.recipe.recipeId = :recipeId",
                Long.class)
                .setParameter("recipeId", recipeId)
                .getSingleResult();
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
                recipeService.addRecipeNote(accountId, request);

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

        RecipeNote firstNote = recipeService.addRecipeNote(
                firstAccountId,
                new RecipeNoteCreateRequest(
                        recipeId, "First user's note"));

        RecipeNote secondNote = recipeService.addRecipeNote(
                secondAccountId,
                new RecipeNoteCreateRequest(
                        recipeId, "Second user's note"));

        long firstNoteId = firstNote.getRecipeNoteId();
        long secondNoteId = secondNote.getRecipeNoteId();

        flushAndClear();

        List<RecipeNote> firstUserNotes =
                findNotesForAccountAndRecipe(
                        firstAccountId, recipeId);

        List<RecipeNote> secondUserNotes =
                findNotesForAccountAndRecipe(
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
                () -> recipeService.addRecipeNote(
                        account.getAccountId(), request));

        flushAndClear();

        assertEquals(0L,
                countNotesForAccount(account.getAccountId()));
    }

    @Test
    void addRecipeNote_nonexistentAccount_doesNotSaveNote() {
        Account account = createAccount();
        Recipe recipe = createRecipe(account);

        long recipeId = recipe.getRecipeId();

        RecipeNoteCreateRequest request =
                new RecipeNoteCreateRequest(recipeId, "Test note");

        assertThrows(NoSuchElementException.class,
                () -> recipeService.addRecipeNote(
                        Long.MAX_VALUE, request));

        flushAndClear();

        assertEquals(0L, countNotesForRecipe(recipeId));
    }

    @Test
    void addRecipeNote_blankMessage_doesNotSaveNote() {
        Account account = createAccount();
        Recipe recipe = createRecipe(account);

        long recipeId = recipe.getRecipeId();

        RecipeNoteCreateRequest request =
                new RecipeNoteCreateRequest(recipeId, "   ");

        assertThrows(IllegalArgumentException.class,
                () -> recipeService.addRecipeNote(
                        account.getAccountId(), request));

        flushAndClear();

        assertEquals(0L, countNotesForRecipe(recipeId));
    }
}
