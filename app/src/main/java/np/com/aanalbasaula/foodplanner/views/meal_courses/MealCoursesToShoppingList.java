package np.com.aanalbasaula.foodplanner.views.meal_courses;

import android.os.AsyncTask;
import android.util.Log;

import java.util.LinkedList;
import java.util.List;

import np.com.aanalbasaula.foodplanner.database.AppDatabase;
import np.com.aanalbasaula.foodplanner.database.Ingredient;
import np.com.aanalbasaula.foodplanner.database.IngredientDao;
import np.com.aanalbasaula.foodplanner.database.MealCourse;
import np.com.aanalbasaula.foodplanner.database.MealCourseDao;
import np.com.aanalbasaula.foodplanner.database.Recipe;
import np.com.aanalbasaula.foodplanner.database.ShoppingListDao;
import np.com.aanalbasaula.foodplanner.database.ShoppingListEntry;

/**
 * An Async task that converts the provided meal courses into items to be stored within
 * the Shopping List. Current Strategy is to simply copy everything without considering
 * duplicates.
 */
public class MealCoursesToShoppingList extends AsyncTask<Void, Void, Void> {

    private static final String TAG = MealCoursesToShoppingList.class.getSimpleName();
    private final IngredientDao ingredientDao;
    private final ShoppingListDao shoppingListDao;
    private final MealCourseDao mealCourseDao;

    public MealCoursesToShoppingList(AppDatabase db) {
        this.ingredientDao = db.getIngredientDao();
        this.shoppingListDao = db.getShoppingListDao();
        this.mealCourseDao = db.getMealCourseDao();
    }

    @Override
    protected Void doInBackground(Void... empty) {
        Log.i(TAG, "doInBackground: Auto-populating the Shopping list using the provided meals.");
        List<MealCourse> mealCourses = mealCourseDao.getAllMealCoursesInFuture();

        for (MealCourse mealCourse :
                mealCourses) {
            Log.i(TAG, "doInBackground: Gathering ingredients for: " + mealCourse.getName());

            if (mealCourse.getRecipeId() != null) {
                Log.d(TAG, "doInBackground: Meal linked to recipe: " + mealCourse.getRecipeId());
                List<Ingredient> ingredients = ingredientDao.getIngredientsForRecipe(mealCourse.getRecipeId());
                List<ShoppingListEntry> shoppingListEntries = new LinkedList<>();

                Log.d(TAG, "doInBackground: Creating shopping list entries from ingredients");
                for (Ingredient ingredient :
                        ingredients) {
                    Log.d(TAG, "doInBackground: Creating shopping list entry for ingredient: " + ingredient.getName());
                    ShoppingListEntry entry = new ShoppingListEntry();
                    entry.setName(ingredient.getName());
                    shoppingListEntries.add(entry);
                }

                Log.i(TAG, "doInBackground: Saving the shopping list entries");
                shoppingListDao.insert(shoppingListEntries.toArray(new ShoppingListEntry[0]));
            }
        }

        return null;
    }

}
