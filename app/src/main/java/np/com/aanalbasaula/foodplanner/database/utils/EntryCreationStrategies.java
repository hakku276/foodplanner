package np.com.aanalbasaula.foodplanner.database.utils;

import android.util.Log;

import np.com.aanalbasaula.foodplanner.database.MealCourse;
import np.com.aanalbasaula.foodplanner.database.MealCourseDao;

/**
 * A collection of predefined Database Entry Creation strategies, that can be used with
 * {@link EntryCreator}
 */
public class EntryCreationStrategies {
    private static final String TAG = EntryCreationStrategies.class.getSimpleName();

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

}
