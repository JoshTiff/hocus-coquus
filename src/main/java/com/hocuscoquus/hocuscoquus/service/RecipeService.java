package com.hocuscoquus.hocuscoquus.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;

import com.hocuscoquus.hocuscoquus.entity.*;
import com.hocuscoquus.hocuscoquus.repository.RecipeTagRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.hocuscoquus.hocuscoquus.model.IngredientCreateDto;
import com.hocuscoquus.hocuscoquus.model.IngredientCreateRequest;
import com.hocuscoquus.hocuscoquus.model.IngredientModel;
import com.hocuscoquus.hocuscoquus.model.ReactionType;
import com.hocuscoquus.hocuscoquus.model.RecipeCreateRequest;
import com.hocuscoquus.hocuscoquus.model.RecipeIngredientCreateRequest;
import com.hocuscoquus.hocuscoquus.model.RecipeNoteCreateRequest;
import com.hocuscoquus.hocuscoquus.model.RecipeEditRequest;
import com.hocuscoquus.hocuscoquus.model.RecipeEditRequest.IngredientRow;
import com.hocuscoquus.hocuscoquus.repository.IngredientRepository;
import com.hocuscoquus.hocuscoquus.repository.RecipeIngredientRepository;
import com.hocuscoquus.hocuscoquus.repository.RecipeRepository;

@Service
public class RecipeService {
	
	private final IngredientService ingredientService;
    private final RecipeRepository recipeRepository;
    private final IngredientRepository ingredientRepository;
    @Autowired
    RecipeTagRepository tagRepository;
    @Autowired
    AccountService accountService;
    @PersistenceContext
    private EntityManager entityManager;

    // TODO: review/add constructor(s)
    public RecipeService(IngredientService ingredientService, RecipeRepository recipeRepository, RecipeIngredientRepository recipeIngredientRepository, IngredientRepository ingredientRepository) {
    	this.ingredientService = Objects.requireNonNull(ingredientService);
    	this.recipeRepository = Objects.requireNonNull(recipeRepository);
    	this.ingredientRepository = Objects.requireNonNull(ingredientRepository);
    }

    
    @Transactional
    public Recipe createRecipe(RecipeCreateRequest request) {

    	Account account = ((AccountDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getAccount();
    	
    	List<RecipeIngredient> recipeIngredientList = new ArrayList<>();
    	
    	Recipe r = Recipe.builder()
    			.setRecipeName(request.recipeName())
    			.setRecipeDescription(request.recipeDescription())
    			.setInstructions(request.instructions())
    			.setImage(request.image())
    			.setRecipeIngredients(recipeIngredientList)
    			.setAccount(account)
    			.build();
    	 
    	for (RecipeIngredientCreateRequest i : request.ingredients()) {
    		
    		//check if ingredient exists
    		Ingredient ingredient = ingredientRepository.findByIngredientName(i.ingredient().trim().toLowerCase());
    		
    		//if it doesn't, create it
    		if (ingredient == null) {
    			ingredient = new Ingredient();
    			ingredient.setIngredientName(i.ingredient().trim().toLowerCase());
    			ingredient = ingredientService.createIngredient(ingredient);
    		}
    		
    		//create a new RecipeIngredient
    		RecipeIngredient ri = new RecipeIngredient();
    		ri.setIngredient(ingredient);
    		ri.setMeasurement(i.measurement());
    		ri.setQuantity(i.quantity());
    		ri.setRecipe(r);
    		//add it to our Recipe's Ingredient List
    		r.getRecipeIngredients().add(ri);
    	}
    	
    	
    	return recipeRepository.save(r);
    }


    public List<Recipe> searchRecipes() {
        // TODO: add implementation for this method

        List<Recipe> recipes = new ArrayList<>();
        return recipes;
    }

    public List<Recipe> listOwnedRecipes(long accountId) {
        return recipeRepository.findByAccount_AccountIdOrderByRecipeNameAsc(accountId);
    }

    public Recipe getOwnedRecipe(long recipeId, long accountId) {
        return recipeRepository.findByRecipeIdAndAccount_AccountId(recipeId, accountId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Recipe not found"));
    }

    @Transactional(readOnly = true)
    public RecipeEditRequest loadRecipeForEdit(long recipeId, long accountId) {
        Recipe recipe = getOwnedRecipe(recipeId, accountId);
        List<IngredientRow> ingredients = recipe.getRecipeIngredients().stream()
                .map(row -> new IngredientRow(row.getIngredient().getIngredientName(),
                        row.getQuantity(), row.getMeasurement()))
                .toList();
        return new RecipeEditRequest(recipe.getRecipeName(), recipe.getRecipeDescription(),
                recipe.getInstructions(), recipe.getImage(), ingredients);
    }

    @Transactional
    public void updateRecipe(long recipeId, long accountId, RecipeEditRequest request) {
        Recipe recipe = recipeRepository.findForUpdateByRecipeIdAndAccount_AccountId(recipeId, accountId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Recipe not found"));
        recipe.setRecipeName(request.recipeName());
        recipe.setRecipeDescription(request.recipeDescription());
        recipe.setInstructions(request.instructions());
        if (request.image() != null) {
            recipe.setImage(request.image().isBlank() ? null : request.image().strip());
        }
        if (request.ingredients() != null) {
            replaceRecipeIngredients(recipe, request.ingredients());
        }
    }

    private void replaceRecipeIngredients(Recipe recipe, List<IngredientRow> inputs) {
        List<RecipeIngredient> rows = new ArrayList<>();
        for (IngredientRow input : inputs) {
            if (!Double.isFinite(input.quantity())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid ingredient quantity");
            }
            String name = input.ingredient().strip().toLowerCase();
            Ingredient ingredient = ingredientRepository.findByIngredientName(name);
            if (ingredient == null) {
                Ingredient newIngredient = new Ingredient();
                newIngredient.setIngredientName(name);
                ingredient = ingredientService.createIngredient(newIngredient);
            }

            RecipeIngredient row = new RecipeIngredient();
            row.setRecipe(recipe);
            row.setIngredient(ingredient);
            row.setQuantity(input.quantity());
            row.setMeasurement(input.measurement());
            rows.add(row);
        }
        recipe.getRecipeIngredients().clear();
        recipe.getRecipeIngredients().addAll(rows);
    }

    public void deleteRecipe(long recipeId) {
        // TODO: add implementation for this method
    }

    public Recipe getRandomRecipe() {
        // TODO: add implementation for this method
        return null;
    }

    public Recipe likeRecipe(long recipeId, long accountId) {
        // TODO: add implementation for this method
        return null;
    }

    public Recipe dislikeRecipe(long recipeId, long accountId) {
        // TODO: add implementation for this method
        return null;
    }

    /**
     * Used to compute the number of reactions in the {@code reactions} array of a specific type.
     * @param reactionType The type of reaction we want to count.
     * @return The number of reactions in {@code reactions} of the specified type.
     */
    public int computeNumReactions(long recipeId, ReactionType reactionType) {
        Recipe recipe = recipeRepository.findByRecipeId(recipeId);
        if (recipe == null) {
            throw new IllegalArgumentException("Recipe not found: " + recipeId);
        }
        return recipe.computeNumReactions().get(reactionType);
    }

    @Transactional
    public Map<ReactionType, Integer> addReactionToRecipe(long recipeId, ReactionType type) {
        Recipe recipe = findRecipe(recipeId);
        Account account = currentAccount();
        Reaction reaction = recipeRepository.findByRecipe_RecipeIdAndAccount_AccountId(
                recipeId, account.getAccountId());

        if (reaction == null) {
            reaction = new Reaction();
            reaction.setRecipe(recipe);
            reaction.setAccount(account);
            recipe.getReactions().add(reaction);
        }
        reaction.setReactionType(type);
        entityManager.persist(reaction);
        return recipe.computeNumReactions();
    }

    @Transactional
    public Map<ReactionType, Integer> removeReactionFromRecipe(long recipeId) {
        Recipe recipe = findRecipe(recipeId);
        long authenticatedAccountId = currentAccount().getAccountId();
        Reaction reaction = recipeRepository.findByRecipe_RecipeIdAndAccount_AccountId(
                recipeId, authenticatedAccountId);
        if (reaction != null) {
            recipe.getReactions().remove(reaction);
            entityManager.remove(reaction);
        }
        return recipe.computeNumReactions();
    }

    @Transactional
    public RecipeNote addRecipeNote(
            long accountId, RecipeNoteCreateRequest request) {

        if (request == null
                || request.recipeId() == null
                || request.recipeId() <= 0
                || request.message() == null
                || request.message().isBlank()
                || request.message().length() > 255) {
            throw new IllegalArgumentException("Invalid recipe note");
        }

        Recipe recipe = recipeRepository.findById(request.recipeId())
                .orElseThrow(() ->
                        new NoSuchElementException("Recipe not found"));

        Account account = entityManager.find(Account.class, accountId);

        if (account == null) {
            throw new NoSuchElementException("Account not found");
        }

        RecipeNote note = new RecipeNote();
        note.setRecipe(recipe);
        note.setAccount(account);
        note.setMessage(request.message().trim());

        entityManager.persist(note);

        return note;
    }

    private Recipe findRecipe(long recipeId) {
        Recipe recipe = recipeRepository.findByRecipeId(recipeId);
        if (recipe == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Recipe not found: " + recipeId);
        }
        return recipe;
    }

    private Account currentAccount() {
        return ((AccountDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getAccount();
    }

    public List<Recipe> searchRecipesByIngredients(List<IngredientModel> ingredients) {
        // TODO: add implementation for this method

        //return recipeRepository.findByIngredients(List.copyOf(ingredients));
    	return null;
    }

    public void scaleIngredients(long recipeId, long scalar) {
       // TODO: add implementation for this method
    }

    public void downloadRecipe() {
        // TODO: add implementation for this method
    }

    public void printRecipe() {
        // TODO: add implementation for this method
    }

    public void shareRecipeBySMS() {
        // TODO: add implementation for this method
    }

    /**
     * Tags a given recipe with a recipe tag for a given account.
     * @param accountID {@link Account} tagging the recipe.
     * @param recipeID ID of the {@link Recipe} object being tagged.
     * @param tagName String tag name.
     * @return {@link RecipeTag} object representation of the added tag.
     */
    @Transactional
    public RecipeTag addRecipeTag(long accountID, long recipeID, String tagName) {
        if (tagName != null && !tagName.isBlank()) {
            // Get tag by account and tag name and add it as a new tag if it is not present.
            Recipe recipe = getRecipeByID(recipeID);
            Account account = accountService.getAccountByID(accountID);
            RecipeTag recipeTag = tagRepository.findByAccountAndTag(account, tagName);
            if (recipeTag == null) {
                recipeTag = new RecipeTag();
                recipeTag.setTag(tagName);
                recipeTag.setAccount(accountService.getAccountByID(accountID));
                recipeTag.getRecipes().add(recipe);
            }
            // Ensure recipe not already associated with tag.
            if (!recipeTag.getRecipes().contains(recipe)) {
                recipeTag.getRecipes().add(recipe);
            }
            tagRepository.save(recipeTag);
            return recipeTag;
        } else {
            throw new IllegalArgumentException("Tag name cannot be empty!");
        }
    }

    /**
     * Removes an entire recipe tag from a given account.
     * @param accountID {@link Account} tagging the recipe.
     * @param recipeTagID ID of the {@link RecipeTag} object to remove from the account.
     * @return {@link RecipeTag} object representation of the removed tag.
     */
    @Transactional
    public RecipeTag removeRecipeTag(long accountID, long recipeTagID) {
        RecipeTag recipeTag = tagRepository.findByRecipeTagId(recipeTagID);
        if (recipeTag != null) {
            // Get related recipes while we are inside a @Transactional class.
            // If there are no related recipes, return an empty array list.
            if (recipeTag.getRecipes().isEmpty())
                recipeTag.setRecipes(new ArrayList<>(0));
            if (recipeTag.getAccount().getAccountId() == accountID) {
                tagRepository.deleteByRecipeTagId(recipeTagID);
                return recipeTag;
            } else {
                throw new IllegalArgumentException("Cannot delete another user's recipe tag.");
            }
        } else {
            throw new IllegalArgumentException("Recipe tag ID not found.");
        }
    }

    /**
     * Removes a recipe tag from a given recipe for a given account.
     * @param accountID {@link Account} from which to remove the recipe tag for the given recipe.
     * @param recipeTagID ID of the {@link RecipeTag} object to remove from the recipe.
     * @param recipeID ID of the {@link Recipe} from which to remove the tag.
     * @return {@link RecipeTag} object representation of the removed tag.
     */
    @Transactional
    public RecipeTag removeRecipeTagFromRecipe(long accountID, long recipeTagID, long recipeID) {
        RecipeTag recipeTag = tagRepository.findByRecipeTagId(recipeTagID);
        Recipe recipe = getRecipeByID(recipeID);
        if (recipe != null) {
            if (recipeTag != null) {
                // Get related recipes while we are inside a @Transactional class.
                if (recipeTag.getAccount().getAccountId() == accountID) {
                    // Remove tag from recipe.
                    recipe.getRecipeTags().remove(recipeTag);
                    // Remove recipe from tag.
                    recipeTag.getRecipes().remove(recipe);
                    // Save both tag and recipe to update.
                    tagRepository.save(recipeTag);
                    recipeRepository.save(recipe);
                    return recipeTag;
                } else {
                    throw new IllegalArgumentException("Cannot delete another user's recipe tag.");
                }
            } else {
                throw new IllegalArgumentException("Recipe tag ID not found.");
            }
        } else {
            throw new IllegalArgumentException("Recipe ID not found.");
        }
    }

    /**
     * Used to retrieve all recipe tags associated with a given account ID.
     * @param accountID ID of the {@link Account} object from which to query recipe tags.
     * @return A list of {@link RecipeTag} objects representing the account's recipe tags.
     */
    public List<RecipeTag> getRecipeTagsByAccount(long accountID) {
        Account account = accountService.getAccountByID(accountID);
        if (account == null)
            throw new IllegalArgumentException("No account found for given ID.");
        return tagRepository.findByAccount(account);
    }

    /**
     * Used to retrieve a specific recipe tag from an account.
     * @param accountId ID of the {@link Account} object from which to query the recipe tag.
     * @param recipeTagId ID of the {@link RecipeTag} object we want to query.
     * @return A {@link RecipeTag} object representation of the account's recipe tag.
     */
    public RecipeTag getRecipeTag(long accountId, long recipeTagId) {
        RecipeTag recipeTag = tagRepository.findByRecipeTagId(recipeTagId);
        if (recipeTag.getAccount().getAccountId() != accountId)
            throw new IllegalArgumentException("Recipe tag ID not associated with account.");
        return recipeTag;
    }

    public Recipe getRecipeByID(long recipeID) {
        return recipeRepository.getRecipeByRecipeId(recipeID);
    }
}
