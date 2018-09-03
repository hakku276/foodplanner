package np.com.aanalbasaula.foodplanner.views.recipe;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import np.com.aanalbasaula.foodplanner.R;
import np.com.aanalbasaula.foodplanner.database.AppDatabase;
import np.com.aanalbasaula.foodplanner.database.Recipe;
import np.com.aanalbasaula.foodplanner.database.utils.CreateRecipeEntryAsync;

public class NewRecipeActivity extends AppCompatActivity implements CreateRecipeEntryAsync.CreateRecipeEntryListener {

    private static final String TAG = NewRecipeActivity.class.getSimpleName();
    /**
     * Activity Result Code denoting that a new recipe was added. When this result is returned to
     * the calling activity {@link NewRecipeActivity#EXTRA_RECIPE} is set with the added recipe
     */
    public static final int RESULT_RECIPE_ADDED = 100;

    /**
     * The optional result field which has just been added
     */
    public static final String EXTRA_RECIPE = "recipe";

    // the edit text with the recipe name
    private EditText editRecipeName;

    // the database instance
    private AppDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_recipe);

        editRecipeName = findViewById(R.id.edit_recipe_name);
        db = AppDatabase.getInstance(this);
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
}
