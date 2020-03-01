package np.com.aanalbasaula.foodplanner.views.cookbook;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import np.com.aanalbasaula.foodplanner.R;
import np.com.aanalbasaula.foodplanner.database.Ingredient;
import np.com.aanalbasaula.foodplanner.database.RecipeStep;

/**
 * A simple {@link Fragment} subclass that allows Editing or Display of Recipe Steps and Ingredients.
 * Use the {@link RecipeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RecipeFragment extends Fragment {

    private static final String TAG = RecipeFragment.class.getSimpleName();
    private static final String ARG_EDIT_MODE = "editMode";

    // UI
    private RecyclerView mIngredientsList;
    private RecyclerView mRecipeStepsList;

    @Nullable
    private GenericEditableListAdapter<Ingredient> mIngredientsAdapter;

    @Nullable
    private GenericEditableListAdapter<RecipeStep> mRecipeStepAdapter;


    // Data
    private List<Ingredient> ingredients;
    private List<RecipeStep> steps;
    private boolean editMode;

    public RecipeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment RecipeFragment.
     */
    public static RecipeFragment newInstance(boolean editMode) {
        RecipeFragment fragment = new RecipeFragment();
        Bundle args = new Bundle();
        args.putBoolean(ARG_EDIT_MODE, editMode);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            editMode = getArguments().getBoolean(ARG_EDIT_MODE);
        }

        Log.i(TAG, "onCreate: Creating Edit Recipe Fragment: selectedMode: " + editMode);
        ingredients = new LinkedList<>();
        steps = new LinkedList<>();

        mIngredientsAdapter = new GenericEditableListAdapter<>(ingredients, editMode,
                GenericEditableListAdapter.ItemFactories.INGREDIENTS,
                (inflater, parent) -> inflater.inflate(R.layout.layout_list_item_ingredient, parent, false));

        mRecipeStepAdapter = new GenericEditableListAdapter<>(steps, editMode,
                GenericEditableListAdapter.ItemFactories.RECIPE_STEPS,
                (inflater, parent) -> inflater.inflate(R.layout.layout_list_item_recipe_step, parent, false));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.i(TAG, "onCreateView: Creating Edit Recipe Fragment View");
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_edit_recipe, container, false);

        mIngredientsList = view.findViewById(R.id.list_ingredients);
        mIngredientsList.setLayoutManager(new LinearLayoutManager(getContext()));
        mIngredientsList.setAdapter(mIngredientsAdapter);

        mRecipeStepsList = view.findViewById(R.id.list_steps);
        mRecipeStepsList.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecipeStepsList.setAdapter(mRecipeStepAdapter);

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    /**
     * Get all the ingredients that are being edited by this view.
     *
     * @return the list of ingredients that the user input, never null
     */
    public List<Ingredient> getIngredients() {
        return ingredients;
    }

    /**
     * Set the ingredients being shown by this view
     *
     * @param ingredients the ingredients to be displayed
     */
    public void setIngredients(List<Ingredient> ingredients) {
        if (ingredients == null) {
            ingredients = new LinkedList<>();
        }

        Log.d(TAG, "setIngredients: Provided new ingredients: " + ingredients.size());
        this.ingredients = ingredients;

        if (mIngredientsAdapter != null) {
            mIngredientsAdapter.setItems(ingredients);
            mIngredientsAdapter.notifyDataSetChanged();
        }
    }

    /**
     * Get all the steps that are being edited by this view.
     *
     * @return the list of recipes that the user input, never null
     */
    public List<RecipeStep> getSteps() {
        return steps;
    }

    /**
     * Set the steps to be displayed in order. if the provided steps are not sorted in order, they will be sorted
     *
     * @param steps the list of steps to display
     */
    public void setSteps(List<RecipeStep> steps) {
        if (steps == null) {
            steps = new LinkedList<>();
        }

        Collections.sort(steps, (a, b) -> a.getOrder() - b.getOrder());
        this.steps = steps;

        if (mRecipeStepAdapter != null) {
            mRecipeStepAdapter.setItems(steps);
            mRecipeStepAdapter.notifyDataSetChanged();
        }
    }

}
