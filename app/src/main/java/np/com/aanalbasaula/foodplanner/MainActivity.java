package np.com.aanalbasaula.foodplanner;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcel;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import np.com.aanalbasaula.foodplanner.database.AppDatabase;
import np.com.aanalbasaula.foodplanner.database.Recipe;
import np.com.aanalbasaula.foodplanner.database.utils.LoadRecipesAsync;
import np.com.aanalbasaula.foodplanner.views.recipe.NewRecipeActivity;
import np.com.aanalbasaula.foodplanner.views.recipe.LoadScreenFragment;
import np.com.aanalbasaula.foodplanner.views.recipe.ShowRecipeFragment;

public class MainActivity extends AppCompatActivity implements ShowRecipeFragment.OnListFragmentInteractionListener, LoadRecipesAsync.LoadRecipesListener {
    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int REQUEST_NEW_RECIPE = 100;

    // The fragment which shows the list of recipes available
    private ShowRecipeFragment showRecipeFragment;

    // The fragment which shows that the device is currently busy and will show a fragment there sometime soon
    private LoadScreenFragment loadScreenFragment;

    // The application database to be used
    private AppDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.i(TAG, "onCreate: View being created");
        loadScreenFragment = LoadScreenFragment.newInstance();
        //instantiate db anyway
        db = AppDatabase.getInstance(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i(TAG, "onStart: View Starting up");
        FragmentManager fm = getSupportFragmentManager();
        //check if this is the first time the view has been loaded and there is nothing on the screen
        if (fm.getFragments().size() == 0) {
            //put the loading screen
            fm.beginTransaction().add(R.id.fragment_container, loadScreenFragment).commit();
            // and start with the database loading
            LoadRecipesAsync asyncTask = new LoadRecipesAsync(this.db.getRecipeDao(), this);
            asyncTask.execute();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_new_recipe:
                Log.i(TAG, "onOptionsItemSelected: New recipe option menu clicked");
                startNewRecipeCreation();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    private void startNewRecipeCreation(){
        Intent intent = new Intent(this, NewRecipeActivity.class);
        startActivityForResult(intent, REQUEST_NEW_RECIPE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_NEW_RECIPE && resultCode == NewRecipeActivity.RESULT_RECIPE_ADDED){
            Log.i(TAG, "onActivityResult: Recipe was added");
            Recipe recipe = data.getParcelableExtra(NewRecipeActivity.EXTRA_RECIPE);
            showRecipeFragment.onNewRecipeAdded(recipe);
        } else {
            Log.i(TAG, "onActivityResult: Request code: " + requestCode + " result code: " + resultCode);
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onListFragmentInteraction(Recipe item) {
        Log.i(TAG, "onListFragmentInteraction: User interacted with item: " + item.toString());
    }

    @Override
    public void onRecipeLoaded(@NonNull List<Recipe> recipes) {
        Log.i(TAG, "onRecipeLoaded: The recipes have been loaded: Total Count = " + recipes.size());
        showRecipeFragment = ShowRecipeFragment.newInstance(recipes);
        FragmentManager fm = getSupportFragmentManager();
        fm.beginTransaction().replace(R.id.fragment_container, showRecipeFragment).commit();
    }
}
