package np.com.aanalbasaula.foodplanner.utils;

import android.widget.TextView;

/**
 * UI utility functions.
 */
public class UIUtils {

    private UIUtils() {
        // empty constructor to hide it
    }

    /**
     * Adds a paint flag to the provided Text View.
     *
     * @param view      the view to which the paint flag is supposed to be added into
     * @param paintFlag the flag to be added. refer {@link android.graphics.Paint}
     */
    public static void addPaintFlagToTextView(TextView view, int paintFlag) {
        view.setPaintFlags(view.getPaintFlags() | paintFlag);
    }

    /**
     * Removes a paint flag from the provided text view.
     *
     * @param view      The view to remove the flag from
     * @param paintFlag the flag to remove
     */
    public static void removePaintFlatFromTextView(TextView view, int paintFlag) {
        view.setPaintFlags(view.getPaintFlags() & (~paintFlag));
    }

}
