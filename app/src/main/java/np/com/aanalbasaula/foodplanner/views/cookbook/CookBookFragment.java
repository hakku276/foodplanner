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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import np.com.aanalbasaula.foodplanner.R;
import np.com.aanalbasaula.foodplanner.database.AppDatabase;
import np.com.aanalbasaula.foodplanner.database.Recipe;
import np.com.aanalbasaula.foodplanner.database.RecipeDao;
import np.com.aanalbasaula.foodplanner.database.utils.DatabaseLoader;
import np.com.aanalbasaula.foodplanner.utils.BroadcastUtils;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link CookBookFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link CookBookFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CookBookFragment extends Fragment {

    private static final String TAG = CookBookFragment.class.getSimpleName();

    // ui related
    private RecyclerView recyclerView;
    private FloatingActionButton createRecipeButton;
    private OnFragmentInteractionListener mListener;

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
        createRecipeButton = view.findViewById(R.id.btn_create_recipe);

        // setup the recycler view
        Context context = view.getContext();
        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(layoutManager);

        // setup the floating action button
        createRecipeButton.setOnClickListener(createButtonClickListener);

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;

            // register recipe creation broadcast listener
            BroadcastUtils.registerLocalBroadcastListener(context, recipeCreationBroadcastReceiver, BroadcastUtils.ACTION_RECIPE_CREATED);
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;

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
    private BroadcastReceiver recipeCreationBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i(TAG, "onReceive: New recipe has been created");
            loadItemsFromDatabaseAsync();
        }
    };

    /**
     * The listener to database load events. The load is initiated by the {@linkplain #loadItemsFromDatabaseAsync()}
     */
    private DatabaseLoader.DatabaseLoadListener<Recipe> databaseLoadListener = new DatabaseLoader.DatabaseLoadListener<Recipe>() {
        @Override
        public void onItemsLoaded(@NonNull List<Recipe> items) {
            Log.i(TAG, "onItemsLoaded: Successfully loaded recipes");

            CookbookViewAdapter adapter = new CookbookViewAdapter(items);
            recyclerView.setAdapter(adapter);
        }
    };

    /**
     * A create recipe button listener to initiate create recipe workflow.
     */
    private View.OnClickListener createButtonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Log.i(TAG, "onClick: Create Recipe button clicked");
            CreateRecipeDialogFragment dialogFragment = new CreateRecipeDialogFragment();
            dialogFragment.show(getFragmentManager(), "recipe");
        }
    };

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}
