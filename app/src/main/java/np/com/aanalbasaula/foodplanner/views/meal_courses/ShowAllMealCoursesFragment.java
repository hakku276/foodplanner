package np.com.aanalbasaula.foodplanner.views.meal_courses;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import np.com.aanalbasaula.foodplanner.R;
import np.com.aanalbasaula.foodplanner.database.AppDatabase;
import np.com.aanalbasaula.foodplanner.database.MealCourse;
import np.com.aanalbasaula.foodplanner.database.MealCourseDao;
import np.com.aanalbasaula.foodplanner.database.utils.DatabaseLoader;
import np.com.aanalbasaula.foodplanner.utils.BroadcastUtils;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link ShowAllMealCoursesFragmentListener}
 * interface.
 */
public class ShowAllMealCoursesFragment extends Fragment {

    private static final String TAG = ShowAllMealCoursesFragment.class.getSimpleName();

    // database related
    private AppDatabase db;

    // ui related
    private RecyclerView recyclerView;
    private ShowAllMealCoursesFragmentListener mListener;
    private MealCourseViewAdapter mealCourseViewAdapter;

    public ShowAllMealCoursesFragment() {
        // Required empty public constructor
    }

    public static ShowAllMealCoursesFragment newInstance() {
        return new ShowAllMealCoursesFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        db = AppDatabase.getInstance(getContext());
    }

    @Override
    public void onStart() {
        super.onStart();
        loadItemsFromDatabaseAsync();
        registerForContextMenu(recyclerView);
    }

    @Override
    public void onStop() {
        super.onStop();
        unregisterForContextMenu(recyclerView);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_meal_courses_list, container, false);

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            recyclerView = (RecyclerView) view;
            LinearLayoutManager layoutManager = new LinearLayoutManager(context);
            recyclerView.setLayoutManager(layoutManager);
        }
        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof ShowAllMealCoursesFragmentListener) {
            mListener = (ShowAllMealCoursesFragmentListener) context;

            // register for meal creation broadcast
            BroadcastUtils.registerLocalBroadcastListener(context,
                    mealCreationBroadcastListener,
                    BroadcastUtils.ACTION_MEAL_CREATED);
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement ShowAllMealCoursesFragmentListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;

        BroadcastUtils.unregisterLocalBroadcastListener(getContext(), mealCreationBroadcastListener);
    }

    /**
     * A callback listener for database load event. This listener is triggered when the async task
     * completes database load.
     */
    DatabaseLoader.DatabaseLoadListener<MealCourse> databaseLoadListener = new DatabaseLoader.DatabaseLoadListener<MealCourse>() {
        @Override
        public void onItemsLoaded(@NonNull List<MealCourse> items) {
            Log.i(TAG, "onItemsLoaded: Meal Courses have been successfully loaded");
            if (mealCourseViewAdapter == null) {
                mealCourseViewAdapter = new MealCourseViewAdapter(items, mListener);
                recyclerView.setAdapter(mealCourseViewAdapter);
            } else {
                mealCourseViewAdapter.setItems(items);
            }
        }
    };

    /**
     * Initiate load for all the items within the database
     */
    private void loadItemsFromDatabaseAsync() {
        DatabaseLoader<MealCourseDao, MealCourse> asyncTask = new DatabaseLoader<>(db.getMealCourseDao(),
                MealCourseDao::getAllMealCoursesInFuture,
                databaseLoadListener);
        asyncTask.execute();
    }

    /**
     * Broadcast listener to any meal creation that has recently happened.
     * To reload the view as required
     */
    BroadcastReceiver mealCreationBroadcastListener = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i(TAG, "onReceive: Meal Creation broadcast received. Reloading data");
            loadItemsFromDatabaseAsync();
            Log.i(TAG, "onReceive: Started async task to load meal courses");
        }
    };

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        Log.i(TAG, "onContextItemSelected: A Context Item was selected. At Position: " + item.getGroupId());
        // item group id has been set to position within the adapter
        MealCourse mealCourse = mealCourseViewAdapter.getMealCourseAtPosition(item.getGroupId());
        if (mealCourse != null) {
            switch (item.getItemId()) {
                case R.id.action_delete:
                    break;
                case R.id.action_edit:
                    Log.d(TAG, "onContextItemSelected: Context Menu Edit selected for item at position: " + item.getGroupId());
                    PlanMealDialogFragment planMealDialog = new PlanMealDialogFragment();
                    planMealDialog.show(requireFragmentManager(), "plan-meal");
                    break;
            }
        }
        return true;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface ShowAllMealCoursesFragmentListener {
        /**
         * The user interacted on a specific recipe item
         * @param item the item which the user interacted with
         */
        void onListFragmentInteraction(MealCourse item);
    }
}
