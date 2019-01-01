package np.com.aanalbasaula.foodplanner.views.shoppinglist;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import np.com.aanalbasaula.foodplanner.R;
import np.com.aanalbasaula.foodplanner.database.AppDatabase;
import np.com.aanalbasaula.foodplanner.database.CartItem;
import np.com.aanalbasaula.foodplanner.database.utils.LoadShoppingCartItemsAsync;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ShoppingListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ShoppingListFragment extends Fragment implements LoadShoppingCartItemsAsync.Listener {

    private static final String TAG = ShoppingListFragment.class.getSimpleName();

    /**
     * The items that the user has in the cart for shopping
     */
    private List<CartItem> cartItems;

    /**
     * The recycler view that is responsible for showing the list items on the screen
     */
    private RecyclerView mRecyclerView;

    public ShoppingListFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment ShoppingListFragment.
     */
    public static ShoppingListFragment newInstance() {
        return new ShoppingListFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppDatabase db = AppDatabase.getInstance(getContext());
        LoadShoppingCartItemsAsync async = new LoadShoppingCartItemsAsync(db.getShoppingCartDao(), this);
        async.execute();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_shopping_list, container, false);

        mRecyclerView = view.findViewById(R.id.recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(mRecyclerView.getContext());
        mRecyclerView.setLayoutManager(layoutManager);

        return view;
    }

    @Override
    public void onCartItemsLoaded(List<CartItem> items) {
        Log.i(TAG, "onCartItemsLoaded: The shopping cart items have been loaded");
        this.cartItems = items;
        mRecyclerView.setAdapter(new ShoppingListRecyclerViewAdapter(this.cartItems));
    }
}
