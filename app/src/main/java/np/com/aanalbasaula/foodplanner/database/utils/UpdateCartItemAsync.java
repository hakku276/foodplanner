package np.com.aanalbasaula.foodplanner.database.utils;

import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.LinkedList;
import java.util.List;

import np.com.aanalbasaula.foodplanner.database.CartItem;
import np.com.aanalbasaula.foodplanner.database.ShoppingCartDao;

public class UpdateCartItemAsync extends AsyncTask<CartItem, Void, List<CartItem>> {

    private static final String TAG = UpdateCartItemAsync.class.getSimpleName();

    @NonNull
    private ShoppingCartDao dao;

    @Nullable
    private Listener listener;

    public UpdateCartItemAsync(@NonNull ShoppingCartDao dao, @Nullable Listener listener) {
        this.dao = dao;
        this.listener = listener;
    }

    @Override
    protected List<CartItem> doInBackground(CartItem... items) {
        List<CartItem> updatedItems = new LinkedList<>();
        for (CartItem item :
                items) {
            try {
                dao.update(item);
                updatedItems.add(item);
            } catch (Exception e) {
                Log.e(TAG, "doInBackground: Could not update cart item: " + item.getName(), e);
            }
        }
        return updatedItems;
    }

    @Override
    protected void onPostExecute(List<CartItem> cartItems) {
        Log.i(TAG, "onPostExecute: The shopping cart items have been successfully loaded");
        if (listener != null && cartItems.size() != 0) {
            listener.onCartItemsUpdated(cartItems);
        }
    }

    public interface Listener {
        void onCartItemsUpdated(List<CartItem> items);
    }
}
