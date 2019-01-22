package np.com.aanalbasaula.foodplanner.views.shoppinglist;


import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import np.com.aanalbasaula.foodplanner.R;
import np.com.aanalbasaula.foodplanner.database.AppDatabase;
import np.com.aanalbasaula.foodplanner.database.CartItem;
import np.com.aanalbasaula.foodplanner.database.utils.CreateShoppingCartItemAsync;
import np.com.aanalbasaula.foodplanner.database.utils.LoadActiveShoppingCartItemsAsync;
import np.com.aanalbasaula.foodplanner.database.utils.UpdateCartItemAsync;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ShoppingListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ShoppingListFragment extends Fragment implements LoadActiveShoppingCartItemsAsync.Listener,
        CreateShoppingCartItemAsync.Listener, ShoppingListRecyclerViewAdapter.ShoppingListStatusListener {

    private static final String TAG = ShoppingListFragment.class.getSimpleName();

    /**
     * The items that the user has in the cart for shopping
     */
    private List<CartItem> cartItems;

    /**
     * The recycler view that is responsible for showing the list items on the screen
     */
    private RecyclerView mRecyclerView;

    private AppDatabase db;

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
        db = AppDatabase.getInstance(getContext());
        LoadActiveShoppingCartItemsAsync async = new LoadActiveShoppingCartItemsAsync(db.getShoppingCartDao(), this);
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

        FloatingActionButton addButton = view.findViewById(R.id.button_add);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i(TAG, "onClick: Add button clicked");
                onAddItemRequested();
            }
        });

        return view;
    }

    @Override
    public void onCartItemsLoaded(List<CartItem> items) {
        Log.i(TAG, "onCartItemsLoaded: The shopping cart items have been loaded");
        this.cartItems = items;
        mRecyclerView.setAdapter(new ShoppingListRecyclerViewAdapter(this.cartItems, this));
    }

    /**
     * Handler for a click on the Add Item to Cart button
     */
    private void onAddItemRequested() {
        Log.i(TAG, "onClickAddItem: Add cart item requested by user");
        if (getActivity() != null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

            //create the body to be added into the dialog
            LinearLayout layout = new LinearLayout(getActivity());
            layout.setOrientation(LinearLayout.VERTICAL);
            int paddingDialogToContent = (int) getResources().getDimension(R.dimen.padding_dialog_to_content);
            layout.setPadding(paddingDialogToContent, paddingDialogToContent, paddingDialogToContent, paddingDialogToContent);

            TextView message = new TextView(getActivity());
            message.setText(R.string.message_dialog_add_cart_item);
            message.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            final EditText editText = new EditText(getActivity());
            editText.setLayoutParams(new LinearLayoutCompat.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            editText.setSingleLine();
            editText.setImeOptions(EditorInfo.IME_ACTION_DONE);
            editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);

            layout.addView(message);
            layout.addView(editText);

            builder.setTitle(R.string.title_dialog_add_cart_item);
            builder.setView(layout);
            builder.setPositiveButton(R.string.button_save, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Log.i(TAG, "onClick: User clicked Save button");
                    CartItem item = new CartItem();
                    item.setName(editText.getText().toString());
                    item.setActive(true);
                    addToCart(item);
                }
            });
            builder.setNegativeButton(R.string.button_cancel, null);

            AlertDialog dialog = builder.create();

            if (dialog.getWindow() != null) {
                dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
            }
            dialog.show();
        }
    }

    @Override
    public void onItemRemoveRequested(int position) {
        Log.i(TAG, "onItemRemoveRequested: Item remove has been requested by the user at position: " + position);
        CartItem item = cartItems.get(position);
        item.setActive(false);
        UpdateCartItemAsync updateCartItemAsync = new UpdateCartItemAsync(db.getShoppingCartDao(), null);
        updateCartItemAsync.execute(item);
    }

    @Override
    public void onItemRestoreRequested(int position) {
        Log.i(TAG, "onItemRestoreRequested: Item restore state has been requested by the user at position: " + position);
        CartItem item = cartItems.get(position);
        item.setActive(true);
        UpdateCartItemAsync updateCartItemAsync = new UpdateCartItemAsync(db.getShoppingCartDao(), null);
        updateCartItemAsync.execute(item);
    }

    /**
     * Adds the specific item to the cart
     *
     * @param item the string item to be added into the cart
     */
    private void addToCart(CartItem item) {
        Log.i(TAG, "addToCart: Adding Item to cart: " + item.getName());
        cartItems.add(item);
        mRecyclerView.getAdapter().notifyDataSetChanged();

        Log.i(TAG, "addToCart: Saving items to the database");
        CreateShoppingCartItemAsync task = new CreateShoppingCartItemAsync(db.getShoppingCartDao(), this);
        task.execute(item);
    }

    @Override
    public void onCartItemsCreated(CartItem[] cartItems) {
        Log.i(TAG, "onCartItemsCreated: Cart Item added successfully into database");
    }
}
