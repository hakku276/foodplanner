package np.com.aanalbasaula.foodplanner.database.utils;

import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;

import np.com.aanalbasaula.foodplanner.database.Recipe;
import np.com.aanalbasaula.foodplanner.database.RecipeDao;

public class CreateRecipeEntryAsync extends AsyncTask<Recipe, Void, Recipe[]> {

    private static final String TAG = CreateRecipeEntryAsync.class.getSimpleName();

    @NonNull
    private RecipeDao dao;

    @NonNull
    private CreateRecipeEntryListener listener;

    public CreateRecipeEntryAsync(@NonNull RecipeDao dao, @NonNull CreateRecipeEntryListener listener){
        this.dao = dao;
        this.listener = listener;
    }

    @Override
    protected Recipe[] doInBackground(Recipe... recipes) {
        Log.i(TAG, "doInBackground: Creating entries: " + recipes.length);
        try {
            long[] ids = dao.insert(recipes);
            Log.i(TAG, "doInBackground: Created entries count :" + ids.length);
            for (int i = 0; i < ids.length; i++) {
                Log.i(TAG, "doInBackground: New Ingredient ID: " + ids[0]);
                recipes[i].setId(ids[i]);
            }
        }catch (Exception e){
            Log.e(TAG, "doInBackground: Could not create recipe entries", e);
        }
        return recipes;
    }

    @Override
    protected void onPostExecute(Recipe[] recipes) {
        listener.onRecipeEntriesCreated(recipes);
    }

    public interface CreateRecipeEntryListener{

        /**
         * Called when the recipe entries have been created
         */
        void onRecipeEntriesCreated(Recipe[] recipes);
    }
}
