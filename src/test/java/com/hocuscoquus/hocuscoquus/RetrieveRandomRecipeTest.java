package com.hocuscoquus.hocuscoquus;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(properties = "spring.jpa.hibernate.ddl-auto=validate")
@AutoConfigureMockMvc
public class RetrieveRandomRecipeTest {
    @Autowired
    MockMvc mockMvc;

    @Test
    public void testRetrieveRandomRecipe_API() throws Exception {
        mockMvc.perform(get("/api/recipe/view-recipe").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.description").isNotEmpty())
                .andExpect(jsonPath("$.instructions").isNotEmpty())
                .andExpect(jsonPath("$.accountId").isNotEmpty());
    }

    @Test
    public void testRetrieveRandomRecipe() throws Exception {
        mockMvc.perform(get("/recipe/random").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("recipe"))
                .andExpect(model().attributeHasNoErrors("recipe"));
    }
}
