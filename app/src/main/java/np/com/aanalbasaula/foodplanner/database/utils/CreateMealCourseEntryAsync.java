package np.com.aanalbasaula.foodplanner.database.utils;

import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;

import np.com.aanalbasaula.foodplanner.database.MealCourse;
import np.com.aanalbasaula.foodplanner.database.MealCourseDao;

public class CreateMealCourseEntryAsync extends AsyncTask<MealCourse, Void, MealCourse[]>{
    private static final String TAG = CreateMealCourseEntryAsync.class.getSimpleName();

    @NonNull
    private MealCourseDao dao;

    @NonNull
    private CreateMealCourseEntryListener listener;

    public CreateMealCourseEntryAsync(@NonNull MealCourseDao dao, @NonNull CreateMealCourseEntryListener listener){
        this.dao = dao;
        this.listener = listener;
    }

    @Override
    protected MealCourse[] doInBackground(MealCourse... mealCourses) {
        Log.i(TAG, "doInBackground: Creating entries: " + mealCourses.length);
        try {
            long[] ids= dao.insert(mealCourses);
            Log.i(TAG, "doInBackground: Meal Course Added: " + ids.length);
            for (int i = 0; i < ids.length; i++) {
                Log.i(TAG, "doInBackground: New Meal Courses ID: " + ids[0]);
                mealCourses[i].setId(ids[i]);
            }
        }catch (Exception e){
            Log.e(TAG, "doInBackground: Could not create recipe entries", e);
        }
        return mealCourses;
    }

    @Override
    protected void onPostExecute(MealCourse[] ingredients) {
        listener.onMealCourseEntriesCreated(ingredients);
    }

    public interface CreateMealCourseEntryListener {

        /**
         * Called when the Meal Course entries have been created
         */
        void onMealCourseEntriesCreated(MealCourse[] ingredients);
    }
}
