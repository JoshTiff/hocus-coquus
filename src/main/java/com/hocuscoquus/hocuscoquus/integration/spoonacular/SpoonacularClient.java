package com.hocuscoquus.hocuscoquus.integration.spoonacular;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

/**
 * Handles communication with the Spoonacular API to search
 * for recipes and retrieve recipe information.
 */
@Service
public class SpoonacularClient {

    private static final int DEFAULT_RESULTS = 10;
    private static final int MAX_RESULTS = 100;

    private final RestClient client;

    /**
     * Constructs a SpoonacularClient using the configured API key.
     * Initializes the REST client with the Spoonacular API
     * base URL and authentication header.
     *
     * @param apiKey the Spoonacular API key
     */
    public SpoonacularClient(
            @Value("${spoonacular.api.key}") String apiKey) {

        if (apiKey == null || apiKey.isBlank()) {
            throw new IllegalStateException(
                    "Spoonacular API key is not configured");
        }

        this.client = RestClient.builder()
                .baseUrl("https://api.spoonacular.com")
                .defaultHeader("x-api-key", apiKey)
                .build();
    }

    /**
     * Searches Spoonacular for recipes matching the specified
     * query. Returns up to the default number of matching recipes.
     *
     * @param query the recipe search query
     * @return the recipe search results as a JSON string
     * @throws IllegalArgumentException if the query is blank
     */
    public String searchRecipes(String query) {
        return searchRecipes(query, DEFAULT_RESULTS);
    }

    /**
     * Searches Spoonacular for recipes matching the specified
     * query. Returns up to the requested number of recipes.
     *
     * @param query the recipe search query
     * @param number the maximum number of recipes to return (1-100)
     * @return the recipe search results as a JSON string
     * @throws IllegalArgumentException if the parameters are invalid
     */
    public String searchRecipes(String query, int number) {
        validateText(query, "query");
        validateNumber(number);

        return client.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/recipes/complexSearch")
                        .queryParam("query", query)
                        .queryParam("number", number)
                        .build())
                .retrieve()
                .body(String.class);
    }

    /**
     * Retrieves detailed information about a recipe
     * from Spoonacular.
     *
     * @param recipeId the unique Spoonacular ID of the recipe
     * @return the recipe information as a JSON string
     * @throws IllegalArgumentException if the ID is invalid
     */
    public String getRecipeInformation(long recipeId) {
        validateId(recipeId);

        return client.get()
                .uri("/recipes/{id}/information", recipeId)
                .retrieve()
                .body(String.class);
    }

    /**
     * Searches for recipes that use the specified ingredients.
     * Prioritizes recipes using the greatest number of provided
     * ingredients. Returns up to the default number of recipes.
     *
     * @param ingredients a comma-separated list of ingredients
     * @return the matching recipes as a JSON string
     * @throws IllegalArgumentException if ingredients are blank
     */
    public String searchRecipesByIngredients(String ingredients) {
        return searchRecipesByIngredients(ingredients, DEFAULT_RESULTS);
    }

    /**
     * Searches for recipes that use the specified ingredients.
     * Prioritizes recipes using the greatest number of provided
     * ingredients. Returns up to the requested number of recipes.
     *
     * @param ingredients a comma-separated list of ingredients
     * @param number the maximum number of recipes to return (1-100)
     * @return the matching recipes as a JSON string
     * @throws IllegalArgumentException if the parameters are invalid
     */
    public String searchRecipesByIngredients(String ingredients, int number) {
        validateText(ingredients, "ingredients");
        validateNumber(number);

        return client.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/recipes/findByIngredients")
                        .queryParam("ingredients", ingredients)
                        .queryParam("number", number)
                        .queryParam("ranking", 1)
                        .build())
                .retrieve()
                .body(String.class);
    }

    /**
     * Retrieves recipes similar to the specified recipe.
     * Returns up to the default number of similar recipes.
     *
     * @param recipeId the unique Spoonacular ID of the recipe
     * @return the similar recipes as a JSON string
     * @throws IllegalArgumentException if the ID is invalid
     */
    public String getSimilarRecipes(long recipeId) {
        return getSimilarRecipes(recipeId, DEFAULT_RESULTS);
    }

    /**
     * Retrieves recipes similar to the specified recipe.
     * Returns up to the requested number of similar recipes.
     *
     * @param recipeId the unique Spoonacular ID of the recipe
     * @param number the maximum number of recipes to return (1-100)
     * @return the similar recipes as a JSON string
     * @throws IllegalArgumentException if the parameters are invalid
     */
    public String getSimilarRecipes(long recipeId, int number) {
        validateId(recipeId);
        validateNumber(number);

        return client.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/recipes/{id}/similar")
                        .queryParam("number", number)
                        .build(recipeId))
                .retrieve()
                .body(String.class);
    }

    /**
     * Retrieves one random recipe from Spoonacular.
     *
     * @return the random recipe as a JSON string
     */
    public String getRandomRecipes() {
        return getRandomRecipes(1);
    }

    /**
     * Retrieves the specified number of random recipes
     * from Spoonacular.
     *
     * @param number the number of random recipes to retrieve (1-100)
     * @return the random recipes as a JSON string
     * @throws IllegalArgumentException if the number is invalid
     */
    public String getRandomRecipes(int number) {
        validateNumber(number);

        return client.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/recipes/random")
                        .queryParam("number", number)
                        .build())
                .retrieve()
                .body(String.class);
    }

    /**
     * Validates that a text parameter is not null or blank.
     *
     * @param value the text to validate
     * @param parameterName the name of the parameter
     * @throws IllegalArgumentException if the value is blank
     */
    private void validateText(String value, String parameterName) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(
                    parameterName + " cannot be blank");
        }
    }

    /**
     * Validates that a recipe ID is positive.
     *
     * @param recipeId the recipe ID to validate
     * @throws IllegalArgumentException if the ID is invalid
     */
    private void validateId(long recipeId) {
        if (recipeId <= 0) {
            throw new IllegalArgumentException(
                    "Recipe ID must be positive");
        }
    }

    /**
     * Validates that the requested number of recipes is within
     * the supported range.
     *
     * @param number the requested number of recipes
     * @throws IllegalArgumentException if the number is invalid
     */
    private void validateNumber(int number) {
        if (number < 1 || number > MAX_RESULTS) {
            throw new IllegalArgumentException(
                    "Number of recipes must be between 1 and "
                            + MAX_RESULTS);
        }
    }
}
