package np.com.aanalbasaula.foodplanner.database.utils;

import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;

import np.com.aanalbasaula.foodplanner.database.Ingredient;
import np.com.aanalbasaula.foodplanner.database.IngredientDao;

public class CreateIngredientEntryAsync extends AsyncTask<Ingredient, Void, Ingredient[]>{
    private static final String TAG = CreateRecipeEntryAsync.class.getSimpleName();

    @NonNull
    private IngredientDao dao;

    @NonNull
    private CreateIngredientEntryListener listener;

    public CreateIngredientEntryAsync(@NonNull IngredientDao dao, @NonNull CreateIngredientEntryListener listener){
        this.dao = dao;
        this.listener = listener;
    }

    @Override
    protected Ingredient[] doInBackground(Ingredient... ingredients) {
        Log.i(TAG, "doInBackground: Creating entries: " + ingredients.length);
        try {
            dao.insert(ingredients);
        }catch (Exception e){
            Log.e(TAG, "doInBackground: Could not create recipe entries", e);
        }
        return ingredients;
    }

    @Override
    protected void onPostExecute(Ingredient[] ingredients) {
        listener.onIngredientEntriesCreated(ingredients);
    }

    public interface CreateIngredientEntryListener{

        /**
         * Called when the Ingredient entries have been created
         */
        void onIngredientEntriesCreated(Ingredient[] ingredients);
    }
}
