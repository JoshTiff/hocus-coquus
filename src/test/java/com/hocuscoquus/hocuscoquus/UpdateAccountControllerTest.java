package com.hocuscoquus.hocuscoquus;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.hocuscoquus.hocuscoquus.config.SecurityConfig;
import com.hocuscoquus.hocuscoquus.controller.AccountController;
import com.hocuscoquus.hocuscoquus.entity.Account;
import com.hocuscoquus.hocuscoquus.entity.AccountDetails;
import com.hocuscoquus.hocuscoquus.handler.AccountHandler;
import com.hocuscoquus.hocuscoquus.model.AccountUpdateRequest;
import com.hocuscoquus.hocuscoquus.service.AccountService;

@WebMvcTest(AccountController.class)
@Import({AccountHandler.class, SecurityConfig.class})
class UpdateAccountControllerTest {

    private static final String UPDATE_URL = "/account/api/update";

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AccountService accountService;

    // Create a simulated authenticated user
    private AccountDetails testUser() {
        Account account = new Account();
        account.setAccountId(1L);
        account.setFirstName("Test");
        account.setEmail("test@example.com");
        account.setPassword("storedHash");

        return new AccountDetails(account);
    }

    @Test
    void updateAccount_validRequest_returnsNoContent() throws Exception {
        when(accountService.updateAccount(eq(1L), any(AccountUpdateRequest.class)))
                .thenReturn(true);

        mockMvc.perform(patch(UPDATE_URL)
                .with(user(testUser()))
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "currentPassword": "oldPassword",
                        "newPassword": "newPassword",
                        "ingredientIds": [10, 20],
                        "dietaryRestrictionNames": ["Vegetarian"]
                    }
                    """))
                .andExpect(status().isNoContent());

        ArgumentCaptor<AccountUpdateRequest> captor =
                ArgumentCaptor.forClass(AccountUpdateRequest.class);

        verify(accountService).updateAccount(eq(1L), captor.capture());

        AccountUpdateRequest request = captor.getValue();

        assertEquals("oldPassword", request.currentPassword());
        assertEquals("newPassword", request.newPassword());
        assertEquals(List.of(10L, 20L), request.ingredientIds());
        assertEquals(List.of("Vegetarian"), request.dietaryRestrictionNames());
    }

    @Test
    void updateAccount_serviceRejectsRequest_returnsBadRequest() throws Exception {
        when(accountService.updateAccount(eq(1L), any(AccountUpdateRequest.class)))
                .thenReturn(false);

        mockMvc.perform(patch(UPDATE_URL)
                .with(user(testUser()))
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "currentPassword": "wrongPassword",
                        "newPassword": "newPassword"
                    }
                    """))
                .andExpect(status().isBadRequest());

        verify(accountService).updateAccount(
                eq(1L), any(AccountUpdateRequest.class));
    }

    @Test
    void updateAccount_unauthenticatedUser_redirectsToLogin() throws Exception {
        mockMvc.perform(patch(UPDATE_URL)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andExpect(status().isFound())
                .andExpect(redirectedUrl("/login"));

        verify(accountService, never()).updateAccount(
                anyLong(), any(AccountUpdateRequest.class));
    }

    @Test
    void updateAccount_invalidIngredientId_returnsBadRequest() throws Exception {
        mockMvc.perform(patch(UPDATE_URL)
                .with(user(testUser()))
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "ingredientIds": [-1]
                    }
                    """))
                .andExpect(status().isBadRequest());

        verify(accountService, never()).updateAccount(
                anyLong(), any(AccountUpdateRequest.class));
    }

    @Test
    void updateAccount_nullIngredientId_returnsBadRequest() throws Exception {
        mockMvc.perform(patch(UPDATE_URL)
                .with(user(testUser()))
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "ingredientIds": [null]
                    }
                    """))
                .andExpect(status().isBadRequest());

        verify(accountService, never()).updateAccount(
                anyLong(), any(AccountUpdateRequest.class));
    }

    @Test
    void updateAccount_blankRestrictionName_returnsBadRequest() throws Exception {
        mockMvc.perform(patch(UPDATE_URL)
                .with(user(testUser()))
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "dietaryRestrictionNames": ["   "]
                    }
                    """))
                .andExpect(status().isBadRequest());

        verify(accountService, never()).updateAccount(
                anyLong(), any(AccountUpdateRequest.class));
    }

    @Test
    void updateAccount_missingRequestBody_returnsBadRequest() throws Exception {
        mockMvc.perform(patch(UPDATE_URL)
                .with(user(testUser()))
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(accountService, never()).updateAccount(
                anyLong(), any(AccountUpdateRequest.class));
    }

    @Test
    void updateAccount_emptyLists_arePassedToService() throws Exception {
        when(accountService.updateAccount(eq(1L), any(AccountUpdateRequest.class)))
                .thenReturn(true);

        mockMvc.perform(patch(UPDATE_URL)
                .with(user(testUser()))
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "ingredientIds": [],
                        "dietaryRestrictionNames": []
                    }
                    """))
                .andExpect(status().isNoContent());

        ArgumentCaptor<AccountUpdateRequest> captor =
                ArgumentCaptor.forClass(AccountUpdateRequest.class);

        verify(accountService).updateAccount(eq(1L), captor.capture());

        assertEquals(List.of(), captor.getValue().ingredientIds());
        assertEquals(List.of(), captor.getValue().dietaryRestrictionNames());
        assertNull(captor.getValue().newPassword());
    }
}
