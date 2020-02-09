package np.com.aanalbasaula.foodplanner.views.meal_courses;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.scalified.tree.TraversalAction;
import com.scalified.tree.TreeNode;
import com.scalified.tree.multinode.ArrayMultiTreeNode;
import com.scalified.tree.multinode.LinkedMultiTreeNode;
import com.scalified.tree.multinode.MultiTreeNode;

import np.com.aanalbasaula.foodplanner.R;
import np.com.aanalbasaula.foodplanner.database.MealCourse;
import np.com.aanalbasaula.foodplanner.database.MealType;
import np.com.aanalbasaula.foodplanner.utils.UIUtils;
import np.com.aanalbasaula.foodplanner.views.utils.MultiLevelViewNode;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * {@link RecyclerView.Adapter} that can display a {@link np.com.aanalbasaula.foodplanner.database.MealCourse} and makes a call to the
 * specified {@link ShowAllMealCoursesFragment.ShowAllMealCoursesFragmentListener}.
 */
class MealCourseViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String TAG = MealCourseViewAdapter.class.getSimpleName();

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
    @Deprecated
    private static final int ITEM_TYPE_MEAL_TYPE = 1;

    /**
     * The Item type that determines a specific Item in the total list is of type Meal.
     * Used only to show multi-level recycler view
     */
    private static final int ITEM_TYPE_MEAL_COURSE = 2;

    // ordered list of items to show in the view
    @Nullable
    private List<MultiLevelViewNode> items;

    // listener to events within the recycler view
    private final ShowAllMealCoursesFragment.ShowAllMealCoursesFragmentListener mListener;

    private final Context context;

    /**
     * Create a view adapter, provided the items to display and the listener to the events
     * within the recycler view.
     *
     * @param items    the items to be displayed
     * @param context  the context of the application
     * @param listener the listener to events
     */
    MealCourseViewAdapter(@NonNull List<MealCourse> items, Context context,ShowAllMealCoursesFragment.ShowAllMealCoursesFragmentListener listener) {
        mListener = listener;
        this.context = context;

        // prepare the tree structure and order them as lists to be able to display
        prepareDataset(items);
    }

    /**
     * Prepare the list of Items provided, by first organizing them in a Tree structure (Date -> Meal).
     * Then flattening it out into the class property {@linkplain #items} to be able to display them
     * in order later.
     *
     * @param meals the list of meals to be displayed in the view
     */
    private void prepareDataset(List<MealCourse> meals) {
        DateFormat dateFormat = new SimpleDateFormat("EEE, MMM dd, yyyy", Locale.US);
        TreeNode<MultiLevelViewNode> dataset = new LinkedMultiTreeNode<>(null);

        Log.i(TAG, "prepareDataset: Constructing Tree from Meal Courses");
        // convert all the meal courses into a tree of root-> days -> meal types -> meals
        for (MealCourse meal : meals) {
            String dateString = dateFormat.format(meal.getDate());

            TreeNode<MultiLevelViewNode> dateNode = dataset.find(new MultiLevelViewNode(ITEM_TYPE_DATE, dateString));

            if (dateNode == null) {
                Log.d(TAG, "prepareDataset: Date Node not found in dataset: dateString: " + dateString);
                dateNode = new LinkedMultiTreeNode<>(new MultiLevelViewNode(ITEM_TYPE_DATE, dateString));
                dataset.add(dateNode);
            }

            TreeNode<MultiLevelViewNode> mealNode = new LinkedMultiTreeNode<>(new MultiLevelViewNode(ITEM_TYPE_MEAL_COURSE, meal));
            dateNode.add(mealNode);

        }
        Log.i(TAG, "prepareDataset: Done Creating tree");

        items = convertToFlattenedTree(dataset);

    }

    /**
     * Convert the provided Tree into a flattened List. The Tree is traversed in Pre-Order and the root element
     * is left out of the traversal.
     *
     * @param rootNode the node from where the tree should be flattened
     * @return the list of pre-order traversed items.
     */
    private List<MultiLevelViewNode> convertToFlattenedTree(TreeNode<MultiLevelViewNode> rootNode) {
        Log.i(TAG, "convertToFlattenedTree: Flattening provided tree");
        List<MultiLevelViewNode> viewNodes = new LinkedList<>();

        TraversalAction<TreeNode<MultiLevelViewNode>> flatten = new TraversalAction<TreeNode<MultiLevelViewNode>>() {

            private TreeNode<MultiLevelViewNode> lastNode;

            /**
             * Checks whether the provided node is a First Child. ie. In the view, they are sectioned separately.
             * A Node is also said to be first child, if this node is of different meal type.
             *
             * @param node the node to check
             * @return true if the node is supposed
             */
            private boolean isFirstChild(TreeNode<MultiLevelViewNode> node) {
                if (lastNode == null || !lastNode.isSiblingOf(node)){
                    return true;
                }

                Object lastNodeItem = lastNode.data().getItem();
                Object nodeItem = node.data().getItem();

                if (lastNodeItem instanceof MealCourse && nodeItem instanceof MealCourse) {
                    Log.d(TAG, "isFirstChild: Both nodes are of type MealCourse");
                    MealCourse lastNodeMeal = (MealCourse) lastNodeItem;
                    MealCourse nodeMeal = (MealCourse) nodeItem;

                    if (lastNode.isSiblingOf(node) && lastNodeMeal.getType() != nodeMeal.getType()) {
                        Log.d(TAG, "isFirstChild: Node sibling but of different type");
                        return true;
                    }

                }

                return false;
            }

            @Override
            public void perform(TreeNode<MultiLevelViewNode> node) {
                if(!node.isRoot()) {
                    Log.d(TAG, "perform: Traversing node: " + node.data().getItem());
                    if (isFirstChild(node)){
                        Log.d(TAG, "perform: Found First Child: " + node.data().getItem());
                        node.data().setFirstChild(true);
                        lastNode = node;
                    }
                    viewNodes.add(node.data());
                }
            }

            @Override
            public boolean isCompleted() {
                return false;
            }
        };

        rootNode.traversePreOrder(flatten);
        Log.i(TAG, "convertToFlattenedTree: Done flattening tree");
        return viewNodes;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        RecyclerView.ViewHolder holder = null;
        switch (viewType) {
            case ITEM_TYPE_DATE:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.layout_list_item_date_header, parent, false);
                holder = new DateViewHolder(view);
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

            MultiLevelViewNode item = items.get(position);

            switch (holder.getItemViewType()) {
                case ITEM_TYPE_DATE:
                    DateViewHolder dateViewHolder = (DateViewHolder) holder;
                    dateViewHolder.bind(item);
                    break;
                case ITEM_TYPE_MEAL_COURSE:
                    MealCourseViewHolder mealCourseViewHolder = (MealCourseViewHolder) holder;
                    mealCourseViewHolder.bind(item);
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
     * Get a meal course at a specific position within the adapter
     *
     * @param position the position to retrieve the item at
     * @return the meal course if available else null
     */
    @Nullable MealCourse getMealCourseAtPosition(int position) {
        if (items != null && !items.isEmpty()) {
            MultiLevelViewNode node = items.get(position);

            if (node.getItem() instanceof MealCourse) {
                return (MealCourse) node.getItem();
            }

        }

        return null;
    }

    /**
     * Sets a new list of meal items into adapter
     *
     * @param meals the new list of meals to be added into the view
     */
    public void setItems(List<MealCourse> meals) {
        prepareDataset(meals);
        notifyDataSetChanged();
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

        private void bind(MultiLevelViewNode node) {
            mItem = (String) node.getItem();
            mContentView.setText(mItem);
        }
    }

    /**
     * A View holder class to handle the list Item showing the Meal information
     */
    class MealCourseViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener {
        final View mView;
        final TextView mContentView;
        final LinearLayout mMealTypeMarker;
        final RelativeLayout mMealCourseLayout;
        MealCourse mItem;

        MealCourseViewHolder(View view) {
            super(view);
            mView = view;
            mContentView = view.findViewById(R.id.content);
            mMealTypeMarker = view.findViewById(R.id.meal_type_marker);
            mMealCourseLayout = view.findViewById(R.id.layout_meal_course);
            view.setOnCreateContextMenuListener(this);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }

        @Override
        public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
            Log.i(TAG, "onCreateContextMenu: Context menu requested");

            contextMenu.setHeaderTitle(view.getContext().getString(R.string.title_meal_plan_context_menu));
            UIUtils.addContextMenuEntryForListItem(contextMenu, getAdapterPosition(), R.id.action_meal_edit, R.string.action_edit);
            UIUtils.addContextMenuEntryForListItem(contextMenu, getAdapterPosition(), R.id.action_meal_delete, R.string.action_delete);

        }

        /**
         * Bind with the defined node value.
         * @param node the node with which this view holder should be bound to
         */
        private void bind(MultiLevelViewNode node) {
            mItem = (MealCourse) node.getItem();

            // setup the text
            mContentView.setText(mItem.getName());

            // setup the meal type marker (side banner)
            int mealType = mItem.getType().ordinal();
            mMealTypeMarker.setBackgroundColor(context.getResources().getIntArray(R.array.mealTypePalette)[mealType]);

            // setup the margin for the first child
            if (node.isFirstChild()) {
                RelativeLayout.MarginLayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        context.getResources().getDimensionPixelSize(R.dimen.height_meal_plan_course));
                layoutParams.topMargin = context.getResources().getDimensionPixelSize(R.dimen.margin_top_meal_plan_meal);

                mMealCourseLayout.setLayoutParams(layoutParams);
            }
        }
    }
}
