package np.com.aanalbasaula.foodplanner.database.utils;

import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;

import np.com.aanalbasaula.foodplanner.database.AppDatabase;
import np.com.aanalbasaula.foodplanner.database.Ingredient;
import np.com.aanalbasaula.foodplanner.database.IngredientDao;
import np.com.aanalbasaula.foodplanner.database.Recipe;
import np.com.aanalbasaula.foodplanner.database.RecipeDao;
import np.com.aanalbasaula.foodplanner.database.RecipeIngredient;
import np.com.aanalbasaula.foodplanner.database.RecipeIngredientDao;

public class CreateRecipeEntryAsync extends AsyncTask<Recipe, Void, Recipe[]> {

    private static final String TAG = CreateRecipeEntryAsync.class.getSimpleName();

    @NonNull
    private RecipeDao recipeDao;

    @NonNull
    private RecipeIngredientDao recipeIngredientDao;

    @NonNull
    private CreateRecipeEntryListener listener;

    public CreateRecipeEntryAsync(@NonNull AppDatabase db, @NonNull CreateRecipeEntryListener listener){
        this.recipeDao = db.getRecipeDao();
        this.recipeIngredientDao = db.getRecipeIngredientDao();
        this.listener = listener;
    }

    @Override
    protected Recipe[] doInBackground(Recipe... recipes) {
        Log.i(TAG, "doInBackground: Creating entries: " + recipes.length);
        try {
            //create the recipe entries on the recipes table
            long[] ids = recipeDao.insert(recipes);
            Log.i(TAG, "doInBackground: Created entries count :" + ids.length);

            //now link the recipe to the ingredients
            for (int i = 0; i < ids.length; i++) {
                Log.i(TAG, "doInBackground: New Recipe ID: " + ids[i]);
                recipes[i].setId(ids[i]);
                Log.i(TAG, "doInBackground: Linking recipe with ingredients: " + recipes[i].getIngredients().size());
                for (Ingredient ingredient :
                        recipes[i].getIngredients()) {
                    RecipeIngredient repIng = new RecipeIngredient();
                    repIng.setIngredientId(ingredient.getId());
                    repIng.setRecipeId(recipes[i].getId());
                    Log.d(TAG, "doInBackground: Recipe: " + recipes[i] + " Ingredient: " + ingredient.getId());
                    // TODO: maybe chunking them would be better
                    recipeIngredientDao.insert(repIng);
                }
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
