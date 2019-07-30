package np.com.aanalbasaula.foodplanner.database.utils;

import android.arch.core.util.Function;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;

import java.util.Arrays;
import java.util.List;

/**
 * A generic Database Entry Updater async task, which updates the database entries in the background provided the
 * proper information.
 *
 * @param <D> The dao class
 * @param <T> The Type of data being loaded
 */
public class EntryUpdater<D, T> extends AsyncTask<T, Void, Void> {

    private static final String TAG = EntryUpdater.class.getSimpleName();

    // database properties
    private D dao;
    private UpdateStrategy<D, T> updateStrategy;

    // listener
    private DatabaseUpdateListener<T> listener;

    /**
     * Create a new Async Task that uses the dao object to update the list of items into the
     * database. The defined update method is used in order to update the database and the listener
     * is notified after successful update.
     *
     * @param dao            the dao object used to access the database
     * @param updateStrategy the strategy to use when updating the database
     * @param listener       the listener to be notified on completion
     */
    public EntryUpdater(D dao,
                        UpdateStrategy<D, T> updateStrategy,
                        DatabaseUpdateListener<T> listener) {
        this.dao = dao;
        this.updateStrategy = updateStrategy;
        this.listener = listener;
    }

    @SafeVarargs
    @Override
    protected final Void doInBackground(T... items) {
        try {
            updateStrategy.updateEntries(dao, items);
        } catch (Exception e) {
            Log.e(TAG, "doInBackground: Could not load the items from the database", e);
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void args) {
        Log.i(TAG, "onPostExecute: Items have been loaded, notifying the listener");
        this.listener.onItemsUpdated();
    }

    /**
     * The interface which allows users of this utility class to access the result
     */
    public interface DatabaseUpdateListener<T> {
        /**
         * Called once the items have been updated to the database
         *
         */
        void onItemsUpdated();
    }

    /**
     * A database entry update strategy which is used to update the entry into the database.
     *
     * @param <D> the DAO which is used to update the entry into the database
     * @param <T> the Entry class to be updated into the database
     */
    public interface UpdateStrategy<D, T> {

        /**
         * Update the entries into the database.
         *
         * @param dao     the DAO that can be used to updated the entries
         * @param entries the Entries to be update
         */
        void updateEntries(D dao, T[] entries);
    }
}
