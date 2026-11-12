const form = document.querySelector("#recipe-form");
const fields = document.querySelector("#recipe-fields");
const status = document.querySelector("#recipe-status");
const ingredients = document.querySelector("#recipe-ingredients");
const template = document.querySelector("#ingredient-row");
const recipeUrl = form.dataset.recipeUrl;
const textFields = ["recipeName", "recipeDescription", "instructions", "image"];

function addIngredient(item = {}) {
    const row = template.content.firstElementChild.cloneNode(true);
    row.querySelector("[name=ingredient]").value = item.ingredient ?? "";
    row.querySelector("[name=quantity]").value = item.quantity ?? "";
    row.querySelector("[name=measurement]").value = item.measurement ?? "";
    row.querySelector("button").addEventListener("click", () => row.remove());
    ingredients.append(row);
}

async function loadRecipe() {
    try {
        const response = await fetch(recipeUrl, {
            headers: { Accept: "application/json" },
            cache: "no-store"
        });
        if (response.redirected) {
            status.textContent = "Sign in again to edit this recipe.";
            return;
        }
        if (response.status === 404) {
            status.textContent = "This recipe is unavailable to edit.";
            return;
        }
        if (!response.ok) {
            throw new Error("Recipe load failed");
        }

        const recipe = await response.json();
        for (const name of textFields) {
            form.elements[name].value = recipe[name] ?? "";
        }
        form.elements.numberServings.value = recipe.numberServings ?? "";
        recipe.ingredients.forEach(addIngredient);
        form.hidden = false;
        form.classList.add("account-form");
        status.textContent = "";
    } catch {
        status.textContent = "Could not load the recipe. Reload the page to try again.";
    }
}

document.querySelector("#add-ingredient").addEventListener("click", () => addIngredient());

form.addEventListener("submit", async (event) => {
    event.preventDefault();
    if (fields.disabled) {
        return;
    }

    const recipe = {};
    for (const name of textFields) {
        recipe[name] = form.elements[name].value;
    }
    recipe.numberServings = form.elements.numberServings.value === ""
        ? null
        : form.elements.numberServings.valueAsNumber;
    recipe.ingredients = Array.from(ingredients.children, (row) => ({
        ingredient: row.querySelector("[name=ingredient]").value.trim(),
        quantity: row.querySelector("[name=quantity]").valueAsNumber,
        measurement: row.querySelector("[name=measurement]").value
    }));

    if (!recipe.recipeName.trim() || recipe.ingredients.some(item => !item.ingredient || item.quantity <= 0)) {
        status.textContent = "Enter a recipe name, an ingredient name for each row, and positive quantities.";
        return;
    }

    fields.disabled = true;
    status.textContent = "Saving changes…";
    try {
        const response = await fetch(recipeUrl, {
            method: "PUT",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(recipe)
        });
        if (response.redirected) {
            status.textContent = "Sign in again before saving this recipe.";
        } else if (response.status === 204) {
            status.textContent = "Recipe saved.";
        } else if (response.status === 404) {
            status.textContent = "This recipe is unavailable to edit.";
        } else if (response.status === 400) {
            status.textContent = "Check the recipe fields and try saving again.";
        } else {
            throw new Error("Recipe save failed");
        }
    } catch {
        status.textContent = "Could not save the recipe. Try again.";
    } finally {
        fields.disabled = false;
    }
});

loadRecipe();
