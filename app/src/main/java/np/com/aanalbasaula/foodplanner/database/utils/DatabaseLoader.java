package np.com.aanalbasaula.foodplanner.database.utils;

import android.arch.core.util.Function;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;

import java.util.LinkedList;
import java.util.List;

import np.com.aanalbasaula.foodplanner.database.MealCourse;

/**
 * A generic Database loader async task, which loads the database in the background provided the
 * proper information.
 *
 * @param <D> The dao class
 * @param <T> The Type of data being loaded
 */
public class DatabaseLoader<D, T> extends AsyncTask<Void, Void, List<T>> {

    private static final String TAG = DatabaseLoader.class.getSimpleName();

    // database properties
    private D dao;
    private Function<D, List<T>> loadMethod;

    // listener
    private DatabaseLoadListener<T> listener;

    /**
     * Create a new Async Task that uses the dao object to load the list of items
     * from the database. The defined load method is used in order to query the database
     * and the listener is notified after successful loading.
     *
     * @param dao        the dao object used to access the database
     * @param loadMethod the method to use when calling the database
     * @param listener   the listener to be notified on completion
     */
    public DatabaseLoader(D dao,
                          Function<D, List<T>> loadMethod,
                          DatabaseLoadListener<T> listener) {
        this.dao = dao;
        this.loadMethod = loadMethod;
        this.listener = listener;
    }

    @Override
    protected List<T> doInBackground(Void... voids) {
        List<T> items = null;
        try {
            items = loadMethod.apply(dao);
        } catch (Exception e) {
            Log.e(TAG, "doInBackground: Could not load the items from the database", e);
        }
        return items;
    }

    @Override
    protected void onPostExecute(List<T> ingredients) {
        Log.i(TAG, "onPostExecute: Items have been loaded, notifying the listener");
        this.listener.onItemsLoaded(ingredients);
    }

    /**
     * The interface which allows users of this utility class to access the result
     */
    public interface DatabaseLoadListener<T> {
        /**
         * Called once the items have been loaded from the database
         *
         * @param items the list of loaded items
         */
        void onItemsLoaded(@NonNull List<T> items);
    }
}
