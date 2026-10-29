package com.hocuscoquus.hocuscoquus.integration.spoonacular;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;

/**
 * Provides REST endpoints for searching recipes and retrieving
 * recipe information through the Spoonacular API.
 */
@RestController
@RequestMapping("/api/spoonacular")
public class SpoonacularController {

    private final SpoonacularClient client;

    /**
     * Constructs a SpoonacularController with the specified
     * Spoonacular client.
     *
     * @param client the client used to communicate with Spoonacular
     */
    public SpoonacularController(SpoonacularClient client) {
        this.client = client;
    }

    /**
     * Searches for recipes matching the specified query.
     *
     * @param query the recipe search query
     * @param number the maximum number of recipes to return
     * @return the matching recipes as a JSON string
     */
    @GetMapping(value = "/search",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public String search(
            @RequestParam String query,
            @RequestParam(defaultValue = "10") int number) {

        return client.searchRecipes(query, number);
    }

    /**
     * Retrieves detailed information about a recipe.
     *
     * @param id the unique Spoonacular ID of the recipe
     * @return the recipe information as a JSON string
     */
    @GetMapping(value = "/recipes/{id}",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public String getRecipe(@PathVariable long id) {
        return client.getRecipeInformation(id);
    }

    /**
     * Searches for recipes using the specified ingredients.
     * Prioritizes recipes that use the greatest number of
     * provided ingredients.
     *
     * @param ingredients a comma-separated list of ingredients
     * @param number the maximum number of recipes to return
     * @return the matching recipes as a JSON string
     */
    @GetMapping(value = "/ingredients",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public String searchByIngredients(
            @RequestParam String ingredients,
            @RequestParam(defaultValue = "10") int number) {

        return client.searchRecipesByIngredients(ingredients, number);
    }

    /**
     * Retrieves recipes similar to the specified recipe.
     *
     * @param id the unique Spoonacular ID of the recipe
     * @param number the maximum number of recipes to return
     * @return the similar recipes as a JSON string
     */
    @GetMapping(value = "/recipes/{id}/similar",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public String getSimilarRecipes(
            @PathVariable long id,
            @RequestParam(defaultValue = "10") int number) {

        return client.getSimilarRecipes(id, number);
    }

    /**
     * Retrieves random recipes from Spoonacular.
     * Returns one recipe by default.
     *
     * @param number the number of random recipes to retrieve
     * @return the random recipes as a JSON string
     */
    @GetMapping(value = "/random",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public String getRandomRecipes(
            @RequestParam(defaultValue = "1") int number) {

        return client.getRandomRecipes(number);
    }

    /**
     * Handles invalid parameters supplied to recipe endpoints.
     *
     * @param exception the invalid argument exception
     * @return an HTTP 400 response describing the error
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleInvalidInput(
            IllegalArgumentException exception) {

        return ResponseEntity.badRequest()
                .body(Map.of("error", exception.getMessage()));
    }

    /**
     * Handles unsuccessful HTTP responses from Spoonacular.
     * Prevents upstream error details from being exposed.
     *
     * @param exception the Spoonacular response exception
     * @return an appropriate HTTP error response
     */
    @ExceptionHandler(RestClientResponseException.class)
    public ResponseEntity<Map<String, String>> handleApiError(
            RestClientResponseException exception) {

        int status = exception.getStatusCode().value();

        if (status == 402 || status == 429) {
            return ResponseEntity
                    .status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body(Map.of("error",
                            "Spoonacular API quota or rate limit exceeded"));
        }

        return ResponseEntity
                .status(HttpStatus.BAD_GATEWAY)
                .body(Map.of("error",
                        "Unable to retrieve recipes from Spoonacular"));
    }

    /**
     * Handles connection failures and other REST client errors
     * when communicating with Spoonacular.
     *
     * @param exception the REST client exception
     * @return an HTTP 502 response describing the failure
     */
    @ExceptionHandler(RestClientException.class)
    public ResponseEntity<Map<String, String>> handleConnectionError(
            RestClientException exception) {

        return ResponseEntity
                .status(HttpStatus.BAD_GATEWAY)
                .body(Map.of("error",
                        "Unable to connect to Spoonacular"));
    }
}
