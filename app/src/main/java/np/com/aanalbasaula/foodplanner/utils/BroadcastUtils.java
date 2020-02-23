package np.com.aanalbasaula.foodplanner.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;

public class BroadcastUtils {

    // payload related constants
    private static final String KEY_PAYLOAD = "payload";

    // actions related constants
    private static final String ACTION_PREFIX = "np.com.aanalbasaula.foodplanner.";

    // meal requests
    public static final String ACTION_MEAL_REQUEST_EDIT = ACTION_PREFIX + "request.MealEdit";
    public static final String ACTION_MEAL_REQUEST_DELETE = ACTION_PREFIX + "request.MealDelete";

    // meal updates
    public static final String ACTION_MEAL_UPDATED = ACTION_PREFIX + "MealUpdated";
    public static final String ACTION_MEAL_CREATED = ACTION_PREFIX + "MealCreated";

    // recipe requests
    public static final String ACTION_RECIPE_REQUEST_PLAN = ACTION_PREFIX + "request.RecipePlan";
    public static final String ACTION_RECIPE_REQUEST_EDIT = ACTION_PREFIX + "request.RecipeEdit";
    public static final String ACTION_RECIPE_REQUEST_DELETE = ACTION_PREFIX + "request.RecipeDelete";

    // recipe updates
    public static final String ACTION_RECIPE_CREATED = ACTION_PREFIX + "RecipeCreated";
    public static final String ACTION_RECIPE_UPDATED = ACTION_PREFIX + "RecipeUpdated";
    public static final String ACTION_RECIPE_DELETED = ACTION_PREFIX + "RecipeDeleted";

    // shopping list item updates
    public static final String ACTION_SHOPPING_LIST_ENTRY_CREATED = ACTION_PREFIX + "ShoppingListEntryCreated";
    public static final String ACTION_SHOPPING_LIST_ENTRY_DELETED = ACTION_PREFIX + "ShoppingListEntryDeleted";

    /**
     * Send a local broadcast using the provided context for the defined action.
     * Please refer to predefined actions defined in {@link BroadcastUtils}, example
     * {@linkplain #ACTION_MEAL_CREATED}. Consider using these rather than any random
     * string value.
     *
     * @param context The application context. Not null
     * @param action  The string action defining the broadcast
     */
    public static void sendLocalBroadcast(@NonNull final Context context, @NonNull final String action) {
        LocalBroadcastManager manager = LocalBroadcastManager.getInstance(context);
        Intent intent = new Intent(action);
        manager.sendBroadcast(intent);
    }

    /**
     * Send a local broadcast using the provided context for the defined action, along with a specific payload.
     * The payload should be {@linkplain Parcelable} inorder to be delivered with the intent.
     *
     * @param context the application context, Not Null
     * @param action  the String action defining the broadcast, Not Null Not Empty
     * @param payload the payload sent with the broadcast, Not null
     * @param <T>     the payload type which must be parcelable
     */
    public static <T extends Parcelable> void sendLocalBroadcast(@NonNull final Context context, @NonNull final String action, @NonNull T payload) {
        LocalBroadcastManager manager = LocalBroadcastManager.getInstance(context);
        Intent intent = new Intent(action);
        intent.putExtra(KEY_PAYLOAD, payload);
        manager.sendBroadcast(intent);
    }

    /**
     * Extract the payload from the intent if available.
     *
     * @param intent the intent for which the payload should be extracted
     * @param <T>    the type of the payload which is expected
     * @return the payload if available, else null
     */
    public static <T extends Parcelable> T extractPayload(Intent intent) {

        if (!intent.hasExtra(KEY_PAYLOAD)) {
            return null;
        }

        return intent.getParcelableExtra(KEY_PAYLOAD);
    }

    /**
     * A simple helper function to register a broadcast receiver to listen to action broadcasts. Do not
     * forget to deregister the listener when the context is detached or destroyed
     * using {@link #unregisterLocalBroadcastListener(Context, BroadcastReceiver)}
     *
     * @param context  the context of the application
     * @param receiver the receiver of the broadcast
     * @param actions   the actions to filter using
     */
    public static void registerLocalBroadcastListener(@NonNull final Context context, @NonNull final BroadcastReceiver receiver, @NonNull final String... actions) {
        LocalBroadcastManager manager = LocalBroadcastManager.getInstance(context);
        IntentFilter filter = new IntentFilter();
        for (String action :
                actions) {
            filter.addAction(action);
        }
        manager.registerReceiver(receiver, filter);
    }

    /**
     * A simple helper function to unregister a broadcast receiver previously registered.
     * To register a broadcast listener, please refer {@link #registerLocalBroadcastListener(Context, BroadcastReceiver, String...)}
     *
     * @param context the app context
     * @param receiver the receiver that should be unregistered
     */
    public static void unregisterLocalBroadcastListener(@NonNull final Context context, @NonNull final BroadcastReceiver receiver) {
        LocalBroadcastManager manager = LocalBroadcastManager.getInstance(context);
        manager.unregisterReceiver(receiver);
    }

}
