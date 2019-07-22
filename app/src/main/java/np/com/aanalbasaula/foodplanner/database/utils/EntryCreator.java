package np.com.aanalbasaula.foodplanner.database.utils;

import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;

/**
 * An async task that enables creation of entries into the database using a defined
 * D dao object, T items and a Creation Strategy.
 *
 * @param <D> the DAO to be used to create the item T into database
 * @param <T> the Item type to be stored into the database
 */
public class EntryCreator<D, T> extends AsyncTask<T, Void, T[]>{
    private static final String TAG = EntryCreator.class.getSimpleName();

    @NonNull
    private D dao;

    private CreationStrategy<D, T> creationStrategy;

    @NonNull
    private EntryCreationListener<T> listener;

    public EntryCreator(@NonNull D dao, CreationStrategy<D, T> strategy, @NonNull EntryCreationListener<T> listener){
        this.dao = dao;
        this.listener = listener;
        this.creationStrategy = strategy;
    }

    @SafeVarargs
    @Override
    protected final T[] doInBackground(T... items) {
        Log.i(TAG, "doInBackground: Creating entries: " + items.length);
        try {
            creationStrategy.createEntries(dao, items);
        }catch (Exception e){
            Log.e(TAG, "doInBackground: Could not create recipe entries", e);
        }
        return items;
    }

    @Override
    protected void onPostExecute(T[] ingredients) {
        listener.onEntriesCreated(ingredients);
    }

    /**
     * A Listener which is notified about the completion of Entry Creation.
     * @param <T> the Type of Entry that was created
     */
    public interface EntryCreationListener<T> {

        /**
         * Called when the Items have been created. The Database IDs may or
         * may not be filled depending upon the {@link CreationStrategy} used.
         *
         * @param items created items
         */
        void onEntriesCreated(T[] items);
    }

    /**
     * A database entry creation strategy which is used to create the entry into the database.
     * The strategy defined can set the database Ids into the entries.
     *
     * @param <D> the DAO which is used to save the entry into the database
     * @param <T> the Entry class to be saved into the database
     */
    public interface CreationStrategy<D, T> {

        /**
         * Create the entries into the database. The implementation of this method,
         * may or may not set the database ID of the entries.
         *
         * @param dao     the DAO that can be used to store the entries
         * @param entries the Entries to be stored
         */
        void createEntries(D dao, T[] entries);
    }

}
