package np.com.aanalbasaula.foodplanner.database.utils;

import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.LinkedList;
import java.util.List;

import np.com.aanalbasaula.foodplanner.database.CartItem;
import np.com.aanalbasaula.foodplanner.database.ShoppingCartDao;

/**
 * Utility class to save {@link CartItem} into the database. Since the database actions should be performed on
 * a non UI thread {@linkplain AsyncTask} has been used.
 * Usage:
 * <pre>
 *     ShoppingCardDao dao = AppDatabase.getShoppingCartDao();
 *     CreateShoppingCartItemAsync createTask = new CreateShoppingCartItemAsync(dao, this); // any other listener
 *     createTask.execute(item1, item2, item3);
 * </pre>
 */
public class CreateShoppingCartItemAsync extends AsyncTask<CartItem, Void, CartItem[]> {
    private static final String TAG = CreateShoppingCartItemAsync.class.getSimpleName();

    @NonNull
    private ShoppingCartDao dao;

    @Nullable
    private Listener listener;

    public CreateShoppingCartItemAsync(@NonNull ShoppingCartDao dao, @Nullable Listener listener) {
        this.dao = dao;
        this.listener = listener;
    }

    @Override
    protected CartItem[] doInBackground(CartItem... cartItems) {
        Log.i(TAG, "doInBackground: Adding CartItems to the database: " + cartItems.length);
        List<CartItem> savedItems = new LinkedList<>();
        for (CartItem item :
                cartItems) {
            try {
                long[] id = dao.insert(item);
                item.setId(id[0]);
                savedItems.add(item);
            } catch (Exception e) {
                Log.e(TAG, "doInBackground: Could not save the item: " + item.getName(), e);
            }
        }
        return savedItems.toArray(new CartItem[0]);
    }

    @Override
    protected void onPostExecute(CartItem[] items) {
        Log.i(TAG, "onPostExecute: Create Shopping Cart Items completed: " + items.length + " successfully saved");
        if (listener != null && items.length != 0) {
            listener.onCartItemsCreated(items);
        }
    }

    public interface Listener {
        void onCartItemsCreated(CartItem[] cartItems);
    }
}
