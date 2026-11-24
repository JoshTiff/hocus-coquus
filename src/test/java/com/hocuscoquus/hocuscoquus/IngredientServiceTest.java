package com.hocuscoquus.hocuscoquus;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.hocuscoquus.hocuscoquus.entity.Ingredient;
import com.hocuscoquus.hocuscoquus.repository.IngredientRepository;
import com.hocuscoquus.hocuscoquus.service.IngredientService;

@ExtendWith(MockitoExtension.class)
class IngredientServiceTest {

    @Mock
    private IngredientRepository ingredientRepository;

    @Test
    void createIngredient_savesTrimmedLowercaseName() {
        Ingredient ingredient = new Ingredient();
        ingredient.setIngredientName(" OREGANO ");
        IngredientService service = new IngredientService(ingredientRepository);
        when(ingredientRepository.save(ingredient)).thenReturn(ingredient);

        Ingredient saved = service.createIngredient(ingredient);

        assertEquals("oregano", saved.getIngredientName());
        verify(ingredientRepository).existsByIngredientName("oregano");
        verify(ingredientRepository).save(ingredient);
    }

    @Test
    void createIngredient_blankNameIsRejected() {
        Ingredient ingredient = new Ingredient();
        ingredient.setIngredientName("   ");
        IngredientService service = new IngredientService(ingredientRepository);

        assertThrows(IllegalArgumentException.class,
                () -> service.createIngredient(ingredient));

        verifyNoInteractions(ingredientRepository);
    }
}
