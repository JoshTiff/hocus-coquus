package com.hocuscoquus.hocuscoquus;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.NoSuchElementException;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import com.hocuscoquus.hocuscoquus.entity.Account;
import com.hocuscoquus.hocuscoquus.entity.Recipe;
import com.hocuscoquus.hocuscoquus.entity.RecipeNote;
import com.hocuscoquus.hocuscoquus.model.RecipeNoteCreateRequest;
import com.hocuscoquus.hocuscoquus.repository.IngredientRepository;
import com.hocuscoquus.hocuscoquus.repository.RecipeIngredientRepository;
import com.hocuscoquus.hocuscoquus.repository.RecipeRepository;
import com.hocuscoquus.hocuscoquus.service.IngredientService;
import com.hocuscoquus.hocuscoquus.service.RecipeService;

import jakarta.persistence.EntityManager;

@ExtendWith(MockitoExtension.class)
class AddRecipeNoteUnitTest {

    @Mock
    private RecipeRepository recipeRepository;

    @Mock
    private IngredientRepository ingredientRepository;

    @Mock
    private RecipeIngredientRepository recipeIngredientRepository;

    @Mock
    private IngredientService ingredientService;

    @Mock
    private EntityManager entityManager;

    @Mock
    private Recipe recipe;

    private RecipeService recipeService;
    private Account account;

    @BeforeEach
    void setUp() {
        recipeService = new RecipeService(
                ingredientService,
                recipeRepository,
                recipeIngredientRepository,
                ingredientRepository);

        ReflectionTestUtils.setField(
                recipeService, "entityManager", entityManager);

        account = new Account();
        account.setAccountId(1L);
    }

    @Test
    void addRecipeNote_validRequest_savesNote() {
        when(recipeRepository.findById(Long.valueOf(10L)))
                .thenReturn(Optional.of(recipe));

        when(entityManager.find(Account.class, 1L))
                .thenReturn(account);

        RecipeNoteCreateRequest request =
                new RecipeNoteCreateRequest(
                        10L, "Try adding more garlic");

        RecipeNote result =
                recipeService.addRecipeNote(1L, request);

        assertNotNull(result);
        assertSame(recipe, result.getRecipe());
        assertSame(account, result.getAccount());
        assertEquals("Try adding more garlic", result.getMessage());

        verify(entityManager).persist(result);
    }

    @Test
    void addRecipeNote_messageIsTrimmed() {
        when(recipeRepository.findById(Long.valueOf(10L)))
                .thenReturn(Optional.of(recipe));

        when(entityManager.find(Account.class, 1L))
                .thenReturn(account);

        RecipeNoteCreateRequest request =
                new RecipeNoteCreateRequest(
                        10L, "  Add more salt  ");

        RecipeNote result =
                recipeService.addRecipeNote(1L, request);

        assertEquals("Add more salt", result.getMessage());
        verify(entityManager).persist(result);
    }

    @Test
    void addRecipeNote_nullRequest_throwsException() {
        assertThrows(IllegalArgumentException.class,
                () -> recipeService.addRecipeNote(1L, null));

        verifyNoInteractions(recipeRepository, entityManager);
    }

    @Test
    void addRecipeNote_invalidRecipeId_throwsException() {
        RecipeNoteCreateRequest request =
                new RecipeNoteCreateRequest(-1L, "Test note");

        assertThrows(IllegalArgumentException.class,
                () -> recipeService.addRecipeNote(1L, request));

        verifyNoInteractions(recipeRepository, entityManager);
    }

    @Test
    void addRecipeNote_blankMessage_throwsException() {
        RecipeNoteCreateRequest request =
                new RecipeNoteCreateRequest(10L, "   ");

        assertThrows(IllegalArgumentException.class,
                () -> recipeService.addRecipeNote(1L, request));

        verifyNoInteractions(recipeRepository, entityManager);
    }

    @Test
    void addRecipeNote_messageTooLong_throwsException() {
        RecipeNoteCreateRequest request =
                new RecipeNoteCreateRequest(10L, "a".repeat(256));

        assertThrows(IllegalArgumentException.class,
                () -> recipeService.addRecipeNote(1L, request));

        verifyNoInteractions(recipeRepository, entityManager);
    }

    @Test
    void addRecipeNote_recipeNotFound_throwsException() {
        when(recipeRepository.findById(Long.valueOf(999L)))
                .thenReturn(Optional.empty());

        RecipeNoteCreateRequest request =
                new RecipeNoteCreateRequest(999L, "Test note");

        assertThrows(NoSuchElementException.class,
                () -> recipeService.addRecipeNote(1L, request));

        verifyNoInteractions(entityManager);
    }

    @Test
    void addRecipeNote_accountNotFound_throwsException() {
        when(recipeRepository.findById(Long.valueOf(10L)))
                .thenReturn(Optional.of(recipe));

        when(entityManager.find(Account.class, 999L))
                .thenReturn(null);

        RecipeNoteCreateRequest request =
                new RecipeNoteCreateRequest(10L, "Test note");

        assertThrows(NoSuchElementException.class,
                () -> recipeService.addRecipeNote(999L, request));

        verify(entityManager, never()).persist(any());
    }
}
