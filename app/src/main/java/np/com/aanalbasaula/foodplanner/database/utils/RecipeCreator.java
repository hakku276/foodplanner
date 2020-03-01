package np.com.aanalbasaula.foodplanner.database.utils;

import android.os.AsyncTask;
import android.util.Log;

import java.util.List;

import np.com.aanalbasaula.foodplanner.database.Ingredient;
import np.com.aanalbasaula.foodplanner.database.IngredientDao;
import np.com.aanalbasaula.foodplanner.database.Recipe;
import np.com.aanalbasaula.foodplanner.database.RecipeDao;
import np.com.aanalbasaula.foodplanner.database.RecipeStep;
import np.com.aanalbasaula.foodplanner.database.RecipeStepDao;

/**
 * A Database utility asynchronous task which can be used to only create the Recipe.
 * It saves the Recipe into the Recipe Table, then if available, saves the Ingredients and the
 * RecipeSteps
 */
public class RecipeCreator extends AsyncTask<Recipe, Void, Recipe[]> {

    /**
     * The Status Listener interface for callbacks interested in the status of the creator.
     */
    public interface RecipeCreationStatusListener {

        /**
         * Called when the provided recipes have been successfully created.
         * @param recipes the recipes that were updated
         */
        void onRecipeCreated(Recipe[] recipes);

    }

    private static final String TAG = RecipeCreator.class.getSimpleName();

    private final RecipeDao recipeDao;
    private final IngredientDao ingredientDao;
    private final RecipeStepDao recipeStepDao;
    private final RecipeCreationStatusListener listener;

    public RecipeCreator(RecipeDao recipeDao,
                         IngredientDao ingredientDao,
                         RecipeStepDao recipeStepDao,
                         RecipeCreationStatusListener listener) {
        this.recipeDao = recipeDao;
        this.ingredientDao = ingredientDao;
        this.listener = listener;
        this.recipeStepDao = recipeStepDao;
    }

    @Override
    protected Recipe[] doInBackground(Recipe... recipes) {
        Log.i(TAG, "doInBackground: Updating recipes count:" + recipes.length);

        for (Recipe recipe :
                recipes) {

            // create the recipe
            long[] ids = recipeDao.insert(recipe);
            recipe.setId(ids[0]);
            Log.d(TAG, "doInBackground: New Recipe Object created: " + recipe.getId());

            if (recipe.getIngredients() != null) {
                Log.d(TAG, "doInBackground: Creating the list of ingredients");

                if (recipe.getIngredients() != null) {
                    Ingredient[] ingredients = prepareIngredients(recipe);
                    EntryCreationStrategies.ingredientEntryCreationStrategy.createEntries(ingredientDao, ingredients);
                }

                if (recipe.getRecipeSteps() != null) {
                    RecipeStep[] recipeSteps = prepareRecipeSteps(recipe);
                    EntryCreationStrategies.recipeStepEntryCreationStrategy.createEntries(recipeStepDao, recipeSteps);
                }
            }
        }

        return recipes;
    }

    @Override
    protected void onPostExecute(Recipe[] recipes) {
        if (listener != null) {
            listener.onRecipeCreated(recipes);
        }
    }

    /**
     * Prepare the recipe steps for storage
     *
     * @param recipe the recipe for which the recipe steps need to be prepared
     * @return the recipe steps prepared to be stored
     */
    private RecipeStep[] prepareRecipeSteps(Recipe recipe) {
        RecipeStep[] recipeSteps = new RecipeStep[recipe.getRecipeSteps().size()];

        int i = 0;
        for (RecipeStep recipeStep :
                recipe.getRecipeSteps()) {
            recipeStep.setRecipeId(recipe.getId());
            recipeStep.setOrder(i);
            recipeSteps[i] = recipeStep;
            i++;
        }

        return recipeSteps;
    }

    /**
     * Prepare the Ingredients for storage
     *
     * @param recipe the recipe for which the ingredients need to be prepared
     * @return the ingredients prepared to be stored
     */
    private Ingredient[] prepareIngredients(Recipe recipe) {
        Ingredient[] ingredients = new Ingredient[recipe.getIngredients().size()];

        int i = 0;
        for (Ingredient ingredient :
                recipe.getIngredients()) {
            ingredient.setRecipeId(recipe.getId());
            ingredients[i] = ingredient;
            i++;
        }

        return ingredients;
    }

}
