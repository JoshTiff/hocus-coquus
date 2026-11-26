package com.hocuscoquus.hocuscoquus;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import com.hocuscoquus.hocuscoquus.controller.DietaryRestrictionController;
import com.hocuscoquus.hocuscoquus.entity.Account;
import com.hocuscoquus.hocuscoquus.entity.AccountDetails;
import com.hocuscoquus.hocuscoquus.entity.DietaryRestriction;
import com.hocuscoquus.hocuscoquus.model.DietaryRestrictionCreateRequest;
import com.hocuscoquus.hocuscoquus.model.DietaryRestrictionRecord;
import com.hocuscoquus.hocuscoquus.service.AccountDietaryRestrictionService;

@ExtendWith(MockitoExtension.class)
class DietaryRestrictionControllerTest {

    @Mock
    private AccountDietaryRestrictionService dietaryRestrictionService;

    @InjectMocks
    private DietaryRestrictionController controller;

    @Test
    void create_passesRestrictionAndManualIngredientIdsToService() {
        // Ingredient IDs sent by the client are carried into the account service.
        Account account = new Account();
        account.setAccountId(17L);
        AccountDetails details = new AccountDetails(account);
        DietaryRestrictionCreateRequest request =
                new DietaryRestrictionCreateRequest(
                        "Peanut-Free", List.of(11L, 12L));
        when(dietaryRestrictionService.createDietaryRestriction(any()))
                .thenAnswer(invocation -> {
                    DietaryRestriction restriction = invocation.getArgument(0);
                    restriction.setDietaryRestrictionId(23L);
                    return restriction;
                });

        var response = controller.create(details, request);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        DietaryRestrictionRecord body =
                assertInstanceOf(DietaryRestrictionRecord.class, response.getBody());
        assertEquals(23L, body.dietaryRestrictionId());
        assertEquals("peanut-free", body.dietaryRestrictionName());
        assertEquals(List.of(11L, 12L), body.ingredientIds());

        ArgumentCaptor<DietaryRestriction> restrictionCaptor =
                ArgumentCaptor.forClass(DietaryRestriction.class);
        verify(dietaryRestrictionService).createDietaryRestriction(
                restrictionCaptor.capture());
        assertEquals(
                List.of(11L, 12L),
                restrictionCaptor.getValue().getIngredients().stream()
                        .map(ingredient -> ingredient.getIngredientId())
                        .toList());
    }

    @Test
    void delete_returnsNotFoundWhenRestrictionIsMissingOrNotOwned() {
        // The controller hides both missing and other-account restrictions as not found.
        Account account = new Account();
        account.setAccountId(17L);
        when(dietaryRestrictionService.deleteDietaryRestriction(23L))
                .thenReturn(false);

        var response = controller.delete(new AccountDetails(account), 23L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(dietaryRestrictionService).deleteDietaryRestriction(23L);
    }

    @Test
    void create_rejectsUnauthenticatedRequest() {
        var response = controller.create(
                null,
                new DietaryRestrictionCreateRequest("Peanut-Free", List.of(11L)));

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        verifyNoInteractions(dietaryRestrictionService);
    }
}
