
package com.hocuscoquus.hocuscoquus;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.NoSuchElementException;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.hocuscoquus.hocuscoquus.entity.Account;
import com.hocuscoquus.hocuscoquus.entity.Recipe;
import com.hocuscoquus.hocuscoquus.entity.RecipeNote;
import com.hocuscoquus.hocuscoquus.model.RecipeNoteCreateRequest;
import com.hocuscoquus.hocuscoquus.repository.AccountRepository;
import com.hocuscoquus.hocuscoquus.repository.RecipeNoteRepository;
import com.hocuscoquus.hocuscoquus.repository.RecipeRepository;
import com.hocuscoquus.hocuscoquus.service.RecipeNoteService;

@ExtendWith(MockitoExtension.class)
class AddRecipeNoteUnitTest {

    @Mock
    private RecipeNoteRepository recipeNoteRepository;

    @Mock
    private RecipeRepository recipeRepository;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private Recipe recipe;

    private RecipeNoteService recipeNoteService;
    private Account account;

    @BeforeEach
    void setUp() {
        recipeNoteService = new RecipeNoteService(
                recipeNoteRepository,
                recipeRepository,
                accountRepository);

        account = new Account();
        account.setAccountId(1L);
    }

    @Test
    void addRecipeNote_validRequest_savesNote() {
        // Use Long.valueOf to select the Optional-returning findById
        when(recipeRepository.findById(Long.valueOf(10L)))
                .thenReturn(Optional.of(recipe));

        when(accountRepository.findById(1L))
                .thenReturn(account);

        when(recipeNoteRepository.save(any(RecipeNote.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        RecipeNoteCreateRequest request =
                new RecipeNoteCreateRequest(
                        10L, "Try adding more garlic");

        RecipeNote result =
                recipeNoteService.addRecipeNote(1L, request);

        assertNotNull(result);
        assertSame(recipe, result.getRecipe());
        assertSame(account, result.getAccount());
        assertEquals("Try adding more garlic", result.getMessage());

        verify(recipeNoteRepository).save(result);
    }

    @Test
    void addRecipeNote_messageIsTrimmed() {
        when(recipeRepository.findById(Long.valueOf(10L)))
                .thenReturn(Optional.of(recipe));

        when(accountRepository.findById(1L))
                .thenReturn(account);

        when(recipeNoteRepository.save(any(RecipeNote.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        RecipeNoteCreateRequest request =
                new RecipeNoteCreateRequest(
                        10L, "  Add more salt  ");

        RecipeNote result =
                recipeNoteService.addRecipeNote(1L, request);

        assertEquals("Add more salt", result.getMessage());
    }

    @Test
    void addRecipeNote_nullRequest_throwsException() {
        assertThrows(IllegalArgumentException.class,
                () -> recipeNoteService.addRecipeNote(1L, null));

        verifyNoInteractions(
                recipeRepository,
                accountRepository,
                recipeNoteRepository);
    }

    @Test
    void addRecipeNote_invalidRecipeId_throwsException() {
        RecipeNoteCreateRequest request =
                new RecipeNoteCreateRequest(
                        -1L, "Test note");

        assertThrows(IllegalArgumentException.class,
                () -> recipeNoteService.addRecipeNote(1L, request));

        verifyNoInteractions(
                recipeRepository,
                accountRepository,
                recipeNoteRepository);
    }

    @Test
    void addRecipeNote_blankMessage_throwsException() {
        RecipeNoteCreateRequest request =
                new RecipeNoteCreateRequest(
                        10L, "   ");

        assertThrows(IllegalArgumentException.class,
                () -> recipeNoteService.addRecipeNote(1L, request));

        verifyNoInteractions(
                recipeRepository,
                accountRepository,
                recipeNoteRepository);
    }

    @Test
    void addRecipeNote_messageTooLong_throwsException() {
        RecipeNoteCreateRequest request =
                new RecipeNoteCreateRequest(
                        10L, "a".repeat(256));

        assertThrows(IllegalArgumentException.class,
                () -> recipeNoteService.addRecipeNote(1L, request));

        verifyNoInteractions(
                recipeRepository,
                accountRepository,
                recipeNoteRepository);
    }

    @Test
    void addRecipeNote_recipeNotFound_throwsException() {
        when(recipeRepository.findById(Long.valueOf(999L)))
                .thenReturn(Optional.empty());

        RecipeNoteCreateRequest request =
                new RecipeNoteCreateRequest(
                        999L, "Test note");

        assertThrows(NoSuchElementException.class,
                () -> recipeNoteService.addRecipeNote(1L, request));

        verifyNoInteractions(
                accountRepository,
                recipeNoteRepository);
    }

    @Test
    void addRecipeNote_accountNotFound_throwsException() {
        when(recipeRepository.findById(Long.valueOf(10L)))
                .thenReturn(Optional.of(recipe));

        when(accountRepository.findById(999L))
                .thenReturn(null);

        RecipeNoteCreateRequest request =
                new RecipeNoteCreateRequest(
                        10L, "Test note");

        assertThrows(NoSuchElementException.class,
                () -> recipeNoteService.addRecipeNote(999L, request));

        verify(recipeNoteRepository, never()).save(any());
    }
}