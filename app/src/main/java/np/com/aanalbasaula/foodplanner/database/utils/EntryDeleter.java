package np.com.aanalbasaula.foodplanner.database.utils;

import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * A generic Database Entry Deleter async task, which deletes the database entries in the background provided the
 * proper information.
 *
 * @param <D> The dao class
 * @param <T> The Type of data being deleted
 */
public class EntryDeleter<D, T> extends AsyncTask<T, Void, Void> {

    private static final String TAG = EntryDeleter.class.getSimpleName();

    // database properties
    private D dao;
    private DeleteStrategy<D, T> deleteStrategy;

    // listener
    @Nullable
    private DatabaseDeletionListener<T> listener;

    /**
     * Create a new Async Task that uses the dao object to delete the list of items from the
     * database. The defined delete strategy is used in order to delete the entries and the listener
     * is notified after successful deletion.
     *
     * @param dao            the dao object used to access the database
     * @param deleteStrategy the strategy to use when deleting the database
     * @param listener       the listener to be notified on completion
     */
    public EntryDeleter(D dao,
                        DeleteStrategy<D, T> deleteStrategy,
                        @Nullable DatabaseDeletionListener<T> listener) {
        this.dao = dao;
        this.deleteStrategy = deleteStrategy;
        this.listener = listener;
    }

    @SafeVarargs
    @Override
    protected final Void doInBackground(T... items) {
        try {
            deleteStrategy.deleteEntries(dao, items);
        } catch (Exception e) {
            Log.e(TAG, "doInBackground: Could not delete the items from the database", e);
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void args) {
        Log.i(TAG, "onPostExecute: Items have been deleted, notifying the listener");
        if(listener != null) {
            this.listener.onItemsDeleted();
        }
    }

    /**
     * The interface which allows users of this utility class to access the result
     */
    public interface DatabaseDeletionListener<T> {
        /**
         * Called once the items have been deleted from the database
         *
         */
        void onItemsDeleted();

    }

    /**
     * A database entry deletion strategy which is used to delete the entry from the database.
     *
     * @param <D> the DAO which is used to update the entry into the database
     * @param <T> the Entry class to be deleted from the database
     */
    public interface DeleteStrategy<D, T> {

        /**
         * Update the entries into the database.
         *
         * @param dao     the DAO that can be used to updated the entries
         * @param entries the Entries to be update
         */
        void deleteEntries(D dao, T[] entries);
    }
}
