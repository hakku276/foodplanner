package np.com.aanalbasaula.foodplanner.database.utils;

import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;

import java.util.LinkedList;
import java.util.List;

import np.com.aanalbasaula.foodplanner.database.Recipe;
import np.com.aanalbasaula.foodplanner.database.RecipeDao;

/**
 * An async utility class which loads all the recipes present in the database.
 */
public class LoadRecipesAsync extends AsyncTask<Void, Void, List<Recipe>> {
    private static final String TAG = LoadRecipesAsync.class.getSimpleName();

    // The dao for the recipe
    @NonNull
    private RecipeDao dao;

    // The listener for the result
    @NonNull
    private LoadRecipesListener listener;

    public LoadRecipesAsync(@NonNull RecipeDao dao, @NonNull LoadRecipesListener listener) {
        this.dao = dao;
        this.listener = listener;
    }

    @Override
    protected List<Recipe> doInBackground(Void... voids) {
        List<Recipe> recipes = new LinkedList<>();
        try {
            recipes = this.dao.getAllRecipes();
        } catch (Exception e) {
            Log.e(TAG, "doInBackground: Could not load the recipes", e);
        }
        return recipes;
    }

    @Override
    protected void onPostExecute(List<Recipe> recipes) {
        Log.i(TAG, "onPostExecute: Recipes have been loaded, notifying the listener");
        this.listener.onRecipeLoaded(recipes);
    }

    /**
     * The interface which allows users of this utility class to access the result
     */
    public interface LoadRecipesListener {
        /**
         * Called once the Recipe has been loaded from the database
         *
         * @param recipes the list of recipes
         */
        void onRecipeLoaded(@NonNull List<Recipe> recipes);
    }
}
