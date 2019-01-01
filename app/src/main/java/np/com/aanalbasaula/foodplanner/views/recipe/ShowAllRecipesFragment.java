package np.com.aanalbasaula.foodplanner.views.recipe;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
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
import np.com.aanalbasaula.foodplanner.database.utils.LoadRecipesAsync;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link ShowAllRecipesFragmentListener}
 * interface.
 */
public class ShowAllRecipesFragment extends Fragment implements LoadRecipesAsync.LoadRecipesListener{

    private static final String TAG = ShowAllRecipesFragment.class.getSimpleName();

    private ShowAllRecipesFragmentListener mListener;

    private List<Recipe> recipes;

    private RecyclerView recyclerView;

    private AppDatabase db;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ShowAllRecipesFragment() {
    }

    public static ShowAllRecipesFragment newInstance() {
        return new ShowAllRecipesFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        db = AppDatabase.getInstance(getContext());
        LoadRecipesAsync asyncTask = new LoadRecipesAsync(this.db.getRecipeDao(), this);
        asyncTask.execute();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recipe_list, container, false);

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            recyclerView = (RecyclerView) view;
            LinearLayoutManager layoutManager = new LinearLayoutManager(context);
            recyclerView.setLayoutManager(layoutManager);
        }
        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof ShowAllRecipesFragmentListener) {
            mListener = (ShowAllRecipesFragmentListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement ShowAllRecipesFragmentListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * Notify the fragment that a new recipe was added
     * @param recipe the added recipe
     */
    public void onNewRecipeAdded(Recipe recipe){
        this.recipes.add(recipe);
        this.recyclerView.getAdapter().notifyDataSetChanged();
    }

    @Override
    public void onRecipeLoaded(@NonNull List<Recipe> recipes) {
        Log.i(TAG, "onRecipeLoaded: Recipes have been loaded");
        this.recipes = recipes;
        recyclerView.setAdapter(new RecipeRecyclerViewAdapter(this.recipes, mListener));
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface ShowAllRecipesFragmentListener {
        /**
         * The user interacted on a specific recipe item
         * @param item the item which the user interacted with
         */
        void onListFragmentInteraction(Recipe item);
    }
}
