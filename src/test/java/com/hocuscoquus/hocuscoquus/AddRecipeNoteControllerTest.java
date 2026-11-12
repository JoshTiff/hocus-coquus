package com.hocuscoquus.hocuscoquus;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.NoSuchElementException;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.hocuscoquus.hocuscoquus.config.SecurityConfig;
import com.hocuscoquus.hocuscoquus.controller.RecipeNoteController;
import com.hocuscoquus.hocuscoquus.entity.Account;
import com.hocuscoquus.hocuscoquus.entity.AccountDetails;
import com.hocuscoquus.hocuscoquus.entity.Recipe;
import com.hocuscoquus.hocuscoquus.entity.RecipeNote;
import com.hocuscoquus.hocuscoquus.model.RecipeNoteCreateRequest;
import com.hocuscoquus.hocuscoquus.service.RecipeNoteService;

@WebMvcTest(RecipeNoteController.class)
@Import(SecurityConfig.class)
class AddRecipeNoteControllerTest {

    private static final String URL = "/api/recipe-note/create";

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private RecipeNoteService recipeNoteService;

    private AccountDetails testUser() {
        Account account = new Account();
        account.setAccountId(1L);
        account.setFirstName("Test");
        account.setEmail("test@example.com");
        account.setPassword("storedHash");

        return new AccountDetails(account);
    }

    private RecipeNote testNote() {
        Account account = new Account();
        account.setAccountId(1L);

        Recipe recipe = Recipe.builder()
                .setRecipeName("Test Recipe")
                .build();
        recipe.setRecipeId(10L);

        RecipeNote note = new RecipeNote();
        note.setRecipeNoteId(5L);
        note.setRecipe(recipe);
        note.setAccount(account);
        note.setMessage("Add more garlic");

        return note;
    }

    @Test
    void addRecipeNote_validRequest_returnsCreated() throws Exception {
        when(recipeNoteService.addRecipeNote(
                eq(1L), any(RecipeNoteCreateRequest.class)))
                .thenReturn(testNote());

        mockMvc.perform(post(URL)
                .with(user(testUser()))
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "recipeId": 10,
                        "message": "Add more garlic"
                    }
                    """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.recipeNoteId").value(5))
                .andExpect(jsonPath("$.recipeId").value(10))
                .andExpect(jsonPath("$.accountId").value(1))
                .andExpect(jsonPath("$.message").value("Add more garlic"));

        ArgumentCaptor<RecipeNoteCreateRequest> captor =
                ArgumentCaptor.forClass(RecipeNoteCreateRequest.class);

        verify(recipeNoteService).addRecipeNote(eq(1L), captor.capture());

        assertEquals(10L, captor.getValue().recipeId());
        assertEquals("Add more garlic", captor.getValue().message());
    }

    @Test
    void addRecipeNote_recipeNotFound_returnsNotFound() throws Exception {
        when(recipeNoteService.addRecipeNote(
                eq(1L), any(RecipeNoteCreateRequest.class)))
                .thenThrow(new NoSuchElementException("Recipe not found"));

        mockMvc.perform(post(URL)
                .with(user(testUser()))
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "recipeId": 999,
                        "message": "Test note"
                    }
                    """))
                .andExpect(status().isNotFound());
    }

    @Test
    void addRecipeNote_serviceRejectsRequest_returnsBadRequest()
            throws Exception {

        when(recipeNoteService.addRecipeNote(
                eq(1L), any(RecipeNoteCreateRequest.class)))
                .thenThrow(new IllegalArgumentException("Invalid recipe note"));

        mockMvc.perform(post(URL)
                .with(user(testUser()))
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "recipeId": 10,
                        "message": "Test note"
                    }
                    """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void addRecipeNote_blankMessage_returnsBadRequest() throws Exception {
        mockMvc.perform(post(URL)
                .with(user(testUser()))
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "recipeId": 10,
                        "message": "   "
                    }
                    """))
                .andExpect(status().isBadRequest());

        verify(recipeNoteService, never()).addRecipeNote(
                eq(1L), any(RecipeNoteCreateRequest.class));
    }

    @Test
    void addRecipeNote_missingRecipeId_returnsBadRequest() throws Exception {
        mockMvc.perform(post(URL)
                .with(user(testUser()))
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "message": "Test note"
                    }
                    """))
                .andExpect(status().isBadRequest());

        verify(recipeNoteService, never()).addRecipeNote(
                eq(1L), any(RecipeNoteCreateRequest.class));
    }

    @Test
    void addRecipeNote_negativeRecipeId_returnsBadRequest() throws Exception {
        mockMvc.perform(post(URL)
                .with(user(testUser()))
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "recipeId": -1,
                        "message": "Test note"
                    }
                    """))
                .andExpect(status().isBadRequest());

        verify(recipeNoteService, never()).addRecipeNote(
                eq(1L), any(RecipeNoteCreateRequest.class));
    }

    @Test
    void addRecipeNote_messageTooLong_returnsBadRequest() throws Exception {
        String body = """
                {
                    "recipeId": 10,
                    "message": "%s"
                }
                """.formatted("a".repeat(256));

        mockMvc.perform(post(URL)
                .with(user(testUser()))
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(status().isBadRequest());

        verify(recipeNoteService, never()).addRecipeNote(
                eq(1L), any(RecipeNoteCreateRequest.class));
    }

    @Test
    void addRecipeNote_unauthenticatedUser_redirectsToLogin()
            throws Exception {

        mockMvc.perform(post(URL)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "recipeId": 10,
                        "message": "Test note"
                    }
                    """))
                .andExpect(status().isFound())
                .andExpect(redirectedUrl("/login"));

        verify(recipeNoteService, never()).addRecipeNote(
                eq(1L), any(RecipeNoteCreateRequest.class));
    }
}
