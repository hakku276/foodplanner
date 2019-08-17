package np.com.aanalbasaula.foodplanner.views.cookbook;

import android.net.Uri;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.widget.EditText;
import android.widget.FrameLayout;

import java.util.List;

import np.com.aanalbasaula.foodplanner.R;
import np.com.aanalbasaula.foodplanner.database.Ingredient;

public class RecipeCreatorActivity extends AppCompatActivity implements AddIngredientFragment.OnFragmentInteractionListener {

    private static final String TAG = RecipeCreatorActivity.class.getSimpleName();

    private EditText textRecipeName;
    private AddIngredientFragment fragmentAddIngredient;

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
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.i(TAG, "onCreateOptionsMenu: Inflating layout menu");
        getMenuInflater().inflate(R.menu.activity_recipe_creator, menu);
        return true;
    }

    @Override
    public void onIngredientsChanged(List<Ingredient> ingredients) {
        Log.i(TAG, "onIngredientsChanged: The ingredients have changed");
    }
}
