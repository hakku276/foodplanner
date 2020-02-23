package np.com.aanalbasaula.foodplanner.utils;

import android.content.Context;
import android.os.ParcelUuid;
import android.support.annotation.MenuRes;
import android.support.v7.widget.PopupMenu;
import android.view.MenuInflater;
import android.view.View;

import np.com.aanalbasaula.foodplanner.R;

/**
 * A short hand utility method collection for PopMenu Handling.
 */
public class PopupMenuUtils {

    /**
     * Show a popup menu on the target view
     *
     * @param context               the context of the application which is required to make View changes
     * @param target                the view which will be used as a target to display the popup menu
     * @param menuId                the id of the menu defined in the resources
     * @param menuItemClickListener the click handler for menu presses
     * @return the newly created popup menu
     */
    public static PopupMenu show(Context context, View target, @MenuRes int menuId, PopupMenu.OnMenuItemClickListener menuItemClickListener) {

        PopupMenu popup = new PopupMenu(context, target);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(menuId, popup.getMenu());
        popup.setOnMenuItemClickListener(menuItemClickListener);
        popup.show();

        return popup;
    }
}
