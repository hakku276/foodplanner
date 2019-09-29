package np.com.aanalbasaula.foodplanner.views.cookbook;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import java.util.List;

import np.com.aanalbasaula.foodplanner.R;
import np.com.aanalbasaula.foodplanner.database.AppDatabase;
import np.com.aanalbasaula.foodplanner.database.Ingredient;
import np.com.aanalbasaula.foodplanner.database.IngredientDao;
import np.com.aanalbasaula.foodplanner.database.Recipe;
import np.com.aanalbasaula.foodplanner.database.RecipeDao;
import np.com.aanalbasaula.foodplanner.database.utils.DatabaseLoader;
import np.com.aanalbasaula.foodplanner.database.utils.EntryCreationStrategies;
import np.com.aanalbasaula.foodplanner.database.utils.EntryCreator;
import np.com.aanalbasaula.foodplanner.database.utils.RecipeUpdater;
import np.com.aanalbasaula.foodplanner.utils.BroadcastUtils;

/**
 * An activity that is responsible for creating a Recipe as well as for updating it.
 */
public class RecipeCreatorActivity extends AppCompatActivity implements AddIngredientFragment.OnFragmentInteractionListener {

    private static final String TAG = RecipeCreatorActivity.class.getSimpleName();
    public static final String EXTRA_EDIT_RECIPE = "recipe";

    // ui related
    private EditText textRecipeName;
    private AddIngredientFragment fragmentAddIngredient;

    // working properties
    private boolean isEditMode;
    private Recipe recipe; // the recipe being currently edited using this view

    // database related
    private RecipeDao recipeDao;
    private IngredientDao ingredientDao;
    @Nullable
    private List<Ingredient> ingredients;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_creator);
        Log.i(TAG, "onCreate: Setting up UI");

        // setup the toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // enable back button on the toolbar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // gather required views
        textRecipeName = findViewById(R.id.text_recipe_name);

        // add the ingredients fragment to view
        fragmentAddIngredient = AddIngredientFragment.newInstance();
        getSupportFragmentManager().beginTransaction()
                .add(R.id.content, fragmentAddIngredient, "fragment-add-ingredient")
                .commit();

        recipeDao = AppDatabase.getInstance(this).getRecipeDao();
        ingredientDao = AppDatabase.getInstance(this).getIngredientDao();

        // Extract Recipe from intent before preparing the view, since either edit mode or not
        // is dependent on configuration sent within the Intent
        extractRecipeFromIntent();

        // prepare the view for either edit or create mode
        prepareView();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.i(TAG, "onCreateOptionsMenu: Inflating layout menu");
        if (!isEditMode) {
            Log.i(TAG, "onCreateOptionsMenu: Creating Create Mode Menu");
            getMenuInflater().inflate(R.menu.activity_recipe_creator, menu);
        } else {
            Log.i(TAG, "onCreateOptionsMenu: Creating Edit Mode Menu");
            getMenuInflater().inflate(R.menu.activity_recipe_updator, menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.i(TAG, "onOptionsItemSelected: Option menu selected");
        // check the action if is required to be handled
        // NOTE: when the options menu is created, it is created different for edit mode
        // NOTE: return true after every handle to avoid parent view to get the save request
        if (item.getItemId() == R.id.action_recipe_save) {
            Log.i(TAG, "onOptionsItemSelected: User Requested save recipe");
            saveRecipe();
            return true;
        } else if (item.getItemId() == R.id.action_recipe_edit) {
            Log.i(TAG, "onOptionsItemSelected: User requested edit recipe");
            updateRecipe();
            return true;
        } else if (item.getItemId() == android.R.id.home) {
            Log.i(TAG, "onOptionsItemSelected: User pressed back button");
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onIngredientsChanged(List<Ingredient> ingredients) {
        Log.i(TAG, "onIngredientsChanged: The ingredients have changed");
        this.ingredients = ingredients;
    }

    /**
     * Extracts the recipe provided within the intent while starting the activity. Note:
     * If the intent has the recipe, it is to be considered that the view has been opened for edit
     * of this specific recipe.
     */
    private void extractRecipeFromIntent() {
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra(EXTRA_EDIT_RECIPE)) {
            Log.i(TAG, "extractRecipeFromIntent: Intent available for recipe extraction");
            recipe = intent.getParcelableExtra(EXTRA_EDIT_RECIPE);
            isEditMode = true;
        }
    }

    /**
     * Prepares the view for either Edit or Create.
     */
    private void prepareView() {
        if (isEditMode) {
            Log.i(TAG, "prepareView: View was opened in edit mode. Populating view for Edit.");
            textRecipeName.setText(recipe.getName());

            Log.d(TAG, "prepareView: Loading ingredients");
            // begin load of ingredients
            DatabaseLoader<IngredientDao, Ingredient> loader = new DatabaseLoader<>(ingredientDao, (d) -> d.getIngredientsForRecipe(recipe.getId()), ingredientsLoadListener);
            loader.execute();
        }
    }

    /**
     * Check whether the input is valid or not. In case the input is not valid,
     * displays proper error messages.
     *
     * @return true if valid, else false
     */
    private boolean isRecipeValid() {
        String recipeName = textRecipeName.getText().toString();

        if (recipeName.isEmpty()) {
            textRecipeName.setError(getString(R.string.error_required));
            return false;
        }

        return true;
    }

    /**
     * Saves the currently edited recipe into the database
     */
    private void saveRecipe() {
        Log.i(TAG, "saveRecipe: Validating recipe before saving into database");
        if (!isRecipeValid()) {
            Log.i(TAG, "saveRecipe: User provided input is not valid.");
            return;
        }

        Log.i(TAG, "saveRecipe: Input is valid. Creating recipe in database");
        Recipe recipe = new Recipe();
        recipe.setName(textRecipeName.getText().toString());

        EntryCreator<RecipeDao, Recipe> entryCreator = new EntryCreator<>(recipeDao, EntryCreationStrategies.recipeCreationStrategy, recipeCreationListener);
        entryCreator.execute(recipe);
    }

    /**
     * Updates the currently edited recipe into the database
     */
    private void updateRecipe() {
        Log.i(TAG, "updateRecipe: Updating Recipe: " + recipe.getName());

        // collect the list of ingredients
        recipe.setIngredients(ingredients);

        RecipeUpdater updater = new RecipeUpdater(recipeDao, ingredientDao, recipeUpdateListener);
        updater.execute(recipe);
    }

    /**
     * The Recipe entry creation listener
     */
    private final EntryCreator.EntryCreationListener<Recipe> recipeCreationListener = new EntryCreator.EntryCreationListener<Recipe>() {
        @Override
        public void onEntriesCreated(Recipe[] items) {
            Log.i(TAG, "onEntriesCreated: Recipe has been successfully saved into database");
            if (items == null || items.length < 1){
                Log.e(TAG, "onEntriesCreated: Could not save recipe into database");
                return;
            }

            Recipe recipe = items[0];

            Log.i(TAG, "onEntriesCreated: Saving ingredients into database");
            if (ingredients == null || ingredients.isEmpty()) {
                Log.i(TAG, "onEntriesCreated: No ingredients to save into database");
                // end the recipe creation here
                finish();
                return;
            }

            // set the recipe id
            for (Ingredient ingredient :
                    ingredients) {
                ingredient.setRecipeId(recipe.getId());
            }

            // save the ingredients now
            EntryCreator<IngredientDao, Ingredient> entryCreator = new EntryCreator<>(ingredientDao, EntryCreationStrategies.ingredientEntryCreationStrategy, ingredientCreationListener);
            entryCreator.execute(ingredients.toArray(new Ingredient[0]));
        }
    };

    private final RecipeUpdater.RecipeUpdateStatusListener recipeUpdateListener = new RecipeUpdater.RecipeUpdateStatusListener() {
        @Override
        public void onRecipeUpdated(Recipe[] recipes) {
            Log.i(TAG, "onRecipeUpdated: The recipe has been updated");
            Intent data = new Intent();
            data.putExtra(EXTRA_EDIT_RECIPE, recipe);
            setResult(RESULT_OK, data);
            finish();
        }
    };

    /**
     * A listener to wait for ingredients to be written to the database. The listener is responsible for
     * closing the view once the writing is done. since the view is no longer needed after this moment.
     */
    private final EntryCreator.EntryCreationListener<Ingredient> ingredientCreationListener = new EntryCreator.EntryCreationListener<Ingredient>() {
        @Override
        public void onEntriesCreated(Ingredient[] items) {
            Log.i(TAG, "onEntriesCreated: Ingredients have been created");
            if (items != null) {
                Log.i(TAG, "onEntriesCreated: Newly created ingredients: " + items.length);
                finish();
            }
        }
    };

    /**
     * A listener to listen for ingredient loads from the database.
     * NOTE: the ingredients are only loaded when the view is in EDIT mode
     */
    private final DatabaseLoader.DatabaseLoadListener<Ingredient> ingredientsLoadListener = ingredients -> {
        Log.i(TAG, "IngredientsLoadListener: The ingredients have been successfully loaded from the database: " + ingredients.size());
        this.ingredients = ingredients;
        this.fragmentAddIngredient.setIngredients(ingredients);
    };

}
