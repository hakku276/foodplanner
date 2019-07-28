package np.com.aanalbasaula.foodplanner.views.cookbook;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import np.com.aanalbasaula.foodplanner.R;
import np.com.aanalbasaula.foodplanner.database.AppDatabase;
import np.com.aanalbasaula.foodplanner.database.Recipe;
import np.com.aanalbasaula.foodplanner.database.RecipeDao;
import np.com.aanalbasaula.foodplanner.database.utils.EntryCreationStrategies;
import np.com.aanalbasaula.foodplanner.database.utils.EntryCreator;
import np.com.aanalbasaula.foodplanner.utils.BroadcastUtils;

/**
 * A simple dialog box to create a recipe.
 */
public class CreateRecipeDialogFragment extends DialogFragment {

    private static final String TAG = CreateRecipeDialogFragment.class.getSimpleName();

    // View related properties
    private EditText textRecipeName;
    private AlertDialog dialog;

    // Database properties
    private RecipeDao recipeDao;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View bodyView = inflateLayout();
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        // initialize the DAO
        recipeDao = AppDatabase.getInstance(getActivity()).getRecipeDao();

        // to prevent auto close on button press, set listeners to null.
        builder.setView(bodyView)
                .setTitle(R.string.title_dialog_create_recipe)
                .setPositiveButton(R.string.button_create, null)
                .setNegativeButton(R.string.button_cancel, null);

        dialog = builder.create();

        // add button listener on the dialog to listen to button press
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                Button button = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
                button.setOnClickListener(addRecipeButtonListener);
            }
        });

        return dialog;
    }

    /**
     * Infaltes the Dialog fragment body layout and also populates the
     * class properties related to the view.
     *
     * @return the root body view
     */
    private View inflateLayout() {
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_create_recipe, null);

        // get view components
        textRecipeName = view.findViewById(R.id.text_name);

        return view;
    }

    /**
     * Validates whether the form input was correct or not.
     *
     * @return returns false if invalid. In case of a false, the process should not move forward
     */
    private boolean isValid() {
        return textRecipeName.getText().length() != 0;
    }

    /**
     * A custom button click listener instead of the Dialog button click listener to:
     * <ol>
     *     <li>Prevent auto dismiss of dialog on user positive click (needed for UI validations)</li>
     *     <li>Dismiss the dialog after item successfully written in the database</li>
     * </ol>
     */
    private View.OnClickListener addRecipeButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Log.i(TAG, "onClick: Add recipe button clicked");
            if (!isValid()) {
                textRecipeName.setError(getString(R.string.error_required));
                return;
            }

            // construct recipe object to save
            Recipe recipe = new Recipe();
            recipe.setName(textRecipeName.getText().toString());

            EntryCreator<RecipeDao, Recipe> creator = new EntryCreator<>(recipeDao, EntryCreationStrategies.recipeCreationStrategy, recipeEntryCreationListener);
            creator.execute(recipe);
        }
    };

    /**
     * The listener to database write async task.
     */
    private EntryCreator.EntryCreationListener<Recipe> recipeEntryCreationListener = new EntryCreator.EntryCreationListener<Recipe>() {
        @Override
        public void onEntriesCreated(Recipe[] items) {
            Log.i(TAG, "onEntriesCreated: Entry successfully created in database");
            BroadcastUtils.sendLocalBroadcast(getContext(), BroadcastUtils.ACTION_RECIPE_CREATED);
            dialog.dismiss();
        }
    };
}
