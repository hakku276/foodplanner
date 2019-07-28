package np.com.aanalbasaula.foodplanner.database.utils;

import android.util.Log;

import np.com.aanalbasaula.foodplanner.database.MealCourse;
import np.com.aanalbasaula.foodplanner.database.MealCourseDao;
import np.com.aanalbasaula.foodplanner.database.Recipe;
import np.com.aanalbasaula.foodplanner.database.RecipeDao;

/**
 * A collection of predefined Database Entry Creation strategies, that can be used with
 * {@link EntryCreator}
 */
public class EntryCreationStrategies {
    private static final String TAG = EntryCreationStrategies.class.getSimpleName();

    private EntryCreationStrategies() {
        // empty private constructor to hide default constructor
    }

    /**
     * Entry creation Strategy to insert Meal Course using a MealCourseDAO
     */
    public static EntryCreator.CreationStrategy<MealCourseDao, MealCourse> mealCourseCreationStrategy = (dao, entries) -> {
        long[] ids = dao.insert(entries);
        for (int i = 0; i < ids.length; i++) {
            Log.i(TAG, "doInBackground: New Meal Courses ID: " + ids[0]);
            entries[i].setId(ids[i]);
        }
    };

    /**
     * Entry creation Strategy to insert Recipe using a RecipeDAO
     */
    public static EntryCreator.CreationStrategy<RecipeDao, Recipe> recipeCreationStrategy = (dao, entries) -> {
        long[] ids = dao.insert(entries);
        for (int i = 0; i < ids.length; i++) {
            Log.i(TAG, "doInBackground: New Meal Courses ID: " + ids[0]);
            entries[i].setId(ids[i]);
        }
    };

}
