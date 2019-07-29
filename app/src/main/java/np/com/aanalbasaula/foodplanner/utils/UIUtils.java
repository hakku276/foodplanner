package np.com.aanalbasaula.foodplanner.utils;

import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

/**
 * UI utility functions.
 */
public class UIUtils {

    private UIUtils() {
        // empty constructor to hide it
    }

    /**
     * Force the keyboard to open on the defined view
     *
     * @param context the context in which this interaction is to be performed
     * @param view    the view to show the keyboard for
     */
    public static void forceShowKeyboard(Context context, View view) {
        ((InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE)).showSoftInput(view, InputMethodManager.SHOW_FORCED);
    }

    /**
     * Hide the keyboard to from the defined view
     *
     * @param context the context where the action is to be performed
     * @param view    the view to hide the keyboard
     */
    public static void forceHideKeyboardFromView(Context context, View view) {
        ((InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.RESULT_UNCHANGED_SHOWN);
    }

}
