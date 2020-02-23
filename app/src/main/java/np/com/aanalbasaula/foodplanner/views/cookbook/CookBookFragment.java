package np.com.aanalbasaula.foodplanner.views.cookbook;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import np.com.aanalbasaula.foodplanner.R;
import np.com.aanalbasaula.foodplanner.database.AppDatabase;
import np.com.aanalbasaula.foodplanner.database.Recipe;
import np.com.aanalbasaula.foodplanner.database.RecipeDao;
import np.com.aanalbasaula.foodplanner.database.utils.DatabaseLoader;
import np.com.aanalbasaula.foodplanner.database.utils.EntryDeleter;
import np.com.aanalbasaula.foodplanner.utils.BroadcastUtils;
import np.com.aanalbasaula.foodplanner.utils.PopupMenuUtils;
import np.com.aanalbasaula.foodplanner.utils.UIUtils;
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
    private static final int REQUEST_EDIT_RECIPE = 1;

    // ui related
    private RecyclerView recyclerView;
    private GenericRecyclerViewAdapter<Recipe, DisplayRecipeViewHolder> recipeAdapter;
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
        Log.i(TAG, "onContextItemSelected: Context Item at position: " + item.getGroupId());

        Recipe recipe = null;

        switch (UIUtils.getActionIdForContextMenuItem(item)) {
            case R.id.action_recipe_view:
                recipe = recipeAdapter.getItemAtPosition(UIUtils.getPositionForContextMenuItem(item));
                viewRecipe(recipe);
                return true;
        }

        return false;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        // register recipe creation broadcast listener
        BroadcastUtils.registerLocalBroadcastListener(context, recipeDataSourceUpdatesBroadcastReceiver,
                BroadcastUtils.ACTION_RECIPE_CREATED,
                BroadcastUtils.ACTION_RECIPE_UPDATED,
                BroadcastUtils.ACTION_RECIPE_DELETED);
        BroadcastUtils.registerLocalBroadcastListener(context, moreActionBroadcastReceiver,
                BroadcastUtils.ACTION_RECIPE_REQUEST_PLAN,
                BroadcastUtils.ACTION_RECIPE_REQUEST_EDIT,
                BroadcastUtils.ACTION_RECIPE_REQUEST_DELETE);

    }

    @Override
    public void onDetach() {
        super.onDetach();

        // unregister broadcast listener
        BroadcastUtils.unregisterLocalBroadcastListener(getContext(), recipeDataSourceUpdatesBroadcastReceiver);
        BroadcastUtils.unregisterLocalBroadcastListener(getContext(), moreActionBroadcastReceiver);
    }

    /**
     * View the details of the recipe. Loads another activity to display the details of this recipe
     *
     * @param recipe the recipe to view
     */
    private void viewRecipe(Recipe recipe) {
        if (recipe == null) {
            Log.e(TAG, "viewRecipe: Cannot view recipe, since it is null");
            return;
        }

        Intent intent = new Intent(getContext(), RecipeViewerActivity.class);
        intent.putExtra(RecipeViewerActivity.EXTRA_RECIPE, recipe);
        startActivity(intent);
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
     * A broadcast listener that listens to events on datasource changes. This is generally used
     * to reload the database once items have been inserted into the database
     */
    private final BroadcastReceiver recipeDataSourceUpdatesBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i(TAG, "onReceive: New recipe has been created");
            loadItemsFromDatabaseAsync();
        }
    };

    /**
     * A broadcast listener that listens to events on user requests on the UI.
     */
    private final BroadcastReceiver moreActionBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Recipe recipe = BroadcastUtils.extractPayload(intent);
            if (recipe == null) {
                Log.e(TAG, "onReceive: Could not extract recipe from more actions request");
                return;
            }

            switch (intent.getAction()) {
                case BroadcastUtils.ACTION_RECIPE_REQUEST_PLAN:
                    Log.i(TAG, "onReceive: Planning Recipe: " + recipe.getName());
                    PlanMealDialogFragment fragment = PlanMealDialogFragment.build(recipe);
                    fragment.show(getFragmentManager(), "meal-plan");
                    break;
                case BroadcastUtils.ACTION_RECIPE_REQUEST_EDIT:
                    Log.i(TAG, "onReceive: Editing recipe: " + recipe.getName());
                    RecipeCreatorActivity.showActivity(getActivity(), recipe, REQUEST_EDIT_RECIPE);
                    break;
                case BroadcastUtils.ACTION_RECIPE_REQUEST_DELETE:
                    Log.i(TAG, "onReceive: Deleting recipe: " + recipe.getName());
                    EntryDeleter<RecipeDao, Recipe> deleter = new EntryDeleter<>(recipeDao, RecipeDao::delete,
                            () -> BroadcastUtils.sendLocalBroadcast(getContext(), BroadcastUtils.ACTION_RECIPE_DELETED));
                    deleter.execute(recipe);
                    break;
            }
        }
    };

    /**
     * The listener to database load events. The load is initiated by the {@linkplain #loadItemsFromDatabaseAsync()}
     */
    private final DatabaseLoader.DatabaseLoadListener<Recipe> databaseLoadListener = new DatabaseLoader.DatabaseLoadListener<Recipe>() {
        @Override
        public void onItemsLoaded(@NonNull List<Recipe> items) {
            Log.i(TAG, "onItemsLoaded: Successfully loaded recipes");

            recipeAdapter = new GenericRecyclerViewAdapter<>(items, displayViewHolderFactory);
            recyclerView.setAdapter(recipeAdapter);
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
    private class DisplayRecipeViewHolder extends GenericRecyclerViewAdapter.GenericViewHolder<Recipe> implements PopupMenu.OnMenuItemClickListener {

        private final View mView;
        private final TextView mRecipeName;
        private final TextView mTags;
        private final RelativeLayout mRecipeLayout;
        private final ImageButton mBtnMore;
        private Recipe recipe;

        public DisplayRecipeViewHolder(View view) {
            super(view);
            mView = view;
            mRecipeName = view.findViewById(R.id.text_recipe_name);
            mTags = view.findViewById(R.id.text_recipe_tags);
            mRecipeLayout = view.findViewById(R.id.layout_recipe);
            mBtnMore = view.findViewById(R.id.btn_recipe_more);

            mBtnMore.setOnClickListener(this::moreActionsRequested);
            view.setOnClickListener(e -> viewRecipe(recipe));
        }

        /**
         * A click handler for more actions on more buttons clicked on a single recipe.
         * @param view
         */
        private void moreActionsRequested(View view) {
            Log.i(TAG, "moreActionsRequested: Recipe actions requested for recipe: " + recipe.getName());
            PopupMenuUtils.show(getContext(), mBtnMore, R.menu.popup_recipe, this);
        }

        @Override
        public void bind(Recipe item) {
            this.recipe = item;
            mRecipeName.setText(item.getName());
        }

        @Override
        public boolean onMenuItemClick(MenuItem item) {
            Log.i(TAG, "onMenuItemClick: User Requested Action: " + item.getItemId());

            switch (item.getItemId()) {
                case R.id.action_recipe_plan:
                    Log.i(TAG, "onMenuItemClick: User requested: PLAN recipe: " + recipe.getName());
                    BroadcastUtils.sendLocalBroadcast(getContext(), BroadcastUtils.ACTION_RECIPE_REQUEST_PLAN, recipe);
                    break;
                case R.id.action_recipe_edit:
                    Log.i(TAG, "onMenuItemClick: User requested: EDIT recipe: " + recipe.getName());
                    BroadcastUtils.sendLocalBroadcast(getContext(), BroadcastUtils.ACTION_RECIPE_REQUEST_EDIT, recipe);
                    break;
                case R.id.action_recipe_delete:
                    Log.i(TAG, "onMenuItemClick: User requested: DELETE recipe: " + recipe.getName());
                    BroadcastUtils.sendLocalBroadcast(getContext(), BroadcastUtils.ACTION_RECIPE_REQUEST_DELETE, recipe);
                    break;
            }

            return false;
        }
    }

}
