package np.com.aanalbasaula.foodplanner.views.cookbook;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import np.com.aanalbasaula.foodplanner.R;
import np.com.aanalbasaula.foodplanner.database.AppDatabase;
import np.com.aanalbasaula.foodplanner.database.Recipe;
import np.com.aanalbasaula.foodplanner.database.RecipeDao;
import np.com.aanalbasaula.foodplanner.database.utils.DatabaseLoader;
import np.com.aanalbasaula.foodplanner.utils.BroadcastUtils;
import np.com.aanalbasaula.foodplanner.views.meal_courses.PlanMealDialogFragment;
import np.com.aanalbasaula.foodplanner.views.utils.GenericRecyclerViewAdapter;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * Use the {@link CookBookFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CookBookFragment extends Fragment {

    private static final String TAG = CookBookFragment.class.getSimpleName();

    // ui related
    private RecyclerView recyclerView;
    private FloatingActionButton btnCreateRecipe;

    // Database related
    private RecipeDao recipeDao;

    public CookBookFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     */
    public static CookBookFragment newInstance() {
        return new CookBookFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        recipeDao = AppDatabase.getInstance(getContext()).getRecipeDao();
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.i(TAG, "onStart: Fragment started. Loading all recipes");
        loadItemsFromDatabaseAsync();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_cook_book, container, false);

        recyclerView = view.findViewById(R.id.list_cookbook);
        btnCreateRecipe = view.findViewById(R.id.btn_create_recipe);

        // setup the recycler view
        Context context = view.getContext();
        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(layoutManager);

        // setup the floating action button
        btnCreateRecipe.setOnClickListener(createButtonClickListener);

        return view;
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        Log.i(TAG, "onContextItemSelected: Context Item Selected here");
        return super.onContextItemSelected(item);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        // register recipe creation broadcast listener
        BroadcastUtils.registerLocalBroadcastListener(context, recipeCreationBroadcastReceiver, BroadcastUtils.ACTION_RECIPE_CREATED);
    }

    @Override
    public void onDetach() {
        super.onDetach();

        // unregister broadcast listener
        BroadcastUtils.unregisterLocalBroadcastListener(getContext(), recipeCreationBroadcastReceiver);
    }

    /**
     * Load the Recipes from the database asynchronous.
     */
    private void loadItemsFromDatabaseAsync() {
        Log.i(TAG, "loadItemsFromDatabaseAsync: Initiating async recipes load from database");
        DatabaseLoader<RecipeDao, Recipe> loader = new DatabaseLoader<>(recipeDao, RecipeDao::getAllRecipes, databaseLoadListener);
        loader.execute();
    }

    /**
     * A broadcast listener that listens to events on creation of new recipes. This is generally used
     * to reload the database once items have been inserted into the database
     */
    private final BroadcastReceiver recipeCreationBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i(TAG, "onReceive: New recipe has been created");
            loadItemsFromDatabaseAsync();
        }
    };

    /**
     * The listener to database load events. The load is initiated by the {@linkplain #loadItemsFromDatabaseAsync()}
     */
    private final DatabaseLoader.DatabaseLoadListener<Recipe> databaseLoadListener = new DatabaseLoader.DatabaseLoadListener<Recipe>() {
        @Override
        public void onItemsLoaded(@NonNull List<Recipe> items) {
            Log.i(TAG, "onItemsLoaded: Successfully loaded recipes");

            GenericRecyclerViewAdapter<Recipe, DisplayRecipeViewHolder> adapter = new GenericRecyclerViewAdapter<>(items, displayViewHolderFactory);
            recyclerView.setAdapter(adapter);
        }
    };

    /**
     * A create recipe button listener to initiate create recipe workflow.
     */
    private final View.OnClickListener createButtonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Log.i(TAG, "onClick: Create Recipe button clicked");
            Intent intent = new Intent(getContext(), RecipeCreatorActivity.class);
            startActivity(intent);
        }
    };

    /**
     * The factory that creates the view holder used to display content on screen
     */
    private final GenericRecyclerViewAdapter.GenericViewHolderFactory<DisplayRecipeViewHolder> displayViewHolderFactory =
            new GenericRecyclerViewAdapter.GenericViewHolderFactory<DisplayRecipeViewHolder>() {
                @Override
                public DisplayRecipeViewHolder newInstance(View view) {
                    return new DisplayRecipeViewHolder(view);
                }

                @Override
                public View createView(LayoutInflater inflater, ViewGroup parent) {
                    return inflater.inflate(R.layout.layout_list_item_recipe, parent, false);
                }
            };

    /**
     * A view holder that can be used to display Recipes on a Recycler View. Depends on the
     * {@link GenericRecyclerViewAdapter} and a layout that contains the
     */
    private class DisplayRecipeViewHolder extends GenericRecyclerViewAdapter.GenericViewHolder<Recipe> {

        private final View mView;
        private final TextView mContentView;
        private Recipe item;

        public DisplayRecipeViewHolder(View view) {
            super(view);
            mView = view;
            mContentView = view.findViewById(R.id.content);
            mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.i(TAG, "onClick: Recipe Item clicked");
                    PlanMealDialogFragment fragment = PlanMealDialogFragment.build(item.getName());
                    fragment.show(getFragmentManager(), "meal-plan");
                }
            });

            mView.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
                @Override
                public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
                    Log.i(TAG, "onCreateContextMenu: User wants context menu on recipe item");
                    contextMenu.setHeaderTitle(view.getContext().getString(R.string.title_recipe_context_menu));
                    contextMenu.add(getAdapterPosition(), R.id.action_view, 0, R.string.action_view);
                    contextMenu.add(getAdapterPosition(), R.id.action_edit, 0, R.string.action_edit);
                    contextMenu.add(getAdapterPosition(), R.id.action_delete, 0, R.string.action_delete);
                }
            });
        }

        @Override
        public void bind(Recipe item) {
            this.item = item;
            mContentView.setText(item.getName());
        }
    }

}
