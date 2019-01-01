package np.com.aanalbasaula.foodplanner;

import android.app.Application;
import android.util.Log;

import np.com.aanalbasaula.foodplanner.database.AppDatabase;

public class FoodPlanner extends Application {

    private static final String TAG = FoodPlanner.class.getSimpleName();

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "onCreate: Creating new application instance");
        Log.i(TAG, "onCreate: Initializing the database");
        AppDatabase.getInstance(this);
    }
}
