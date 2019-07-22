package np.com.aanalbasaula.foodplanner.views.meal_courses;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;

import np.com.aanalbasaula.foodplanner.R;
import np.com.aanalbasaula.foodplanner.database.AppDatabase;
import np.com.aanalbasaula.foodplanner.database.MealCourse;
import np.com.aanalbasaula.foodplanner.database.MealCourseDao;
import np.com.aanalbasaula.foodplanner.database.MealType;
import np.com.aanalbasaula.foodplanner.database.utils.EntryCreationStrategies;
import np.com.aanalbasaula.foodplanner.database.utils.EntryCreator;
import np.com.aanalbasaula.foodplanner.utils.BroadcastUtils;
import np.com.aanalbasaula.foodplanner.utils.DateUtils;


/**
 * A Dialog box that is displayed when the user wants to plan a meal.
 */
public class PlanMealDialogFragment extends DialogFragment {

    private static final String TAG = PlanMealDialogFragment.class.getSimpleName();

    // View related properties
    private EditText textMealName;
    private Spinner enumMealType;
    private DatePicker datePicker;
    private AlertDialog dialog;

    // Data access related properties
    private MealCourseDao mealCourseDao;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View bodyView = inflateLayout();
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        // initialization of DAO
        mealCourseDao = AppDatabase.getInstance(getActivity()).getMealCourseDao();

        builder.setView(bodyView)
                .setTitle(R.string.title_dialog_add_meal)
                .setPositiveButton(R.string.button_add, null)
                .setNegativeButton(R.string.button_cancel, null);

        dialog = builder.create();

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                Button button = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
                button.setOnClickListener(new AddMealButtonListener());
            }
        });

        return dialog;
    }

    /**
     * Inflates the dialog layout and also initializes the View Properties of this class
     * @return the view that was recently inflated
     */
    private View inflateLayout() {
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_plan_meal, null);

        // setup the enumMealType with the appropriate data
        enumMealType =  view.findViewById(R.id.enum_meal_type);
        ArrayAdapter<CharSequence> mealTypesAdapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.meal_types, android.R.layout.simple_spinner_dropdown_item);
        enumMealType.setAdapter(mealTypesAdapter);

        // get the other view components
        textMealName = view.findViewById(R.id.text_name);
        datePicker = view.findViewById(R.id.date_picker);
        datePicker.setMinDate(System.currentTimeMillis());

        return view;
    }

    /**
     * Check whether the user input is valid or not
     */
    private boolean isInputValid() {
        String name = textMealName.getText().toString();
        if (name.isEmpty()) {
            textMealName.setError(getString(R.string.error_required));
            return false;
        }

        return true;
    }

    /**
     * Extract the meal type enum from the drop down list
     * @return the extracted meal type enum instance
     */
    private MealType extractMealType() {
        String value = (String) enumMealType.getSelectedItem();
        return MealType.valueOf(value.toUpperCase());
    }

    /**
     * A button click listener to listen to
     */
    private class AddMealButtonListener implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            Log.i(TAG, "onClick: Add meal button clicked");
            if(isInputValid()) {
                Log.i(TAG, "onClick: The input was valid. Proceeding with meal entry creation");
                MealCourse mealCourse = new MealCourse();
                mealCourse.setDate(DateUtils.extractDateFromPicker(datePicker));
                mealCourse.setName(textMealName.getText().toString());
                mealCourse.setType(extractMealType());
                Log.d(TAG, "onClick: Extracted meal information : " + mealCourse.toString());

                // start creation async task
                EntryCreator<MealCourseDao, MealCourse> entryCreator = new EntryCreator<>(mealCourseDao, EntryCreationStrategies.mealCourseCreationStrategy, mealCourseCreationListener);
                entryCreator.execute(mealCourse);
            }
        }
    }

    private EntryCreator.EntryCreationListener<MealCourse> mealCourseCreationListener = new EntryCreator.EntryCreationListener<MealCourse>() {
        @Override
        public void onEntriesCreated(MealCourse[] items) {
            Log.i(TAG, "onEntriesCreated: Meal Course Entry was successfully created.");
            // send out broadcast that a meal was created
            BroadcastUtils.sendLocalBroadcast(getContext(), BroadcastUtils.ACTION_MEAL_CREATED);
            dialog.dismiss();
        }
    };
}
