package np.com.aanalbasaula.foodplanner.utils;

import android.support.annotation.IdRes;
import android.support.annotation.StringRes;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.widget.TextView;

import np.com.aanalbasaula.foodplanner.R;

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

    /**
     * Adds a context Menu Entry to the provided context menu instance, when the context menu is
     * being displayed for a list item.
     *
     * @param contextMenu  the context menu instance
     * @param itemPosition the position of the item in the list
     * @param actionId     the action of the item
     * @param actionName   the string resource id that points to the name of the action
     */
    public static void addContextMenuEntryForListItem(ContextMenu contextMenu, int itemPosition, @IdRes int actionId, @StringRes int actionName) {
        contextMenu.add(itemPosition, actionId, 0, actionName);
    }

    /**
     * Get the action id of the menu item within a context menu. Please make sure to use this method
     * only with menu items created using the {@linkplain #addContextMenuEntryForListItem(ContextMenu, int, int, int)}.
     *
     * @param menuItem the menu item under consideration
     * @return the action id.
     */
    public static int getActionIdForContextMenuItem(MenuItem menuItem) {
        return menuItem.getItemId();
    }

    /**
     * Get the position of the item for which this context menu item was created. Please make sure to use this method
     * only with menu items created using the {@linkplain #addContextMenuEntryForListItem(ContextMenu, int, int, int)}.
     *
     * @param menuItem the menu item under consideration
     * @return the item position for which the menu item is related to
     */
    public static int getPositionForContextMenuItem(MenuItem menuItem) {
        return menuItem.getGroupId();
    }

}
