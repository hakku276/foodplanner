package np.com.aanalbasaula.foodplanner.views.meal_courses;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import np.com.aanalbasaula.foodplanner.R;
import np.com.aanalbasaula.foodplanner.database.MealCourse;
import np.com.aanalbasaula.foodplanner.database.MealType;
import np.com.aanalbasaula.foodplanner.views.utils.MultiLevelViewSupport;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * {@link RecyclerView.Adapter} that can display a {@link np.com.aanalbasaula.foodplanner.database.MealCourse} and makes a call to the
 * specified {@link ShowAllMealCoursesFragment.ShowAllMealCoursesFragmentListener}.
 */
public class MealCourseViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    // Static definitions

    /**
     * The Item type that determines a specific Item in the total list is of type Date.
     * Used only to show multi-level recycler view
     */
    private static final int ITEM_TYPE_DATE = 0;

    /**
     * The Item type that determines a specific Item in the total list is of type Meal Type.
     * Used only to show multi-level recycler view
     */
    private static final int ITEM_TYPE_MEAL_TYPE = 1;

    /**
     * The Item type that determines a specific Item in the total list is of type Meal.
     * Used only to show multi-level recycler view
     */
    private static final int ITEM_TYPE_MEAL_COURSE = 2;

    // ordered list of items to show in the view
    @Nullable
    private List<MultiLevelViewSupport.Node> items;

    // listener to events within the recycler view
    private final ShowAllMealCoursesFragment.ShowAllMealCoursesFragmentListener mListener;

    /**
     * Create a view adapter, provided the items to display and the listener to the events
     * within the recycler view.
     *
     * @param items    the items to be displayed
     * @param listener the listener to events
     */
    MealCourseViewAdapter(@NonNull List<MealCourse> items, ShowAllMealCoursesFragment.ShowAllMealCoursesFragmentListener listener) {
        mListener = listener;

        // prepare the tree structure and order them as lists to be able to display
        prepareDataset(items);
    }

    /**
     * Prepare the list of Items provided, by first organizing them in a Tree structure (Date -> Meal Type -> Meal).
     * Then flattening it out into the class property {@linkplain #items} to be able to display them
     * in order later.
     *
     * @param meals the list of meals to be displayed in the view
     */
    private void prepareDataset(List<MealCourse> meals) {
        DateFormat dateFormat = SimpleDateFormat.getDateInstance();
        Map<String, Map<MealType, List<MealCourse>>> dataset = new HashMap<>();
        List<String> sortedKeys = new LinkedList<>();

        // convert all the meal courses into a map of day -> (meal types), (meal type) -> meals
        for (MealCourse meal :
                meals) {
            String dateString = dateFormat.format(meal.getDate());

            if (!dataset.containsKey(dateString)) {
                sortedKeys.add(dateString);
                dataset.put(dateString, new HashMap<>());
            }

            Map<MealType, List<MealCourse>> mealTypesPerDay = dataset.get(dateString);

            if (!mealTypesPerDay.containsKey(meal.getType())) {
                mealTypesPerDay.put(meal.getType(), new ArrayList<>());
            }

            List<MealCourse> mealsPerDayPerType = mealTypesPerDay.get(meal.getType());
            mealsPerDayPerType.add(meal);
        }

        items = convertToFlattenedTree(sortedKeys, dataset);
    }

    /**
     * Provided a Tree structure using maps, converts it into a flat list in ready to be displayed.
     * Utilizes the {@link MultiLevelViewSupport} to create the tree structure and then later flatten
     * it out as a list.
     *
     * @param sortedKeys The sorted list of keys for the root map in which order the view should be shown
     * @param dataset    the tree represented as a map. date -> meal type -> meal
     * @return the flat list of items to be shown on the UI
     */
    private List<MultiLevelViewSupport.Node> convertToFlattenedTree(List<String> sortedKeys, Map<String, Map<MealType, List<MealCourse>>> dataset) {
        MultiLevelViewSupport.RootElement root = new MultiLevelViewSupport.RootElement();

        // for all date entries in the map create nodes
        for (String date :
                sortedKeys) {

            MultiLevelViewSupport.Node dateNode = new MultiLevelViewSupport.Node(ITEM_TYPE_DATE, date);
            Map<MealType, List<MealCourse>> mealTypePerDate = dataset.get(date);

            // add all the meal courses as nodes within the date
            // using MealType.values() since the order of the Meal type should be defined
            for (MealType mealType :
                    MealType.values()) {

                // the day does not contain the meal type, continue with another type
                if (!mealTypePerDate.containsKey(mealType)) {
                    continue;
                }
                MultiLevelViewSupport.Node mealTypeNode = new MultiLevelViewSupport.Node(ITEM_TYPE_MEAL_TYPE, mealType);
                List<MealCourse> mealsPerTypePerDate = mealTypePerDate.get(mealType);

                // add all meals into the course
                for (MealCourse mealCourse :
                        mealsPerTypePerDate) {
                    MultiLevelViewSupport.Node mealCourseNode = new MultiLevelViewSupport.Node(ITEM_TYPE_MEAL_COURSE, mealCourse);
                    mealTypeNode.addChild(mealCourseNode);
                }

                dateNode.addChild(mealTypeNode);
            }

            root.addChildNode(dateNode);
        }
        return root.getChildElementsInOrder();
    }

    /**
     * Converts the Meal Type enum to a String displayable to the user
     *
     * @param type the type of meal
     * @return a string displayable to the user
     */
    private String mealTypeToDisplayString(MealType type) {
        String mealType = type.name();
        mealType = mealType.substring(0, 1) + mealType.substring(1).toLowerCase();
        return mealType;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = null;
        RecyclerView.ViewHolder holder = null;
        switch (viewType) {
            case ITEM_TYPE_DATE:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.layout_list_item_date_header, parent, false);
                holder = new DateViewHolder(view);
                break;
            case ITEM_TYPE_MEAL_TYPE:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.layout_list_item_meal_type_sub_header, parent, false);
                holder = new MealTypeViewHolder(view);
                break;
            case ITEM_TYPE_MEAL_COURSE:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.layout_list_item_meal_course, parent, false);
                holder = new MealCourseViewHolder(view);
                break;
        }
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, int position) {
        if (items != null) {

            MultiLevelViewSupport.Node item = items.get(position);

            switch (holder.getItemViewType()) {
                case ITEM_TYPE_DATE:
                    DateViewHolder dateViewHolder = (DateViewHolder) holder;
                    dateViewHolder.mItem = (String) item.getItem();
                    dateViewHolder.mContentView.setText(dateViewHolder.mItem);
                    break;
                case ITEM_TYPE_MEAL_TYPE:
                    MealTypeViewHolder mealTypeViewHolder = (MealTypeViewHolder) holder;
                    mealTypeViewHolder.mItem = (MealType) item.getItem();
                    mealTypeViewHolder.mContentView.setText(mealTypeToDisplayString(mealTypeViewHolder.mItem));
                    break;
                case ITEM_TYPE_MEAL_COURSE:
                    MealCourseViewHolder mealCourseViewHolder = (MealCourseViewHolder) holder;
                    mealCourseViewHolder.mItem = (MealCourse) item.getItem();
                    mealCourseViewHolder.mContentView.setText(mealCourseViewHolder.mItem.getName());
                    mealCourseViewHolder.mView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (null != mListener) {
                                // Notify the active callbacks interface (the activity, if the
                                // fragment is attached to one) that an item has been selected.
                            }
                        }
                    });
                    break;
            }
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (items == null) {
            return 0;
        }
        return items.get(position).getType();
    }

    @Override
    public int getItemCount() {
        if (items != null) {
            return items.size();
        }
        return 0;
    }

    /**
     * A View holder class to handle the list Item showing the Dates
     */
    class DateViewHolder extends RecyclerView.ViewHolder {
        final View mView;
        final TextView mContentView;
        String mItem;

        DateViewHolder(View view) {
            super(view);
            mView = view;
            mContentView = (TextView) view.findViewById(R.id.content);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }

    /**
     * A View holder class to handle the list Item showing the Meal Type
     */
    class MealTypeViewHolder extends RecyclerView.ViewHolder {
        final View mView;
        final TextView mContentView;
        MealType mItem;

        MealTypeViewHolder(View view) {
            super(view);
            mView = view;
            mContentView = (TextView) view.findViewById(R.id.content);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }

    /**
     * A View holder class to handle the list Item showing the Meal information
     */
    class MealCourseViewHolder extends RecyclerView.ViewHolder {
        final View mView;
        final TextView mContentView;
        MealCourse mItem;

        MealCourseViewHolder(View view) {
            super(view);
            mView = view;
            mContentView = (TextView) view.findViewById(R.id.content);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }
}
