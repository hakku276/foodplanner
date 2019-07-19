package np.com.aanalbasaula.foodplanner.database.utils;

import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.LinkedList;
import java.util.List;

import np.com.aanalbasaula.foodplanner.database.MealCourse;
import np.com.aanalbasaula.foodplanner.database.MealCourseDao;

public class UpdateMealCourseAsync extends AsyncTask<MealCourse, Void, List<MealCourse>> {

    private static final String TAG = UpdateMealCourseAsync.class.getSimpleName();

    @NonNull
    private MealCourseDao dao;

    @Nullable
    private Listener listener;

    public UpdateMealCourseAsync(@NonNull MealCourseDao dao, @Nullable Listener listener) {
        this.dao = dao;
        this.listener = listener;
    }

    @Override
    protected List<MealCourse> doInBackground(MealCourse... items) {
        List<MealCourse> updatedItems = new LinkedList<>();
        for (MealCourse item :
                items) {
            try {
                dao.update(item);
                updatedItems.add(item);
            } catch (Exception e) {
                Log.e(TAG, "doInBackground: Could not update meal course item: " + item.getName(), e);
            }
        }
        return updatedItems;
    }

    @Override
    protected void onPostExecute(List<MealCourse> mealCourses) {
        Log.i(TAG, "onPostExecute: The meal courses have been successfully updated");
        if (listener != null && mealCourses.size() != 0) {
            listener.onMealCourseUpdated(mealCourses);
        }
    }

    public interface Listener {
        void onMealCourseUpdated(List<MealCourse> items);
    }
}
