package np.com.aanalbasaula.foodplanner.views.cookbook;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.List;

import np.com.aanalbasaula.foodplanner.R;
import np.com.aanalbasaula.foodplanner.database.AppDatabase;
import np.com.aanalbasaula.foodplanner.database.Ingredient;
import np.com.aanalbasaula.foodplanner.database.IngredientDao;
import np.com.aanalbasaula.foodplanner.database.IngredientDao_Impl;
import np.com.aanalbasaula.foodplanner.database.Recipe;
import np.com.aanalbasaula.foodplanner.database.utils.DatabaseLoader;
import np.com.aanalbasaula.foodplanner.views.utils.GenericRecyclerViewAdapter;

/**
 * A simple {@link Fragment} subclass that is responsible for displaying all the ingredients
 * of a recipe. Use the {@link ShowIngredientFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ShowIngredientFragment extends Fragment {

    private static final String TAG = ShowIngredientFragment.class.getSimpleName();
    private static final String ARG_RECIPE = "recipe";

    // UI related elements
    private RecyclerView recyclerView;

    // database related elements
    private IngredientDao ingredientDao;

    // to display data
    private Recipe recipe; // the ingredients of the recipe being displayed
    private List<Ingredient> ingredients;

    public ShowIngredientFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of this fragment to display the
     * ingredients of a specific recipe.
     *
     * @param recipe The ingredients of the recipe to display on the screen
     * @return A new instance of fragment ShowIngredientFragment.
     */
    public static ShowIngredientFragment newInstance(Recipe recipe) {
        Log.i(TAG, "newInstance: Creating new instance of the fragment using: " + recipe);
        ShowIngredientFragment fragment = new ShowIngredientFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_RECIPE, recipe);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "onCreate: Fragment Created");
        if (getArguments() != null) {
            recipe = getArguments().getParcelable(ARG_RECIPE);
        }

        // setup the database related elements
        ingredientDao = AppDatabase.getInstance(requireContext()).getIngredientDao();


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.i(TAG, "onCreateView: Inflating Fragment View");
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_show_ingredient, container, false);

        this.recyclerView = view.findViewById(R.id.list_ingredients);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // only load ingredients after view has been setup
        loadIngredientsFromDatabase();

        return view;
    }

    /**
     * Loads all the ingredients for the recipe that is being displayed by this fragment.
     */
    private void loadIngredientsFromDatabase() {
        // load all the ingredients
        DatabaseLoader<IngredientDao, Ingredient> loader = new DatabaseLoader<>(ingredientDao, databaseEntriesLoader, ingredientsLoadListener);
        loader.execute();
    }

    /**
     * The lambda that loads the ingredients from the database for the recipe to be displayed
     */
    private DatabaseLoader.DatabaseLoadMethod<IngredientDao, List<Ingredient>> databaseEntriesLoader = dao -> dao.getIngredientsForRecipe(recipe.getId());

    /**
     * The lambda that listens to the ingredients being loaded from the database.
     */
    private DatabaseLoader.DatabaseLoadListener<Ingredient> ingredientsLoadListener = ingredients -> {
        Log.i(TAG, "IngredientsLoadListener: Loaded the ingredients from the database: " + ingredients.size());
        this.ingredients = ingredients;
        //setup the recycler view adapter
        GenericRecyclerViewAdapter<Ingredient, DisplayIngredientViewHolder> adapter = new GenericRecyclerViewAdapter<>(ingredients, new IngredientsViewHolderFactory());
        this.recyclerView.setAdapter(adapter);
    };

    private class DisplayIngredientViewHolder extends GenericRecyclerViewAdapter.GenericViewHolder<Ingredient> {

        private TextView mContentView;
        private ImageButton mDeleteButton;

        public DisplayIngredientViewHolder(View itemView) {
            super(itemView);
            this.mContentView = itemView.findViewById(R.id.content);
            this.mDeleteButton = itemView.findViewById(R.id.btn_remove);

            // hide the delete button
            this.mDeleteButton.setVisibility(View.GONE);
        }

        @Override
        public void bind(Ingredient item) {
            mContentView.setText(item.getName());
        }
    }

    private class IngredientsViewHolderFactory implements GenericRecyclerViewAdapter.GenericViewHolderFactory<DisplayIngredientViewHolder> {

        @Override
        public DisplayIngredientViewHolder newInstance(View view) {
            return new DisplayIngredientViewHolder(view);
        }

        @Override
        public View createView(LayoutInflater inflater, ViewGroup parent) {
            return inflater.inflate(R.layout.layout_list_item_ingredient, parent, false);
        }
    }
}
