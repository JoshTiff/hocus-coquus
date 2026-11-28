package com.hocuscoquus.hocuscoquus;

import com.hocuscoquus.hocuscoquus.config.SecurityConfig;
import com.hocuscoquus.hocuscoquus.controller.RecipeController;
import com.hocuscoquus.hocuscoquus.entity.Account;
import com.hocuscoquus.hocuscoquus.entity.AccountDetails;
import com.hocuscoquus.hocuscoquus.handler.RecipeHandler;
import com.hocuscoquus.hocuscoquus.service.RecipeService;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RecipeController.class)
@Import(SecurityConfig.class)
class DeleteRecipeControllerTest {

    private static final String URL = "/api/recipe/{recipeId}";

    private static final long ACCOUNT_ID = 1L;
    private static final long OTHER_ACCOUNT_ID = 2L;
    private static final long RECIPE_ID = 10L;

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private RecipeHandler recipeHandler;

    @MockitoBean
    private RecipeService recipeService;

    private AccountDetails testUser(long accountId) {
        Account account = new Account();
        account.setAccountId(accountId);
        account.setFirstName("Test");
        account.setEmail("test" + accountId + "@example.com");
        account.setPassword("storedHash");

        return new AccountDetails(account);
    }

    @Test
    void deleteRecipe_validRequest_returnsNoContent()
            throws Exception {

        mockMvc.perform(delete(URL, RECIPE_ID)
                .with(user(testUser(ACCOUNT_ID)))
                .with(csrf()))
                .andExpect(status().isNoContent())
                .andExpect(content().string(""));

        verify(recipeService).deleteRecipe(RECIPE_ID, ACCOUNT_ID);
    }

    @Test
    void deleteRecipe_nonexistentRecipe_returnsNotFound()
            throws Exception {

        doThrow(new ResponseStatusException(
                HttpStatus.NOT_FOUND, "Recipe not found"))
                .when(recipeService)
                .deleteRecipe(RECIPE_ID, ACCOUNT_ID);

        mockMvc.perform(delete(URL, RECIPE_ID)
                .with(user(testUser(ACCOUNT_ID)))
                .with(csrf()))
                .andExpect(status().isNotFound());

        verify(recipeService).deleteRecipe(RECIPE_ID, ACCOUNT_ID);
    }

    @Test
    void deleteRecipe_anotherAccountsRecipe_returnsNotFound()
            throws Exception {

        doThrow(new ResponseStatusException(
                HttpStatus.NOT_FOUND, "Recipe not found"))
                .when(recipeService)
                .deleteRecipe(RECIPE_ID, OTHER_ACCOUNT_ID);

        mockMvc.perform(delete(URL, RECIPE_ID)
                .with(user(testUser(OTHER_ACCOUNT_ID)))
                .with(csrf()))
                .andExpect(status().isNotFound());

        verify(recipeService)
                .deleteRecipe(RECIPE_ID, OTHER_ACCOUNT_ID);

        verify(recipeService, never())
                .deleteRecipe(RECIPE_ID, ACCOUNT_ID);
    }

    @Test
    void deleteRecipe_zeroId_returnsBadRequest()
            throws Exception {

        doThrow(new ResponseStatusException(
                HttpStatus.BAD_REQUEST, "Invalid recipe ID"))
                .when(recipeService)
                .deleteRecipe(0L, ACCOUNT_ID);

        mockMvc.perform(delete(URL, 0L)
                .with(user(testUser(ACCOUNT_ID)))
                .with(csrf()))
                .andExpect(status().isBadRequest());

        verify(recipeService).deleteRecipe(0L, ACCOUNT_ID);
    }

    @Test
    void deleteRecipe_negativeId_returnsBadRequest()
            throws Exception {

        doThrow(new ResponseStatusException(
                HttpStatus.BAD_REQUEST, "Invalid recipe ID"))
                .when(recipeService)
                .deleteRecipe(-1L, ACCOUNT_ID);

        mockMvc.perform(delete(URL, -1L)
                .with(user(testUser(ACCOUNT_ID)))
                .with(csrf()))
                .andExpect(status().isBadRequest());

        verify(recipeService).deleteRecipe(-1L, ACCOUNT_ID);
    }

    @Test
    void deleteRecipe_nonNumericId_returnsBadRequest()
            throws Exception {

        mockMvc.perform(delete(URL, "abc")
                .with(user(testUser(ACCOUNT_ID)))
                .with(csrf()))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(recipeService);
    }

    @Test
    void deleteRecipe_unauthenticatedUser_redirectsToLogin()
            throws Exception {

        mockMvc.perform(delete(URL, RECIPE_ID)
                .with(csrf()))
                .andExpect(status().isFound())
                .andExpect(redirectedUrl("/login"));

        verifyNoInteractions(recipeService);
    }

    @Test
    void deleteRecipe_suppliedAccountIdIsIgnored()
            throws Exception {

        mockMvc.perform(delete(URL, RECIPE_ID)
                .param("accountId", String.valueOf(OTHER_ACCOUNT_ID))
                .with(user(testUser(ACCOUNT_ID)))
                .with(csrf()))
                .andExpect(status().isNoContent());

        verify(recipeService).deleteRecipe(RECIPE_ID, ACCOUNT_ID);

        verify(recipeService, never())
                .deleteRecipe(RECIPE_ID, OTHER_ACCOUNT_ID);
    }

    @Test
    void deleteRecipe_repeatedDeletion_returnsNotFound()
            throws Exception {

        doNothing()
                .doThrow(new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Recipe not found"))
                .when(recipeService)
                .deleteRecipe(RECIPE_ID, ACCOUNT_ID);

        mockMvc.perform(delete(URL, RECIPE_ID)
                .with(user(testUser(ACCOUNT_ID)))
                .with(csrf()))
                .andExpect(status().isNoContent());

        mockMvc.perform(delete(URL, RECIPE_ID)
                .with(user(testUser(ACCOUNT_ID)))
                .with(csrf()))
                .andExpect(status().isNotFound());

        verify(recipeService, times(2))
                .deleteRecipe(RECIPE_ID, ACCOUNT_ID);
    }
}