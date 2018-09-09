package np.com.aanalbasaula.foodplanner.database.utils;

import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;

import java.util.LinkedList;
import java.util.List;

import np.com.aanalbasaula.foodplanner.database.Ingredient;
import np.com.aanalbasaula.foodplanner.database.IngredientDao;

public class LoadIngredientsAsync extends AsyncTask<Void, Void, List<Ingredient>> {
    private static final String TAG = LoadRecipesAsync.class.getSimpleName();

    // The dao for the ingredient
    @NonNull
    private IngredientDao dao;

    // The listener for the result
    @NonNull
    private LoadIngredientsListener listener;

    public LoadIngredientsAsync(@NonNull IngredientDao dao, @NonNull LoadIngredientsListener listener) {
        this.dao = dao;
        this.listener = listener;
    }

    @Override
    protected List<Ingredient> doInBackground(Void... voids) {
        List<Ingredient> recipes = new LinkedList<>();
        try {
            recipes = this.dao.getAllIngredients();
        } catch (Exception e) {
            Log.e(TAG, "doInBackground: Could not load the recipes", e);
        }
        return recipes;
    }

    @Override
    protected void onPostExecute(List<Ingredient> ingredients) {
        Log.i(TAG, "onPostExecute: Recipes have been loaded, notifying the listener");
        this.listener.onIngredientsLoaded(ingredients);
    }

    /**
     * The interface which allows users of this utility class to access the result
     */
    public interface LoadIngredientsListener {
        /**
         * Called once the Ingredients have been loaded from the database
         *
         * @param ingredients the list of ingredients
         */
        void onIngredientsLoaded(@NonNull List<Ingredient> ingredients);
    }
}
