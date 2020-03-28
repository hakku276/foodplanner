package np.com.aanalbasaula.foodplanner.views.cookbook;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import np.com.aanalbasaula.foodplanner.R;
import np.com.aanalbasaula.foodplanner.database.AppDatabase;
import np.com.aanalbasaula.foodplanner.database.Ingredient;
import np.com.aanalbasaula.foodplanner.database.IngredientDao;
import np.com.aanalbasaula.foodplanner.database.Recipe;
import np.com.aanalbasaula.foodplanner.database.RecipeStep;
import np.com.aanalbasaula.foodplanner.database.RecipeStepDao;
import np.com.aanalbasaula.foodplanner.database.utils.DatabaseLoader;

public class RecipeViewerActivity extends AppCompatActivity {

    public static final String EXTRA_RECIPE = "recipe";
    private static final int REQUEST_EDIT_RECIPE = 1;
    private static final String TAG = RecipeViewerActivity.class.getSimpleName();

    @Nullable
    private Recipe recipe; // the recipe being displayed on screen

    // UI related properties
    private TextView textRecipeName;
    private Toolbar toolbar;
    private RecipeFragment recipeFragment;
    private TextView textPreparationTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_viewer);

        // setup the toolbar
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // enable back button on the toolbar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        extractDisplayRecipe();

        // gather UI elements
        textRecipeName = findViewById(R.id.text_recipe_name);
        textPreparationTime = findViewById(R.id.text_cooking_time);
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (recipeFragment == null) {
            recipeFragment = RecipeFragment.newInstance(false);
        }
        getSupportFragmentManager().beginTransaction().replace(R.id.content, recipeFragment).commit();

        if (recipe != null) {
            Log.i(TAG, "onStart: The Recipe is available. Loading Content");
            prepareView();
        } else {
            Log.w(TAG, "onStart: Could not find displayable Recipe");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.d(TAG, "onCreateOptionsMenu: Creating Options menu");
        getMenuInflater().inflate(R.menu.activity_recipe_viewer, menu);

        return true; // to display the menu
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.action_recipe_edit && recipe != null) {
            Log.i(TAG, "onOptionsItemSelected: Editing Recipe: " + recipe.getName());
            showEditRecipeView();
            return true; // consume menu processing here
        }

        Log.d(TAG, "onOptionsItemSelected: Not processing options menu item. Either unknown menu item or null recipe");
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.i(TAG, "onActivityResult: The Opened Activity has returned a result");

        if (requestCode == REQUEST_EDIT_RECIPE && resultCode == RESULT_OK) {
            Log.d(TAG, "onActivityResult: Recipe has been edited by the user. Extracting new Recipe Information");

            if (data == null) {
                Log.e(TAG, "onActivityResult: The Recipe Activity finished without setting data.");
                finish();
            } else {
                Log.d(TAG, "onActivityResult: Recipe information was available in extras");
                recipe = data.getParcelableExtra(RecipeCreatorActivity.EXTRA_EDIT_RECIPE);
            }
        }
    }

    /**
     * Sets up the complete activity view to correctly display the {@linkplain Recipe} as required.
     * The ingredients are displayed using the {@link RecipeFragment} fragment (The fragment
     * is created if only necessary.)
     */
    private void prepareView() {
        Log.i(TAG, "prepareView: Setting up UI to display the recipe: " + recipe.getName());
        textRecipeName.setText(recipe.getName());
        textPreparationTime.setText(recipe.getPreparationTime() + " mins");

        Log.d(TAG, "prepareView: Loading Recipe Information");
        IngredientDao ingredientDao = AppDatabase.getInstance(this).getIngredientDao();
        DatabaseLoader<IngredientDao, Ingredient> ingredientsLoader = new DatabaseLoader<>(ingredientDao,
                d -> d.getIngredientsForRecipe(recipe.getId()),
                items -> recipeFragment.setIngredients(items));
        ingredientsLoader.execute();

        RecipeStepDao recipeStepDao = AppDatabase.getInstance(this).getRecipeStepDao();
        DatabaseLoader<RecipeStepDao, RecipeStep> stepsLoader = new DatabaseLoader<>(recipeStepDao,
                d -> d.getRecipeSteps(recipe.getId()),
                items -> recipeFragment.setSteps(items));
        stepsLoader.execute();

    }

    /**
     * Extracts the recipe to be displayed on screen. The extracted recipe is set in the
     * class property {@linkplain #recipe}. The property could be NULL after extraction
     * therefore should be checked before use.
     */
    private void extractDisplayRecipe() {
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra(EXTRA_RECIPE)) {
            Log.i(TAG, "onCreate: Found Parcelled Recipe in Intent");
            recipe = intent.getParcelableExtra(EXTRA_RECIPE);
            Log.i(TAG, "onCreate: Showing Recipe: " + recipe);
        }
    }

    /**
     * Displays the Edit Recipe View. For now, a reused version of the {@link RecipeCreatorActivity}
     * will be used. The Recipe Creator Activity will morph to accommodate the edit functionality.
     */
    private void showEditRecipeView() {
        Log.i(TAG, "showEditRecipeView: Starting recipe edit view");
        RecipeCreatorActivity.showActivity(this, recipe, REQUEST_EDIT_RECIPE);
    }

}
