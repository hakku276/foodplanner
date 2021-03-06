package np.com.aanalbasaula.foodplanner.views.shopping_list;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.List;
import java.util.Set;

import np.com.aanalbasaula.foodplanner.R;
import np.com.aanalbasaula.foodplanner.database.AppDatabase;
import np.com.aanalbasaula.foodplanner.database.ShoppingListDao;
import np.com.aanalbasaula.foodplanner.database.ShoppingListEntry;
import np.com.aanalbasaula.foodplanner.database.utils.DatabaseLoader;
import np.com.aanalbasaula.foodplanner.database.utils.EntryCreationStrategies;
import np.com.aanalbasaula.foodplanner.database.utils.EntryCreator;
import np.com.aanalbasaula.foodplanner.database.utils.EntryDeleter;
import np.com.aanalbasaula.foodplanner.database.utils.EntryUpdater;
import np.com.aanalbasaula.foodplanner.utils.BroadcastUtils;

/**
 * A {@link Fragment} subclass to display the shopping list of the user.
 * Use the {@link ShoppingListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ShoppingListFragment extends Fragment {

    private static final String TAG = ShoppingListFragment.class.getSimpleName();

    // ui related
    private RecyclerView recyclerView;
    private ImageButton btnAddListEntry;
    private EditText textListItemName;
    private Button btnClearAllSelected;
    private ShoppingListViewAdapter adapter;

    // Database related
    private ShoppingListDao shoppingListDao;

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
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // initialize the database
        shoppingListDao = AppDatabase.getInstance(getContext()).getShoppingListDao();
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.i(TAG, "onStart: Fragment started. Loading all shopping list items");
        loadItemsFromDatabaseAsync();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_shopping_list, container, false);

        recyclerView = view.findViewById(R.id.content);
        btnAddListEntry = view.findViewById(R.id.btn_add);
        textListItemName = view.findViewById(R.id.text_name);
        btnClearAllSelected = view.findViewById(R.id.btn_clear_all_selected);

        // setup the recycler view
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        // setup the add list entry button
        btnAddListEntry.setOnClickListener(addShoppingListEntryButtonListener);

        // setup the clear all selected button listener
        btnClearAllSelected.setOnClickListener(clearAllButtonListener);

        // setup the text view
        textListItemName.setOnEditorActionListener(editorActionListener);

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        // register shopping list entry creation broadcast listener
        BroadcastUtils.registerLocalBroadcastListener(context, shoppingListChangeBroadcastReceiver,
                BroadcastUtils.ACTION_SHOPPING_LIST_ENTRY_CREATED,
                BroadcastUtils.ACTION_SHOPPING_LIST_ENTRY_DELETED);
    }

    @Override
    public void onDetach() {
        super.onDetach();

        // unregister from the broadcast listener
        BroadcastUtils.unregisterLocalBroadcastListener(getContext(), shoppingListChangeBroadcastReceiver);
    }

    /**
     * Initiates a load items request on the database asynchronously.
     */
    private void loadItemsFromDatabaseAsync() {
        Log.i(TAG, "loadItemsFromDatabaseAsync: Loading Items from database");
        DatabaseLoader<ShoppingListDao, ShoppingListEntry> loader = new DatabaseLoader<>(shoppingListDao, ShoppingListDao::getShoppingList, shoppingListLoadListener);
        loader.execute();
    }

    /**
     * Validate whether the shopping list items input is correct or not. If not, appropriate error
     * notices are displayed to the user.
     * @return true if the input is valid, else false
     */
    private boolean isInputValid() {
        String itemName = textListItemName.getText().toString();
        // initially validate the entry
        if (itemName.isEmpty()) {
            Log.d(TAG, "onClick: The input length is zero. Cannot proceed with shopping list creation");
            textListItemName.setError(getString(R.string.error_required));
            return false;
        }

        for (ShoppingListEntry entry :
                adapter.getItems()) {
            if (itemName.equals(entry.getName())) {
                textListItemName.setError(getString(R.string.error_already_exists));
                return false;
            }
        }

        return true;
    }

    /**
     * A database generic listener to listen to database loads. It refreshes the adapter on database load
     */
    private DatabaseLoader.DatabaseLoadListener<ShoppingListEntry> shoppingListLoadListener = new DatabaseLoader.DatabaseLoadListener<ShoppingListEntry>() {
        @Override
        public void onItemsLoaded(@NonNull List<ShoppingListEntry> items) {
            Log.i(TAG, "onItemsLoaded: Shopping List Entries have been successfully loaded");
            adapter = new ShoppingListViewAdapter(items, shoppingListSelectionChangeListener);
            recyclerView.setAdapter(adapter);
        }
    };

    /**
     * A database generic listener, to listen to database Shopping list entry creation events.
     */
    private EntryCreator.EntryCreationListener<ShoppingListEntry> shoppingListEntryCreationListener = new EntryCreator.EntryCreationListener<ShoppingListEntry>() {
        @Override
        public void onEntriesCreated(ShoppingListEntry[] items) {
            // broadcast that the shopping list entry was created
            Log.i(TAG, "onEntriesCreated: Shopping List Entry successfully created");
            BroadcastUtils.sendLocalBroadcast(getContext(), BroadcastUtils.ACTION_SHOPPING_LIST_ENTRY_CREATED);
        }
    };

    private EntryDeleter.DatabaseDeletionListener<ShoppingListEntry> shoppingListEntryDeletionListener = new EntryDeleter.DatabaseDeletionListener<ShoppingListEntry>() {
        @Override
        public void onItemsDeleted() {
            Log.i(TAG, "onItemsDeleted: Successfully deleted database entries");
            BroadcastUtils.sendLocalBroadcast(getContext(), BroadcastUtils.ACTION_SHOPPING_LIST_ENTRY_DELETED);
        }
    };

    /**
     * A click listener for the clear all items in the shopping list button
     */
    private View.OnClickListener clearAllButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Log.i(TAG, "onClick: Clear all selected items requested");
            Set<ShoppingListEntry> selectedEntries = adapter.getSelectedItems();

            if (!selectedEntries.isEmpty()) {
                Log.d(TAG, "onClick: There are selected items to be deleted");
                EntryDeleter<ShoppingListDao, ShoppingListEntry> entryDeleter = new EntryDeleter<>(shoppingListDao, ShoppingListDao::delete, shoppingListEntryDeletionListener);
                entryDeleter.execute(selectedEntries.toArray(new ShoppingListEntry[0]));
            }
        }
    };

    /**
     * A click listener to the add shopping list item.
     */
    private View.OnClickListener addShoppingListEntryButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Log.i(TAG, "onClick: Add shopping list button clicked");

            if (!isInputValid()) {
                Log.i(TAG, "onClick: The user input is invalid");
                return;
            }

            // begin creation of shopping list entry
            ShoppingListEntry entry = new ShoppingListEntry();
            entry.setName(textListItemName.getText().toString());

            EntryCreator<ShoppingListDao, ShoppingListEntry> creator = new EntryCreator<>(shoppingListDao,
                    EntryCreationStrategies.shoppingListEntryCreationStrategy,
                    shoppingListEntryCreationListener);
            creator.execute(entry);
            textListItemName.setText(""); // clear the successfully adding
        }
    };

    /**
     * Listen to events on the keyboard specially the done button, to enable users to add items
     * with the done button
     */
    private EditText.OnEditorActionListener editorActionListener = new TextView.OnEditorActionListener() {
        @Override
        public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
            Log.i(TAG, "onEditorAction: Keyboard button action triggered");

            if (textListItemName.getText().length() == 0) {
                Log.i(TAG, "onEditorAction: User input is empty. Hiding keyboard");
                // enable the system to handle the event thereby closing the keyboard
                return false;
            }

            addShoppingListEntryButtonListener.onClick(textView);

            // disable the system to handle the event. does not close the keyboard
            return true;
        }
    };

    /**
     * A broadcast receiver that waits for changes on the shopping list within the database.
     * Note: Listening to updates is not particularly necessary.
     */
    private BroadcastReceiver shoppingListChangeBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i(TAG, "onReceive: Shopping list database has been changed.");
            loadItemsFromDatabaseAsync();
        }
    };

    /**
     * The listener to events within the Shopping list. For instance the user selected an item after buying it.
     * NOTE: This could have been done without the listener on the View Adapter, but to separate the concern of
     * Database access to the fragment. A listener has been used.
     */
    private ShoppingListViewAdapter.ShoppingListSelectionChangeListener shoppingListSelectionChangeListener = new ShoppingListViewAdapter.ShoppingListSelectionChangeListener() {
        @Override
        public void onShoppingListEntrySelectionChanged(ShoppingListEntry entry) {
            Log.i(TAG, "onShoppingListEntrySelectionChanged: A Single shopping list entry has been changed. ");

            // TODO update the entry on the database
            EntryUpdater<ShoppingListDao, ShoppingListEntry> updater = new EntryUpdater<>(shoppingListDao, ShoppingListDao::update, null);
            updater.execute(entry);
        }

        @Override
        public void onShoppingListSelectionChanged(Set<ShoppingListEntry> entries) {
            Log.i(TAG, "onShoppingListSelectionChanged: User has change shopping list selection. Now Selected: " + entries.size());
            if (entries.size() == 0) {
                btnClearAllSelected.setVisibility(View.GONE);
            } else {
                btnClearAllSelected.setVisibility(View.VISIBLE);
            }
        }
    };
}
