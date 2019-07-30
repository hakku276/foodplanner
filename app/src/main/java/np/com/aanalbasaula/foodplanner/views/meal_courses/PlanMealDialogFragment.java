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

import java.util.Calendar;

import np.com.aanalbasaula.foodplanner.R;
import np.com.aanalbasaula.foodplanner.database.AppDatabase;
import np.com.aanalbasaula.foodplanner.database.MealCourse;
import np.com.aanalbasaula.foodplanner.database.MealCourseDao;
import np.com.aanalbasaula.foodplanner.database.MealType;
import np.com.aanalbasaula.foodplanner.database.utils.EntryCreationStrategies;
import np.com.aanalbasaula.foodplanner.database.utils.EntryCreator;
import np.com.aanalbasaula.foodplanner.database.utils.EntryUpdater;
import np.com.aanalbasaula.foodplanner.utils.BroadcastUtils;
import np.com.aanalbasaula.foodplanner.utils.DateUtils;


/**
 * A Dialog box that is displayed when the user wants to plan a meal.
 */
public class PlanMealDialogFragment extends DialogFragment {

    private static final String TAG = PlanMealDialogFragment.class.getSimpleName();
    private static final String ARG_MEAL_COURSE = "meal_course";
    private static final String ARG_MEAL_NAME = "meal_name";

    // View related properties
    private EditText textMealName;
    private Spinner enumMealType;
    private DatePicker datePicker;
    private AlertDialog dialog;

    // Data access related properties
    private boolean isEditMode;
    private MealCourse mealCourse; // edit mode meal course
    private MealCourseDao mealCourseDao;

    public static PlanMealDialogFragment build(MealCourse mealCourse) {
        PlanMealDialogFragment mealDialogFragment = new PlanMealDialogFragment();

        Bundle bundle = new Bundle();
        bundle.putParcelable(ARG_MEAL_COURSE, mealCourse);

        mealDialogFragment.setArguments(bundle);

        return mealDialogFragment;
    }

    public static PlanMealDialogFragment build(String mealName) {
        PlanMealDialogFragment mealDialogFragment = new PlanMealDialogFragment();

        Bundle bundle = new Bundle();
        bundle.putString(ARG_MEAL_NAME, mealName);

        mealDialogFragment.setArguments(bundle);

        return mealDialogFragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Log.i(TAG, "onCreateDialog: Creating Dialog. Inflating views");
        View bodyView = inflateLayout();
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        // try and setup modes
        setupCreateWithArgs(getArguments());
        setupEditMode(getArguments());

        // initialization of DAO
        mealCourseDao = AppDatabase.getInstance(getActivity()).getMealCourseDao();

        builder.setView(bodyView)
                .setNegativeButton(R.string.button_cancel, null);

        if (isEditMode){
            builder.setTitle(R.string.title_dialog_update_meal)
                    .setPositiveButton(R.string.button_update, null);
        } else {
            builder.setTitle(R.string.title_dialog_add_meal)
                    .setPositiveButton(R.string.button_add, null);
        }

        dialog = builder.create();

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                Button button = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
                button.setOnClickListener(new DialogPositiveButtonListener());
            }
        });

        return dialog;
    }

    /**
     * Sets up the dialog to initialize with the provided arguments in create mode.
     *
     * @param arguments the arguments provided to the fragment
     */
    private void setupCreateWithArgs(Bundle arguments) {
        Log.i(TAG, "setupCreateWithArgs: Setting up create mode with the provided arguments");
        if (arguments != null && arguments.containsKey(ARG_MEAL_NAME)) {
            Log.d(TAG, "setupCreateWithArgs: Arguments available for use");
            String mealName = arguments.getString(ARG_MEAL_NAME);
            isEditMode = false;

            // set the meal name to the edit text
            textMealName.setText(mealName);
        }
    }

    /**
     * Sets up the edit mode if possible using the argument provided to the fragment. Else
     * the Create mode is enabled by default
     *
     * @param arguments the argument provided to the fragment
     */
    private void setupEditMode(Bundle arguments) {
        Log.i(TAG, "setupEditMode: Setting up edit mode in case Arguments available");
        if (arguments != null && arguments.containsKey(ARG_MEAL_COURSE)) {
            Log.d(TAG, "setupEditMode: Arguments available");
            mealCourse = arguments.getParcelable(ARG_MEAL_COURSE);

            if (mealCourse != null) {
                Log.d(TAG, "setupEditMode: Meal course not empty");
                isEditMode = true;

                // initialize the views
                textMealName.setText(mealCourse.getName());
                enumMealType.setSelection(getPositionForMealType(mealCourse.getType()));

                Calendar calendar = Calendar.getInstance();
                calendar.setTime(mealCourse.getDate());
                datePicker.updateDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
            }
        }
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
     * Get the position of the meal type within the enum
     * @param type the type of meal
     * @return the int position
     */
    private int getPositionForMealType(MealType type) {
        MealType[] types = MealType.values();
        for (int i = 0; i < types.length; i++) {
            if (types[i] == type) {
                return i;
            }
        }
        throw new IllegalArgumentException("Invalid Meal Type");
    }

    /**
     * Update the provided meal course object with the values from the UI
     * @param mealCourse the provided meal course
     */
    private void updateMealCourseFromUi(MealCourse mealCourse) {
        mealCourse.setDate(DateUtils.extractDateFromPicker(datePicker));
        mealCourse.setName(textMealName.getText().toString());
        mealCourse.setType(extractMealType());
    }

    /**
     * A button click listener to listen to positive actions. Either create or update
     */
    private class DialogPositiveButtonListener implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            Log.i(TAG, "onClick: Add meal button clicked");
            if(isInputValid()) {
                Log.d(TAG, "onClick: The input was valid.");
                if (isEditMode) {
                    Log.i(TAG, "onClick: Edit mode detected. Updating meal course entry in database");
                    updateMealCourseFromUi(mealCourse);
                    EntryUpdater<MealCourseDao, MealCourse> entryUpdater = new EntryUpdater<>(mealCourseDao, MealCourseDao::update, mealCourseUpdateListener);
                    entryUpdater.execute(mealCourse);
                } else {
                    mealCourse = new MealCourse();
                    updateMealCourseFromUi(mealCourse);
                    Log.d(TAG, "onClick: Extracted meal information : " + mealCourse.toString());

                    // start creation async task
                    EntryCreator<MealCourseDao, MealCourse> entryCreator = new EntryCreator<>(mealCourseDao, EntryCreationStrategies.mealCourseCreationStrategy, mealCourseCreationListener);
                    entryCreator.execute(mealCourse);
                }
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

    private EntryUpdater.DatabaseUpdateListener<MealCourse> mealCourseUpdateListener = new EntryUpdater.DatabaseUpdateListener<MealCourse>() {
        @Override
        public void onItemsUpdated() {
            Log.i(TAG, "onItemsUpdated: Entry successfully updated");

            // send out the broadcast that meal database has changed
            BroadcastUtils.sendLocalBroadcast(getContext(), BroadcastUtils.ACTION_MEAL_UPDATED);
            dialog.dismiss();
        }
    };
}
