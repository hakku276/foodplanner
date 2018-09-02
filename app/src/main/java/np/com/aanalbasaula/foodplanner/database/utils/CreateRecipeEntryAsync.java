package np.com.aanalbasaula.foodplanner.database.utils;

import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;

import np.com.aanalbasaula.foodplanner.database.Recipe;
import np.com.aanalbasaula.foodplanner.database.RecipeDao;

public class CreateRecipeEntryAsync extends AsyncTask<Recipe, Void, Void> {

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
    protected Void doInBackground(Recipe... recipes) {
        Log.i(TAG, "doInBackground: Creating entries: " + recipes.length);
        try {
            dao.insert(recipes);
        }catch (Exception e){
            Log.e(TAG, "doInBackground: Could not create recipe entries", e);
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        listener.onRecipeEntriesCreated();
    }

    public interface CreateRecipeEntryListener{

        /**
         * Called when the recipe entries have been created
         */
        void onRecipeEntriesCreated();
    }
}
