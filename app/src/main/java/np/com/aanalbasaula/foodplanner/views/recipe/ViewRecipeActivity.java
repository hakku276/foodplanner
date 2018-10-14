package np.com.aanalbasaula.foodplanner.views.recipe;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import np.com.aanalbasaula.foodplanner.R;
import np.com.aanalbasaula.foodplanner.database.AppDatabase;
import np.com.aanalbasaula.foodplanner.database.Recipe;
import np.com.aanalbasaula.foodplanner.database.utils.LoadRecipeIngredientsAsync;

public class ViewRecipeActivity extends AppCompatActivity implements LoadRecipeIngredientsAsync.OnCompleteListener{

    private static final String TAG = ViewRecipeActivity.class.getSimpleName();
    public static final String EXTRA_RECIPE = "recipe";
    private Recipe recipe;
    private AppDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_recipe);
        Intent intent  = getIntent();
        if (intent != null && intent.hasExtra(EXTRA_RECIPE)){
            this.recipe = intent.getParcelableExtra(EXTRA_RECIPE);
        }
        db = AppDatabase.getInstance(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        //add the show recipe fragment if not already present
        FragmentManager fm = getSupportFragmentManager();
        if (fm.getFragments().size() == 0) {
            if (recipe != null && recipe.getIngredients() == null) {
                Log.i(TAG, "onStart: No Fragment available and no recipe ingredients, showing loading fragment");
                fm.beginTransaction().add(R.id.content, LoadScreenFragment.newInstance()).commit();
                //start the ingredients load as well
                new LoadRecipeIngredientsAsync(db.getIngredientDao(), db.getRecipeIngredientDao(), this).execute(recipe);
            } else {
                Log.i(TAG, "onStart: The Recipe has already been loaded, simple show the recipe");
                fm.beginTransaction().add(R.id.content, ShowRecipeFragment.newInstance(recipe)).commit();
            }
        }
    }

    @Override
    public void onComplete(@NonNull Recipe[] recipes) {
        FragmentManager fm = getSupportFragmentManager();
        if(recipe.getIngredients() != null) {
            Log.i(TAG, "onComplete: The ingredients have been correctly loaded");
            fm.beginTransaction().replace(R.id.content, ShowRecipeFragment.newInstance(recipe)).commit();
        } else {
            Log.i(TAG, "onComplete: Could not load the ingredients for the recipe");
            Toast.makeText(this, "Could not load the recipe", Toast.LENGTH_SHORT).show();
        }
    }
}
