package np.com.aanalbasaula.foodplanner.views.cookbook;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.TextView;

import np.com.aanalbasaula.foodplanner.R;
import np.com.aanalbasaula.foodplanner.database.Recipe;

public class RecipeViewerActivity extends AppCompatActivity {

    public static final String EXTRA_RECIPE = "recipe";
    private static final String TAG = RecipeViewerActivity.class.getSimpleName();

    @Nullable
    private Recipe recipe; // the recipe being displayed on screen

    // UI related properties
    private TextView textRecipeName;
    private Toolbar toolbar;
    private ShowIngredientFragment showIngredientFragment;

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

    }

    @Override
    protected void onStart() {
        super.onStart();

        if (recipe != null) {
            Log.i(TAG, "onStart: The Recipe is available. Setting up UI");
            textRecipeName.setText(recipe.getName());
            setupIngredientsView();
        } else {
            Log.w(TAG, "onStart: Could not find displayable Recipe");
        }
    }

    /**
     * Sets up the ingredients view using the {@link ShowIngredientFragment} fragment.
     * The fragment is created if only necessary.
     */
    private void setupIngredientsView() {
        if (showIngredientFragment == null) {
            showIngredientFragment = ShowIngredientFragment.newInstance(recipe);
        }

        getSupportFragmentManager().beginTransaction().replace(R.id.content, showIngredientFragment).commit();
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

}
