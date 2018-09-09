package np.com.aanalbasaula.foodplanner.views.recipe;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import java.util.List;

import np.com.aanalbasaula.foodplanner.R;
import np.com.aanalbasaula.foodplanner.database.AppDatabase;
import np.com.aanalbasaula.foodplanner.database.Ingredient;
import np.com.aanalbasaula.foodplanner.database.Recipe;
import np.com.aanalbasaula.foodplanner.database.utils.CreateIngredientEntryAsync;
import np.com.aanalbasaula.foodplanner.database.utils.CreateRecipeEntryAsync;
import np.com.aanalbasaula.foodplanner.database.utils.LoadIngredientsAsync;

public class NewRecipeActivity extends AppCompatActivity implements CreateRecipeEntryAsync.CreateRecipeEntryListener {

    /**
     * Activity Result Code denoting that a new recipe was added. When this result is returned to
     * the calling activity {@link NewRecipeActivity#EXTRA_RECIPE} is set with the added recipe
     */
    public static final int RESULT_RECIPE_ADDED = 100;
    /**
     * The optional result field which has just been added
     */
    public static final String EXTRA_RECIPE = "recipe";
    private static final String TAG = NewRecipeActivity.class.getSimpleName();

    List<Ingredient> ingredients;

    // the edit text with the recipe name
    private EditText editRecipeName;

    // The ingredients layout where new ingredients are added
    private LinearLayout layoutIngredients;

    // the database instance
    private AppDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_recipe);

        editRecipeName = findViewById(R.id.edit_recipe_name);
        layoutIngredients = findViewById(R.id.layout_ingredients);
        db = AppDatabase.getInstance(this);

    }

    @Override
    protected void onStart() {
        super.onStart();
        //setup the ingredients layout
        new LoadIngredientsAsync(db.getIngredientDao(), new LoadIngredientsAsync.LoadIngredientsListener() {
            @Override
            public void onIngredientsLoaded(@NonNull List<Ingredient> ingredients) {
                Log.i(TAG, "onIngredientsLoaded: Ingredients have been loaded: " + ingredients.size());
                NewRecipeActivity.this.ingredients = ingredients;
                //create the add ingredient view
                createAddIngredientView();
            }
        }).execute();
    }

    /**
     * Request that the recipe be saved into the database
     *
     * @param view the view which called this method
     */
    public void requestSaveRecipe(View view) {
        if (isInputValid()) {
            //create the recipe entry into the database
            Recipe recipe = new Recipe(editRecipeName.getText().toString());
            CreateRecipeEntryAsync async = new CreateRecipeEntryAsync(db.getRecipeDao(), this);
            async.execute(recipe);
        }
    }

    /**
     * Creates a view on the screen where user can add an ingredient, should only be called
     * once all the ingredients have been loaded from the database
     */
    private void createAddIngredientView() {
        View view = getLayoutInflater().inflate(R.layout.layout_new_ingredient, null);
        IngredientEntryViewController vc = new IngredientEntryViewController(view);
        view.setTag(vc);
        layoutIngredients.addView(view);
    }

    /**
     * Validates if the provided input on screen is valid or not
     * Also shows error on the invalid entry
     *
     * @return true if valid, else false
     */
    private boolean isInputValid() {
        String recipeName = editRecipeName.getText().toString();
        if (recipeName.isEmpty()) {
            editRecipeName.setError(getString(R.string.error_text_required_empty));
            return false;
        }
        return true;
    }

    @Override
    public void onRecipeEntriesCreated(Recipe[] recipes) {
        Log.i(TAG, "onRecipeEntriesCreated: The recipe entry has been created");
        Intent result = new Intent();
        result.putExtra(EXTRA_RECIPE, recipes[0]);
        this.setResult(RESULT_RECIPE_ADDED, result);
        this.finish();
    }

    private class IngredientEntryViewController {
        // The auto suggesting text input view
        private AutoCompleteTextView input;
        // The add button
        private ImageButton buttonAdd;

        IngredientEntryViewController(View view) {
            input = view.findViewById(R.id.edit_ingredient_name);
            //create the auto suggest adapter for the input
            ArrayAdapter<Ingredient> adapter = new ArrayAdapter<>(NewRecipeActivity.this, android.R.layout.select_dialog_item, ingredients);
            input.setAdapter(adapter);

            buttonAdd = view.findViewById(R.id.button_add);
            buttonAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.i(TAG, "onClick: Add Button Clicked");
                    if (input.getText().toString().isEmpty()) {
                        input.setError(getString(R.string.error_empty_input));
                        return;
                    }
                    addIngredient();
                    //update the views
                    buttonAdd.setVisibility(View.GONE);
                    //make the edit text uneditable
                    input.setEnabled(false);
                    createAddIngredientView();
                }
            });
        }

        /**
         * Add the ingredient into the database
         */
        private void addIngredient() {
            String name = input.getText().toString();
            if (!ingredientInDatabase(name)) {
                Log.i(TAG, "addIngredient: New ingredient input");
                final Ingredient ingredient = new Ingredient();
                ingredient.setName(name);
                Log.i(TAG, "addIngredient: Adding ingredient with name: " + ingredient.getName());
                new CreateIngredientEntryAsync(db.getIngredientDao(), new CreateIngredientEntryAsync.CreateIngredientEntryListener() {
                    @Override
                    public void onIngredientEntriesCreated(Ingredient[] ingredients) {
                        if (ingredients.length > 0) {
                            Log.i(TAG, "onIngredientEntriesCreated: Database Entry created");
                            NewRecipeActivity.this.ingredients.add(ingredients[0]);
                        }
                    }
                }).execute(ingredient);
            }
        }

        /**
         * Check whether the ingredient has already been saved in the database or not
         *
         * @param name the name of the ingredient
         * @return true if already present
         */
        private boolean ingredientInDatabase(String name) {
            for (Ingredient ingredient :
                    ingredients) {
                if (ingredient.getName().equals(name)) {
                    return true;
                }
            }
            return false;
        }
    }
}
