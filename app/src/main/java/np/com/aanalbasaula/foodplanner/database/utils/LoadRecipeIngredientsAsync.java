package np.com.aanalbasaula.foodplanner.database.utils;

import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.List;

import np.com.aanalbasaula.foodplanner.database.AppDatabase;
import np.com.aanalbasaula.foodplanner.database.Ingredient;
import np.com.aanalbasaula.foodplanner.database.IngredientDao;
import np.com.aanalbasaula.foodplanner.database.Recipe;
import np.com.aanalbasaula.foodplanner.database.RecipeIngredientDao;

public class LoadRecipeIngredientsAsync extends AsyncTask<Recipe, Void, Recipe[]> {

    private static final String TAG = LoadRecipeIngredientsAsync.class.getSimpleName();

    /**
     * The listener for the task
     */
    @Nullable
    private OnCompleteListener listener;

    @NonNull
    private IngredientDao ingredientDao;

    @NonNull
    private RecipeIngredientDao recipeIngredientDao;

    public LoadRecipeIngredientsAsync(@NonNull IngredientDao ingredientDao,
                                      @NonNull RecipeIngredientDao recipeIngredientDao,
                                      @Nullable OnCompleteListener listener){
        this.recipeIngredientDao = recipeIngredientDao;
        this.ingredientDao = ingredientDao;
        this.listener = listener;
    }

    @Override
    protected Recipe[] doInBackground(Recipe... args) {
        for (Recipe recipe : args){
            try {
                long[] ingredientIds = recipeIngredientDao.getAllIngredientIdsForRecipe(recipe.getId());
                if(ingredientIds != null) {
                    List<Ingredient> ingredients = ingredientDao.getAllIngredients(ingredientIds);
                    recipe.setIngredients(ingredients);
                } else {
                    recipe.setIngredients(null);
                }
            }catch (Exception e){
                Log.e(TAG, "doInBackground: Could not load the Recipe Ingredients for Recipe: " + recipe.getName(), e);
                recipe.setIngredients(null);
            }
        }
        return args;
    }

    @Override
    protected void onPostExecute(Recipe[] recipes) {
        if (listener != null){
            listener.onComplete(recipes);
        }
    }

    public interface OnCompleteListener {
        /**
         * Denotes the task has completed
         * @param recipes with the ingredients, null ingredients if there was a problem
         */
        void onComplete(@NonNull Recipe[] recipes);
    }
}
