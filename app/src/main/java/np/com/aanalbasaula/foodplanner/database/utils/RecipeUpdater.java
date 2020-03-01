package np.com.aanalbasaula.foodplanner.database.utils;

import android.os.AsyncTask;
import android.util.Log;

import np.com.aanalbasaula.foodplanner.database.Ingredient;
import np.com.aanalbasaula.foodplanner.database.IngredientDao;
import np.com.aanalbasaula.foodplanner.database.Recipe;
import np.com.aanalbasaula.foodplanner.database.RecipeDao;
import np.com.aanalbasaula.foodplanner.database.RecipeStepDao;

/**
 * A Database utility asynchronous task which can be used to only update the Recipe.
 * The Update works only when the ID of the Recipe has not been modified. It deletes
 * the recipe to clear all the ingredients along with it and finally inserts it into
 * the database to get an updated feeling.
 */
public class RecipeUpdater extends RecipeCreator {

    /**
     * The Status Listener interface for callbacks interested in the status of the updater.
     */
    public interface RecipeUpdateStatusListener {

        /**
         * Called when the provided recipes have been successfully updated.
         * @param recipes the recipes that were updated
         */
        void onRecipeUpdated(Recipe[] recipes);

    }

    private static final String TAG = RecipeUpdater.class.getSimpleName();

    private final RecipeDao recipeDao;
    private final RecipeUpdateStatusListener listener;

    public RecipeUpdater(RecipeDao recipeDao,
                         IngredientDao ingredientDao,
                         RecipeStepDao recipeStepDao,
                         RecipeUpdateStatusListener listener) {
        super(recipeDao, ingredientDao, recipeStepDao, null);
        this.recipeDao = recipeDao;
        this.listener = listener;

    }

    @Override
    protected Recipe[] doInBackground(Recipe... recipes) {
        Log.i(TAG, "doInBackground: Updating recipes count:" + recipes.length);

        for (Recipe recipe :
                recipes) {
            Log.d(TAG, "doInBackground: Deleting Recipe: Id: " + recipe.getId());
            recipeDao.delete(recipe);
        }

        // create new recipes
        return super.doInBackground(recipes);
    }

    @Override
    protected void onPostExecute(Recipe[] recipes) {
        if (listener != null) {
            listener.onRecipeUpdated(recipes);
        }
    }

}
