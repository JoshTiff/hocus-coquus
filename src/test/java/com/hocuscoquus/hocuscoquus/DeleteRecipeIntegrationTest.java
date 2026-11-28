package com.hocuscoquus.hocuscoquus;

import com.hocuscoquus.hocuscoquus.entity.Account;
import com.hocuscoquus.hocuscoquus.entity.Calendar;
import com.hocuscoquus.hocuscoquus.entity.CalendarEntry;
import com.hocuscoquus.hocuscoquus.entity.Ingredient;
import com.hocuscoquus.hocuscoquus.entity.Reaction;
import com.hocuscoquus.hocuscoquus.entity.Recipe;
import com.hocuscoquus.hocuscoquus.entity.RecipeIngredient;
import com.hocuscoquus.hocuscoquus.entity.RecipeNote;
import com.hocuscoquus.hocuscoquus.entity.RecipeTag;
import com.hocuscoquus.hocuscoquus.model.ReactionType;
import com.hocuscoquus.hocuscoquus.repository.RecipeRepository;
import com.hocuscoquus.hocuscoquus.service.RecipeService;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(properties = "spring.jpa.hibernate.ddl-auto=validate")
@Transactional
class DeleteRecipeIntegrationTest {

    @Autowired
    private RecipeService recipeService;

    @Autowired
    private RecipeRepository recipeRepository;

    @PersistenceContext
    private EntityManager entityManager;

    // Helpers for creating test data

    private Account createAccount() {
        Account account = new Account();
        account.setFirstName("Test");
        account.setEmail("test-" + UUID.randomUUID() + "@example.com");
        account.setPassword("not-a-real-password");

        entityManager.persist(account);

        return account;
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

    private Ingredient createIngredient() {
        Ingredient ingredient = new Ingredient();
        ingredient.setIngredientName("test-" + UUID.randomUUID());

        entityManager.persist(ingredient);

        return ingredient;
    }

    private RecipeIngredient addIngredient(
            Recipe recipe, Ingredient ingredient) {

        RecipeIngredient row = new RecipeIngredient();
        row.setRecipe(recipe);
        row.setIngredient(ingredient);
        row.setQuantity(2.0);
        row.setMeasurement("cups");

        recipe.getRecipeIngredients().add(row);
        entityManager.persist(row);

        return row;
    }

    private RecipeNote createNote(Recipe recipe, Account account) {
        RecipeNote note = new RecipeNote();
        note.setRecipe(recipe);
        note.setAccount(account);
        note.setMessage("Test recipe note");

        entityManager.persist(note);

        return note;
    }

    private Reaction createReaction(
            Recipe recipe, Account account, ReactionType type) {

        Reaction reaction = new Reaction();
        reaction.setRecipe(recipe);
        reaction.setAccount(account);
        reaction.setReactionType(type);

        entityManager.persist(reaction);

        return reaction;
    }

    private RecipeTag createTag(Account account, Recipe... recipes) {
        RecipeTag tag = new RecipeTag();
        tag.setAccount(account);
        tag.setTag("test-" + UUID.randomUUID());

        tag.getRecipes().addAll(List.of(recipes));

        entityManager.persist(tag);

        return tag;
    }

    private Calendar createCalendar(Account account) {
        Calendar calendar = new Calendar();
        calendar.setAccount(account);
        calendar.setCalendarName("Test Calendar");
        calendar.setDescription("Test description");

        entityManager.persist(calendar);

        return calendar;
    }

    private CalendarEntry createCalendarEntry(
            Calendar calendar, Recipe... recipes) {

        CalendarEntry entry = new CalendarEntry();
        entry.setCalendar(calendar);
        entry.setDateTime(new Date());

        entry.getRecipes().addAll(List.of(recipes));

        entityManager.persist(entry);

        return entry;
    }

    private void flushAndClear() {
        entityManager.flush();
        entityManager.clear();
    }

    // Only called with fixed table names defined in these tests.
    private long countRecipeRows(String tableName, long recipeId) {
        Number result = (Number) entityManager.createNativeQuery(
                "SELECT COUNT(*) FROM " + tableName
                        + " WHERE recipe_id = :recipeId")
                .setParameter("recipeId", recipeId)
                .getSingleResult();

        return result.longValue();
    }

    private void assertRecipeDeleted(long recipeId) {
        assertAll(
                () -> assertEquals(0L,
                        countRecipeRows("recipe", recipeId)),
                () -> assertEquals(0L,
                        countRecipeRows("recipe_ingredient", recipeId)),
                () -> assertEquals(0L,
                        countRecipeRows("recipe_note", recipeId)),
                () -> assertEquals(0L,
                        countRecipeRows("reaction", recipeId)),
                () -> assertEquals(0L,
                        countRecipeRows("recipe_tag_recipe", recipeId)),
                () -> assertEquals(0L,
                        countRecipeRows("calendar_entry_recipe", recipeId))
        );
    }

    @Test
    void deleteRecipe_ownedRecipeIsDeleted() {
        Account account = createAccount();
        Recipe recipe = createRecipe(account);

        long accountId = account.getAccountId();
        long recipeId = recipe.getRecipeId();

        flushAndClear();

        recipeService.deleteRecipe(recipeId, accountId);

        flushAndClear();

        assertRecipeDeleted(recipeId);

        assertNotNull(entityManager.find(Account.class, accountId));
    }

    @Test
    void deleteRecipe_deletesIngredientsButPreservesIngredientEntities() {
        Account account = createAccount();

        Recipe recipe = createRecipe(account);
        Recipe otherRecipe = createRecipe(account);

        Ingredient sharedIngredient = createIngredient();
        Ingredient exclusiveIngredient = createIngredient();

        addIngredient(recipe, sharedIngredient);
        addIngredient(recipe, exclusiveIngredient);
        addIngredient(otherRecipe, sharedIngredient);

        long accountId = account.getAccountId();
        long recipeId = recipe.getRecipeId();
        long otherRecipeId = otherRecipe.getRecipeId();
        long sharedIngredientId = sharedIngredient.getIngredientId();
        long exclusiveIngredientId = exclusiveIngredient.getIngredientId();

        flushAndClear();

        assertEquals(2L,
                countRecipeRows("recipe_ingredient", recipeId));

        recipeService.deleteRecipe(recipeId, accountId);

        flushAndClear();

        assertRecipeDeleted(recipeId);

        // Both underlying ingredients should still exist.
        assertNotNull(entityManager.find(
                Ingredient.class, sharedIngredientId));

        assertNotNull(entityManager.find(
                Ingredient.class, exclusiveIngredientId));

        // The other recipe must retain its ingredient.
        Recipe persistedOtherRecipe =
                entityManager.find(Recipe.class, otherRecipeId);

        assertNotNull(persistedOtherRecipe);
        assertEquals(1, persistedOtherRecipe.getRecipeIngredients().size());

        assertEquals(sharedIngredientId,
                persistedOtherRecipe.getRecipeIngredients().get(0)
                        .getIngredient().getIngredientId());
    }

    @Test
    void deleteRecipe_deletesNotesFromAllAccounts() {
        Account owner = createAccount();
        Account otherAccount = createAccount();

        Recipe recipe = createRecipe(owner);
        Recipe otherRecipe = createRecipe(otherAccount);

        RecipeNote ownerNote = createNote(recipe, owner);
        RecipeNote otherAccountNote = createNote(recipe, otherAccount);
        RecipeNote unrelatedNote = createNote(otherRecipe, otherAccount);

        long accountId = owner.getAccountId();
        long recipeId = recipe.getRecipeId();
        long ownerNoteId = ownerNote.getRecipeNoteId();
        long otherAccountNoteId = otherAccountNote.getRecipeNoteId();
        long unrelatedNoteId = unrelatedNote.getRecipeNoteId();

        flushAndClear();

        assertEquals(2L, countRecipeRows("recipe_note", recipeId));

        recipeService.deleteRecipe(recipeId, accountId);

        flushAndClear();

        assertRecipeDeleted(recipeId);

        assertNull(entityManager.find(RecipeNote.class, ownerNoteId));

        assertNull(entityManager.find(
                RecipeNote.class, otherAccountNoteId));

        assertNotNull(entityManager.find(
                RecipeNote.class, unrelatedNoteId));
    }

    @Test
    void deleteRecipe_deletesReactionsFromAllAccounts() {
        Account owner = createAccount();
        Account otherAccount = createAccount();

        Recipe recipe = createRecipe(owner);
        Recipe otherRecipe = createRecipe(otherAccount);

        Reaction positiveReaction =
                createReaction(recipe, owner, ReactionType.POSITIVE);

        Reaction negativeReaction =
                createReaction(recipe, otherAccount, ReactionType.NEGATIVE);

        Reaction unrelatedReaction =
                createReaction(otherRecipe, owner, ReactionType.POSITIVE);

        long accountId = owner.getAccountId();
        long recipeId = recipe.getRecipeId();
        long positiveReactionId = positiveReaction.getReactionId();
        long negativeReactionId = negativeReaction.getReactionId();
        long unrelatedReactionId = unrelatedReaction.getReactionId();

        flushAndClear();

        assertEquals(2L, countRecipeRows("reaction", recipeId));

        recipeService.deleteRecipe(recipeId, accountId);

        flushAndClear();

        assertRecipeDeleted(recipeId);

        assertNull(entityManager.find(
                Reaction.class, positiveReactionId));

        assertNull(entityManager.find(
                Reaction.class, negativeReactionId));

        assertNotNull(entityManager.find(
                Reaction.class, unrelatedReactionId));
    }

    @Test
    void deleteRecipe_removesTagAssociationsButPreservesTags() {
        Account owner = createAccount();
        Account otherAccount = createAccount();

        Recipe recipe = createRecipe(owner);
        Recipe otherRecipe = createRecipe(otherAccount);

        RecipeTag exclusiveTag = createTag(owner, recipe);

        RecipeTag sharedTag =
                createTag(otherAccount, recipe, otherRecipe);

        long accountId = owner.getAccountId();
        long recipeId = recipe.getRecipeId();
        long otherRecipeId = otherRecipe.getRecipeId();
        long exclusiveTagId = exclusiveTag.getRecipeTagId();
        long sharedTagId = sharedTag.getRecipeTagId();

        flushAndClear();

        assertEquals(2L,
                countRecipeRows("recipe_tag_recipe", recipeId));

        recipeService.deleteRecipe(recipeId, accountId);

        flushAndClear();

        assertRecipeDeleted(recipeId);

        RecipeTag persistedExclusiveTag =
                entityManager.find(RecipeTag.class, exclusiveTagId);

        RecipeTag persistedSharedTag =
                entityManager.find(RecipeTag.class, sharedTagId);

        assertNotNull(persistedExclusiveTag);
        assertNotNull(persistedSharedTag);

        assertTrue(persistedExclusiveTag.getRecipes().isEmpty());

        assertEquals(1, persistedSharedTag.getRecipes().size());

        assertEquals(otherRecipeId,
                persistedSharedTag.getRecipes().get(0).getRecipeId());
    }

    @Test
    void deleteRecipe_removesCalendarAssociationsButPreservesEntries() {
        Account owner = createAccount();
        Account otherAccount = createAccount();

        Recipe recipe = createRecipe(owner);
        Recipe otherRecipe = createRecipe(otherAccount);

        Calendar calendar = createCalendar(otherAccount);

        CalendarEntry exclusiveEntry =
                createCalendarEntry(calendar, recipe);

        CalendarEntry sharedEntry =
                createCalendarEntry(calendar, recipe, otherRecipe);

        long accountId = owner.getAccountId();
        long recipeId = recipe.getRecipeId();
        long otherRecipeId = otherRecipe.getRecipeId();
        long calendarId = calendar.getCalendarId();
        long exclusiveEntryId = exclusiveEntry.getCalendarEntryId();
        long sharedEntryId = sharedEntry.getCalendarEntryId();

        flushAndClear();

        assertEquals(2L,
                countRecipeRows("calendar_entry_recipe", recipeId));

        recipeService.deleteRecipe(recipeId, accountId);

        flushAndClear();

        assertRecipeDeleted(recipeId);

        assertNotNull(entityManager.find(Calendar.class, calendarId));

        CalendarEntry persistedExclusiveEntry =
                entityManager.find(CalendarEntry.class, exclusiveEntryId);

        CalendarEntry persistedSharedEntry =
                entityManager.find(CalendarEntry.class, sharedEntryId);

        assertNotNull(persistedExclusiveEntry);
        assertNotNull(persistedSharedEntry);

        assertTrue(persistedExclusiveEntry.getRecipes().isEmpty());

        assertEquals(1, persistedSharedEntry.getRecipes().size());

        assertEquals(otherRecipeId,
                persistedSharedEntry.getRecipes().get(0).getRecipeId());
    }

    @Test
    void deleteRecipe_anotherAccountsRecipeRemainsUnchanged() {
        Account owner = createAccount();
        Account otherAccount = createAccount();

        Recipe recipe = createRecipe(owner);

        addIngredient(recipe, createIngredient());
        createNote(recipe, owner);
        createReaction(recipe, owner, ReactionType.POSITIVE);
        createTag(owner, recipe);

        Calendar calendar = createCalendar(owner);
        createCalendarEntry(calendar, recipe);

        long recipeId = recipe.getRecipeId();
        long otherAccountId = otherAccount.getAccountId();

        flushAndClear();

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> recipeService.deleteRecipe(recipeId, otherAccountId)
        );

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());

        flushAndClear();

        // Neither the recipe nor its associated data should be modified.
        assertAll(
                () -> assertEquals(1L,
                        countRecipeRows("recipe", recipeId)),
                () -> assertEquals(1L,
                        countRecipeRows("recipe_ingredient", recipeId)),
                () -> assertEquals(1L,
                        countRecipeRows("recipe_note", recipeId)),
                () -> assertEquals(1L,
                        countRecipeRows("reaction", recipeId)),
                () -> assertEquals(1L,
                        countRecipeRows("recipe_tag_recipe", recipeId)),
                () -> assertEquals(1L,
                        countRecipeRows("calendar_entry_recipe", recipeId))
        );
    }

    @Test
    void deleteRecipe_ownerlessRecipeCannotBeDeleted() {
        Account account = createAccount();
        Recipe recipe = createRecipe(null);

        long accountId = account.getAccountId();
        long recipeId = recipe.getRecipeId();

        flushAndClear();

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> recipeService.deleteRecipe(recipeId, accountId)
        );

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());

        flushAndClear();

        Recipe persisted = entityManager.find(Recipe.class, recipeId);

        assertNotNull(persisted);
        assertNull(persisted.getAccount());
    }

    @Test
    void deleteRecipe_nonexistentRecipeReturnsNotFound() {
        Account account = createAccount();
        long accountId = account.getAccountId();

        flushAndClear();

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> recipeService.deleteRecipe(
                        Long.MAX_VALUE, accountId)
        );

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
    }

    @Test
    void deleteRecipe_repeatedDeletionReturnsNotFound() {
        Account account = createAccount();
        Recipe recipe = createRecipe(account);

        long accountId = account.getAccountId();
        long recipeId = recipe.getRecipeId();

        flushAndClear();

        recipeService.deleteRecipe(recipeId, accountId);

        flushAndClear();

        assertRecipeDeleted(recipeId);

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> recipeService.deleteRecipe(recipeId, accountId)
        );

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
    }

    @Test
    void deleteRecipe_allRelationshipsAreRemovedTogether() {
        Account owner = createAccount();
        Account otherAccount = createAccount();

        Recipe recipe = createRecipe(owner);

        Ingredient ingredient = createIngredient();
        addIngredient(recipe, ingredient);

        createNote(recipe, owner);
        createNote(recipe, otherAccount);

        createReaction(recipe, owner, ReactionType.POSITIVE);
        createReaction(recipe, otherAccount, ReactionType.NEGATIVE);

        RecipeTag tag = createTag(otherAccount, recipe);

        Calendar calendar = createCalendar(otherAccount);

        CalendarEntry entry = createCalendarEntry(calendar, recipe);

        long recipeId = recipe.getRecipeId();
        long ownerId = owner.getAccountId();
        long otherAccountId = otherAccount.getAccountId();
        long ingredientId = ingredient.getIngredientId();
        long tagId = tag.getRecipeTagId();
        long calendarId = calendar.getCalendarId();
        long entryId = entry.getCalendarEntryId();

        flushAndClear();

        assertEquals(1L, countRecipeRows("recipe", recipeId));
        assertEquals(1L, countRecipeRows("recipe_ingredient", recipeId));
        assertEquals(2L, countRecipeRows("recipe_note", recipeId));
        assertEquals(2L, countRecipeRows("reaction", recipeId));
        assertEquals(1L, countRecipeRows("recipe_tag_recipe", recipeId));
        assertEquals(1L, countRecipeRows("calendar_entry_recipe", recipeId));

        recipeService.deleteRecipe(recipeId, ownerId);

        flushAndClear();

        assertRecipeDeleted(recipeId);

        // Independent entities must survive the recipe deletion.
        assertAll(
                () -> assertNotNull(
                        entityManager.find(Account.class, ownerId)),
                () -> assertNotNull(
                        entityManager.find(Account.class, otherAccountId)),
                () -> assertNotNull(
                        entityManager.find(Ingredient.class, ingredientId)),
                () -> assertNotNull(
                        entityManager.find(RecipeTag.class, tagId)),
                () -> assertNotNull(
                        entityManager.find(Calendar.class, calendarId)),
                () -> assertNotNull(
                        entityManager.find(CalendarEntry.class, entryId))
        );
    }
}