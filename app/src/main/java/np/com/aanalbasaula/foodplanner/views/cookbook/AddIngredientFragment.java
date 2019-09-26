package np.com.aanalbasaula.foodplanner.views.cookbook;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.LinkedList;
import java.util.List;

import np.com.aanalbasaula.foodplanner.R;
import np.com.aanalbasaula.foodplanner.database.Ingredient;

/**
 * A simple {@link Fragment} subclass that is helps adding ingredients to a recipe. It provides
 * the recently added items an {@link np.com.aanalbasaula.foodplanner.database.Ingredient} object
 * and never is responsible for the database interactions.
 * <p>
 * Activities that contain this fragment must implement the
 * {@link OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link AddIngredientFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AddIngredientFragment extends Fragment {

    private static final String TAG = AddIngredientFragment.class.getSimpleName();

    private RecyclerView recyclerView;
    private IngredientsViewAdapter ingredientsViewAdapter;
    private ImageButton buttonAdd;
    private EditText textIngredientName;
    private OnFragmentInteractionListener mListener;

    public AddIngredientFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment AddIngredientFragment.
     */
    public static AddIngredientFragment newInstance() {
        return new AddIngredientFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "onCreate: Add Ingredient fragment created");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Log.i(TAG, "onCreateView: Inflating fragment view");
        View view = inflater.inflate(R.layout.fragment_add_ingredient, container, false);

        // gather views
        recyclerView = view.findViewById(R.id.content);
        buttonAdd = view.findViewById(R.id.btn_add);
        textIngredientName = view.findViewById(R.id.text_name);

        // setup the recycler view
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        setIngredients(new LinkedList<>());

        // setup the add button
        buttonAdd.setOnClickListener(addButtonClickListener);

        // setup edit text
        textIngredientName.setOnEditorActionListener(editorDoneActionListener);

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnIngredientAddListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * Set the ingredients that this view will display as added ingredients
     * @param ingredients the ingredients to display
     */
    public void setIngredients(List<Ingredient> ingredients) {
        Log.i(TAG, "setIngredients: Resetting the list of ingredients to be displayed on screen");
        ingredientsViewAdapter = new IngredientsViewAdapter(ingredients, ingredientsChangedListener);
        recyclerView.setAdapter(ingredientsViewAdapter);
    }

    /**
     * Validates whether the input provided in the ingredient name is
     * valid or not. It automatically sets the error in the input edit text.
     *
     * @param ingredientName The input ingredient name
     * @return true if valid else false
     */
    private boolean isIngredientInputValid(String ingredientName) {

        if (ingredientName.isEmpty()) {
            textIngredientName.setError(getString(R.string.error_required));
            return false;
        }

        List<Ingredient> addedItems = ingredientsViewAdapter.getIngredients();

        for (Ingredient ingredient:
             addedItems) {
            if (ingredient.getName().equals(ingredientName)) {
                textIngredientName.setError(getString(R.string.error_already_exists));
                return false;
            }
        }

        return true;
    }

    /**
     * The click listener to add button
     */
    private View.OnClickListener addButtonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Log.i(TAG, "onClick: Add button clicked");
            // check if input available
            String ingredientName = textIngredientName.getText().toString();

            // validate the error. the method
            if (!isIngredientInputValid(ingredientName)) {
                Log.d(TAG, "onClick: The user input was invalid");
                return;
            }

            Log.i(TAG, "onClick: Adding ingredient to list with name: " + ingredientName);
            Ingredient ingredient = new Ingredient();
            ingredient.setName(ingredientName);

            Log.i(TAG, "onClick: notifying adapter on dataset changed");
            ingredientsViewAdapter.addIngredient(ingredient);
            ingredientsViewAdapter.notifyDataSetChanged();

            // clean up the edit text
            textIngredientName.setText("");
        }
    };

    /**
     * Listen to keyboard done presses
     */
    private EditText.OnEditorActionListener editorDoneActionListener = new TextView.OnEditorActionListener() {
        @Override
        public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
            Log.i(TAG, "onEditorAction: Editor has a key event" + keyEvent);
            if (textIngredientName.getText().length() == 0) {
                Log.i(TAG, "onEditorAction: The user has no input on the text. Closing the keyboard");
                // return false to let the system handle this action. Closes the keyboard
                return false;
            }
            // redirect calls to item add
            addButtonClickListener.onClick(textView);
            // return true to keep the keyboard open
            return true;
        }
    };

    /**
     * The Ingredients change listener on the adapter. This might be called when the user
     * removes the items from the list.
     */
    private IngredientsViewAdapter.IngredientListChangedListener ingredientsChangedListener = (ingredients) -> {
        Log.i(TAG, "onIngredientsChanged: The user has changed ingredients in the adapter");
        // notify the listener
        mListener.onIngredientsChanged(ingredients);
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

        /**
         * The list of ingredients was changed by the user.
         * @param ingredients the new list of ingredients
         */
        void onIngredientsChanged(List<Ingredient> ingredients);
    }
}
