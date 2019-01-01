package np.com.aanalbasaula.foodplanner.database.utils;

import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import np.com.aanalbasaula.foodplanner.database.CartItem;
import np.com.aanalbasaula.foodplanner.database.ShoppingCartDao;

public class LoadShoppingCartItemsAsync extends AsyncTask<Void, Void, List<CartItem>> {

    private static final String TAG = LoadShoppingCartItemsAsync.class.getSimpleName();

    @NonNull
    private ShoppingCartDao dao;

    @NonNull
    private Listener listener;

    public LoadShoppingCartItemsAsync(@NonNull ShoppingCartDao dao, @NonNull Listener listener) {
        this.dao = dao;
        this.listener = listener;
    }

    @Override
    protected List<CartItem> doInBackground(Void... voids) {
        List<CartItem> items = new ArrayList<>(0);
        try {
            items = dao.getAllCartItems();
        } catch (Exception e) {
            Log.e(TAG, "doInBackground: Could not load the Cart items due to exception", e);
        }
        return items;
    }

    @Override
    protected void onPostExecute(List<CartItem> cartItems) {
        Log.i(TAG, "onPostExecute: The shopping cart items have been successfully loaded");
        listener.onCartItemsLoaded(cartItems);
    }

    public interface Listener {
        void onCartItemsLoaded(List<CartItem> items);
    }
}
