package com.hocuscoquus.hocuscoquus;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.hocuscoquus.hocuscoquus.config.SecurityConfig;
import com.hocuscoquus.hocuscoquus.controller.RecipeExportController;
import com.hocuscoquus.hocuscoquus.entity.Account;
import com.hocuscoquus.hocuscoquus.entity.AccountDetails;
import com.hocuscoquus.hocuscoquus.service.RecipeExportService;

/** Tests the HTTP endpoint for sharing a recipe by email. */
@WebMvcTest(RecipeExportController.class)
@Import(SecurityConfig.class)
class RecipeExportControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private RecipeExportService recipeExportService;

    /**
     * Creates an authenticated test user for requests to the secured endpoint.
     *
     * @return test account details used to authenticate MockMvc requests
     */
    private AccountDetails testUser() {
        Account account = new Account();
        account.setAccountId(1L);
        account.setFirstName("Test");
        account.setEmail("test@example.com");
        account.setPassword("storedHash");
        return new AccountDetails(account);
    }

    /**
     * Ensures a valid request returns the generated mailto URI as plain text.
     *
     * @throws Exception if the MockMvc request or response assertion fails
     */
    @Test
    void shareRecipeByEmail_returnsPrefilledMailtoUri() throws Exception {
        when(recipeExportService.shareRecipeByEmail(7L, "friend@example.com"))
                .thenReturn("mailto:friend@example.com?subject=Recipe");

        mockMvc.perform(post("/api/recipe/7/share/email")
                .with(user(testUser()))
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "recipientEmail": "friend@example.com"
                    }
                    """))
                .andExpect(status().isOk())
                    .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_PLAIN))
                    .andExpect(content().string("mailto:friend@example.com?subject=Recipe"));
    }

    /**
     * Ensures unauthenticated users can prepare a recipe email.
     *
     * @throws Exception if the MockMvc request or response assertion fails
     */
    @Test
    void shareRecipeByEmail_allowsUnauthenticatedUser() throws Exception {
        when(recipeExportService.shareRecipeByEmail(7L, "friend@example.com"))
                .thenReturn("mailto:friend@example.com?subject=Recipe");

        mockMvc.perform(post("/api/recipe/7/share/email")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "recipientEmail": "friend@example.com"
                    }
                    """))
                .andExpect(status().isOk())
                .andExpect(content().string("mailto:friend@example.com?subject=Recipe"));
    }

    /**
     * Ensures an invalid email is rejected before the service is called.
     *
     * @throws Exception if the MockMvc request or response assertion fails
     */
    @Test
    void shareRecipeByEmail_rejectsInvalidRecipient() throws Exception {
        mockMvc.perform(post("/api/recipe/7/share/email")
                .with(user(testUser()))
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "recipientEmail": "not-an-email"
                    }
                    """))
                .andExpect(status().isBadRequest());

        verify(recipeExportService, never()).shareRecipeByEmail(7L, "not-an-email");
    }
}
