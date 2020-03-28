package np.com.aanalbasaula.foodplanner.views.utils;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.NumberPicker;
import android.widget.TextView;

import np.com.aanalbasaula.foodplanner.R;

/**
 * The number picker dialog which allows selection of a number on dialog creation
 */
public class NumberPickerDialog extends DialogFragment {

    private static final String TAG = NumberPickerDialog.class.getSimpleName();
    private static final String ARG_MIN_VALUE = "minValue";
    private static final String ARG_MAX_VALUE = "maxValue";
    private static final String ARG_UNIT_STRING = "numberUnitString";

    // UI
    private NumberPicker numberPicker;
    private TextView numberUnit;

    // listeners
    private NumberPickerDialogListener listener;

    // data
    private int selectedValue;

    // settings
    @StringRes
    private int numberUnitString;
    private int minValue;
    private int maxValue;

    public static NumberPickerDialog getInstance(int minValue, int maxValue, @StringRes int numberUnitString) {
        NumberPickerDialog dialog = new NumberPickerDialog();

        Bundle args = new Bundle();
        args.putInt(ARG_MAX_VALUE, maxValue);
        args.putInt(ARG_MIN_VALUE, minValue);
        args.putInt(ARG_UNIT_STRING, numberUnitString);
        dialog.setArguments(args);

        return dialog;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        // extract the arguments from the
        if (getArguments() != null) {
            minValue = getArguments().getInt(ARG_MIN_VALUE);
            maxValue = getArguments().getInt(ARG_MAX_VALUE);
            numberUnitString = getArguments().getInt(ARG_UNIT_STRING);
        }

        // create the view
        LayoutInflater layoutInflater = getActivity().getLayoutInflater();
        View dialogView = layoutInflater.inflate(R.layout.dialog_number_picker, null);

        // gather views
        numberPicker = dialogView.findViewById(R.id.number_picker);
        numberUnit = dialogView.findViewById(R.id.number_unit);

        // setup view
        numberPicker.setMinValue(minValue);
        numberPicker.setMaxValue(maxValue);
        numberPicker.setWrapSelectorWheel(false);
        numberUnit.setText(numberUnitString);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Choose Value");
        builder.setMessage("Choose a number: ");
        builder.setView(dialogView);
        builder.setPositiveButton(R.string.text_okay, this::onValueAccepted);
        builder.setNegativeButton(R.string.text_cancel, null);
        return builder.create();
    }

    @Override
    public void onStart() {
        super.onStart();
        numberPicker.setValue(selectedValue);
    }

    /**
     * Set the listener for the number picker.
     *
     * @param listener the listener waiting for the number picker
     */
    public void setListener(NumberPickerDialogListener listener) {
        this.listener = listener;
    }

    /**
     * Set the unit string for the dialog box.
     *
     * @param value the String resource value to be used
     */
    public void setUnitString(@StringRes int value) {
        this.numberUnitString = value;
        if (numberUnit != null) {
            this.numberUnit.setText(value);
        }
    }

    /**
     * Update the minimum value of the number picker
     *
     * @param minValue the minimum value
     */
    public void setMinValue(int minValue) {
        this.minValue = minValue;

        if (numberPicker != null) {
            numberPicker.setMinValue(minValue);
        }
    }

    /**
     * Update the maximum value of the number picker
     *
     * @param maxValue the maximum value
     */
    public void setMaxValue(int maxValue) {
        this.maxValue = maxValue;

        if (numberPicker != null) {
            numberPicker.setMaxValue(maxValue);
        }
    }

    /**
     * Retrieve the selected value of the number picker.
     *
     * @return the selected value
     */
    public int getSelectedValue() {
        return selectedValue;
    }

    /**
     * Set the selection of the number picker.
     *
     * @param selectedValue the integer value that should be selected
     */
    public void setSelectedValue(int selectedValue) {
        this.selectedValue = selectedValue;
        if (numberPicker != null) {
            numberPicker.setValue(selectedValue);
        }
    }

    /**
     * The value has been accepted by the user
     *
     * @param dialogInterface the dialog that received the click
     * @param which the button that was clicked
     */
    private void onValueAccepted(DialogInterface dialogInterface, int which) {
        selectedValue = numberPicker.getValue();
        Log.i(TAG, "onValueAccepted: User Selected value: " + selectedValue);

        if (listener != null) {
            listener.onSelectionChanged(this);
        }
    }

    /**
     * A listener for the status of the number picker dialog
     */
    public interface NumberPickerDialogListener {

        /**
         * The user changed the value on the Number Picker.
         *
         * @param dialog the dialog which generated this event
         */
        void onSelectionChanged(NumberPickerDialog dialog);
    }
}
