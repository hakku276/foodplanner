package np.com.aanalbasaula.foodplanner.views.cookbook;

import android.os.Bundle;
import android.support.annotation.NonNull;
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
import np.com.aanalbasaula.foodplanner.database.utils.EntryCreationStrategies;
import np.com.aanalbasaula.foodplanner.database.utils.EntryCreator;

public class RecipeCreatorActivity extends AppCompatActivity implements AddIngredientFragment.OnFragmentInteractionListener {

    private static final String TAG = RecipeCreatorActivity.class.getSimpleName();

    private EditText textRecipeName;
    private AddIngredientFragment fragmentAddIngredient;

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
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.i(TAG, "onCreateOptionsMenu: Inflating layout menu");
        getMenuInflater().inflate(R.menu.activity_recipe_creator, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // check the action if is required to be handled
        if (item.getItemId() == R.id.action_save_recipe) {
            Log.i(TAG, "onOptionsItemSelected: User Requested save recipe");
            saveRecipe();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onIngredientsChanged(List<Ingredient> ingredients) {
        Log.i(TAG, "onIngredientsChanged: The ingredients have changed");
        this.ingredients = ingredients;
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
     * The Recipe entry creation listener
     */
    private EntryCreator.EntryCreationListener<Recipe> recipeCreationListener = new EntryCreator.EntryCreationListener<Recipe>() {
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
                // TODO end the recipe creation here
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

    private EntryCreator.EntryCreationListener<Ingredient> ingredientCreationListener = new EntryCreator.EntryCreationListener<Ingredient>() {
        @Override
        public void onEntriesCreated(Ingredient[] items) {
            Log.i(TAG, "onEntriesCreated: Ingredients have been created");
            if (items != null) {
                Log.i(TAG, "onEntriesCreated: Newly created ingredients: " + items.length);
                finish();
            }
        }
    };

}
