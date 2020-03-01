package np.com.aanalbasaula.foodplanner.views.cookbook;

import android.app.Activity;
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
import np.com.aanalbasaula.foodplanner.database.RecipeStep;
import np.com.aanalbasaula.foodplanner.database.RecipeStepDao;
import np.com.aanalbasaula.foodplanner.database.utils.DatabaseLoader;
import np.com.aanalbasaula.foodplanner.database.utils.RecipeCreator;
import np.com.aanalbasaula.foodplanner.database.utils.RecipeUpdater;

/**
 * An activity that is responsible for creating a Recipe as well as for updating it.
 */
public class RecipeCreatorActivity extends AppCompatActivity {

    private static final String TAG = RecipeCreatorActivity.class.getSimpleName();
    public static final String EXTRA_EDIT_RECIPE = "recipe";

    // ui related
    private EditText textRecipeName;
    private RecipeFragment fragmentEditRecipe;

    // working properties
    private boolean isEditMode;
    private Recipe recipe; // the recipe being currently edited using this view

    // database related
    private RecipeDao recipeDao;
    private IngredientDao ingredientDao;
    private RecipeStepDao recipeStepDao;
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
        fragmentEditRecipe = RecipeFragment.newInstance(true);
        getSupportFragmentManager().beginTransaction()
                .add(R.id.content, fragmentEditRecipe, "fragment-edit-recipe")
                .commit();

        recipeDao = AppDatabase.getInstance(this).getRecipeDao();
        ingredientDao = AppDatabase.getInstance(this).getIngredientDao();
        recipeStepDao = AppDatabase.getInstance(this).getRecipeStepDao();

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
            DatabaseLoader<IngredientDao, Ingredient> ingredientLoader = new DatabaseLoader<>(ingredientDao,
                    d -> d.getIngredientsForRecipe(recipe.getId()),
                    items -> fragmentEditRecipe.setIngredients(items));
            ingredientLoader.execute();

            // begin load of ingredients
            DatabaseLoader<RecipeStepDao, RecipeStep> recipeLoader = new DatabaseLoader<>(recipeStepDao,
                    d -> d.getRecipeSteps(recipe.getId()),
                    items -> fragmentEditRecipe.setSteps(items));
            recipeLoader.execute();
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
        recipe.setIngredients(fragmentEditRecipe.getIngredients());
        recipe.setRecipeSteps(fragmentEditRecipe.getSteps());

        Log.d(TAG, "saveRecipe: Recipe has ingredients: " + recipe.getIngredients().size());
        Log.d(TAG, "saveRecipe: Recipe has steps: " + recipe.getIngredients().size());

        RecipeCreator recipeCreator = new RecipeCreator(recipeDao, ingredientDao, recipeStepDao, recipeCreationListener);
        recipeCreator.execute(recipe);
    }

    /**
     * Updates the currently edited recipe into the database
     */
    private void updateRecipe() {
        Log.i(TAG, "updateRecipe: Updating Recipe: " + recipe.getName());

        if (!isRecipeValid()) {
            Log.i(TAG, "updateRecipe: User provided input is not valid.");
            return;
        }

        // collect the list of ingredients
        recipe.setName(textRecipeName.getText().toString());
        recipe.setIngredients(fragmentEditRecipe.getIngredients());
        recipe.setRecipeSteps(fragmentEditRecipe.getSteps());

        RecipeUpdater updater = new RecipeUpdater(recipeDao, ingredientDao, recipeStepDao, recipeUpdateListener);
        updater.execute(recipe);
    }

    private final RecipeCreator.RecipeCreationStatusListener recipeCreationListener = new RecipeCreator.RecipeCreationStatusListener() {
        @Override
        public void onRecipeCreated(Recipe[] recipes) {
            Log.i(TAG, "onRecipeCreated: Recipe has been successfully saved into database");
            if (recipes == null || recipes.length < 1){
                Log.e(TAG, "onRecipeCreated: Could not save recipe into database");
                return;
            }

            finish();
        }
    };

    private final RecipeUpdater.RecipeUpdateStatusListener recipeUpdateListener = new RecipeUpdater.RecipeUpdateStatusListener() {
        @Override
        public void onRecipeUpdated(Recipe[] recipes) {
            Log.i(TAG, "onRecipeCreated: The recipe has been updated");
            Intent data = new Intent();
            data.putExtra(EXTRA_EDIT_RECIPE, recipe);
            setResult(RESULT_OK, data);
            finish();
        }
    };

    /**
     * Show this activity using the provided configuration. If the recipe is provided, the activity
     * opens in edit mode.
     *
     * @param activity  the activity that wants to start this activity with result
     * @param recipe    the recipe to edit if available
     * @param requestId the request id to be used by the activity to monitor result
     */
    public static void showActivity(Activity activity, Recipe recipe, int requestId) {
        Log.i(TAG, "showActivity: Starting recipe edit view");
        Intent intent = new Intent(activity, RecipeCreatorActivity.class);

        // if the recipe is provided, should be opened in edit mode
        if (recipe != null) {
            intent.putExtra(RecipeCreatorActivity.EXTRA_EDIT_RECIPE, recipe);
        }

        activity.startActivityForResult(intent, requestId);
    }

}
