package np.com.aanalbasaula.foodplanner.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;

public class BroadcastUtils {

    private static final String ACTION_PREFIX = "np.aanalbasaula.foodplanner.";
    public static final String ACTION_MEAL_CREATED = ACTION_PREFIX + "MealCreated";

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
     * A simple helper function to register a broadcast receiver to listen to action broadcasts. Do not
     * forget to deregister the listener when the context is detached or destroyed
     * using {@link #unregisterLocalBroadcastListener(Context, BroadcastReceiver)}
     *
     * @param context  the context of the application
     * @param receiver the receiver of the broadcast
     * @param action   the action to filter using
     */
    public static void registerLocalBroadcastListener(@NonNull final Context context, @NonNull final BroadcastReceiver receiver, @NonNull final String action) {
        LocalBroadcastManager manager = LocalBroadcastManager.getInstance(context);
        IntentFilter filter = new IntentFilter(action);
        manager.registerReceiver(receiver, filter);
    }

    /**
     * A simple helper function to unregister a broadcast receiver previously registered.
     * To register a broadcast listener, please refer {@link #registerLocalBroadcastListener(Context, BroadcastReceiver, String)}
     *
     * @param context the app context
     * @param receiver the receiver that should be unregistered
     */
    public static void unregisterLocalBroadcastListener(@NonNull final Context context, @NonNull final BroadcastReceiver receiver) {
        LocalBroadcastManager manager = LocalBroadcastManager.getInstance(context);
        manager.unregisterReceiver(receiver);
    }

}
