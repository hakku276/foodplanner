package np.com.aanalbasaula.foodplanner.views.cookbook;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.LinkedList;
import java.util.List;

import np.com.aanalbasaula.foodplanner.R;
import np.com.aanalbasaula.foodplanner.database.Ingredient;
import np.com.aanalbasaula.foodplanner.database.RecipeStep;

/**
 * {@link RecyclerView.Adapter} that can display, edit and delete any item on a list that can be
 * convert to and from and String description.
 * The To and from conversion will be performed using the supplied {@linkplain ItemFactory}. New Views
 * will be created using the {@linkplain ViewFactory}, but the view should follow certain requirements on ID conventions.
 * <ul>
 *     <li>There should exist a Root Layout with Two Child Relative Layouts. One for Edit mode ({@linkplain R.id#layout_edit}) and another for View mode ({@linkplain R.id#layout_display}).</li>
 *     <li>The Display Content is presented on a TextView with the id {@linkplain R.id#content}</li>
 *     <li>The Editable contect is presented on an EditText with the id {@linkplain R.id#editable_content}</li>
 *     <li>The bottom border is a LinerLayout and has the id {@linkplain R.id#bottom_border}</li>
 * </ul>
 *
 * Note: It is possible to leave out components, leaving out components will result in some features being unavailable, eg: Leaving Delete out will result in the delete function to be
 * unavailable (as expected).
 *
 * At Present only allows Edit Mode
 *
 * @param <T> The Type of item which should be displayed on screen
 */
public class GenericEditableListAdapter<T> extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String TAG = GenericEditableListAdapter.class.getSimpleName();

    // the items to be shown on the view
    @NonNull
    private List<T> items;

    private final ItemFactory<T> itemFactory;
    private final ViewFactory viewFactory;

    private final boolean isEditMode;

    /**
     * Create a view adapter, provided the items to be displayed
     *
     * @param items the items to be displayed
     */
    GenericEditableListAdapter(@Nullable List<T> items, boolean isEditMode, ItemFactory<T> itemFactory, ViewFactory viewFactory) {
        if (items == null) {
            items = new LinkedList<>();
        }
        this.isEditMode = isEditMode;
        this.items = items;
        this.itemFactory = itemFactory;
        this.viewFactory = viewFactory;
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
//        View view = LayoutInflater.from(parent.getContext())
//                .inflate(R.layout.layout_list_item_ingredient, parent, false);
        View view = viewFactory.create(layoutInflater, parent);
        return new EditableItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, int position) {
        T item = null;
        if (position < items.size()) {
            item = items.get(position);
        }
        EditableItemViewHolder editableItemViewHolder = (EditableItemViewHolder) holder;
        editableItemViewHolder.bind(item);
    }

    @Override
    public int getItemCount() {
        int count = items.size();
        return isEditMode ? (count + 1) : count; // edit mode shows 1 extra
    }

    /**
     * Add ingredient to be displayed on the list to the user.
     *
     * @param ingredient the new ingredient to be added into the list
     */
    void addIngredient(T ingredient) {
        items.add(ingredient);
    }

    public List<T> getIngredients() {
        return items;
    }

    /**
     * The item factory that is necessary to create objects or descriptions of objects to allow
     * generic editing and display on the adapter.
     *
     * @param <I> The of Item being handled by this factory
     *
     */
    public interface ItemFactory<I> {

        /**
         * Get a unique id for the Item. Maybe used only for logging purposes.
         *
         * @param item the item to get the ID for
         * @return the String unique Id. Converted into string if necessary.
         */
        String id(I item);

        /**
         * Get a String description for the Item to be displayed on the UI.
         *
         * @param item the item to describe
         * @return the String description, Empty string possible, but not null
         */
        String describe(I item);

        /**
         * Create a new Item using the provided description.
         *
         * @param description the description that the User provided in the UI
         * @return the newly created item
         */
        I create(String description);

        /**
         * Update the Item description using hte provided description.
         *
         * @param item        the item to be updated, never null
         * @param description the new description
         */
        void update(I item, String description);
    }

    /**
     * The view factory that will create views and provides other convenience methods for handling
     * UI Situations.
     */
    public interface ViewFactory {

        /**
         * Create a view that can be used to display and edit the list item.
         *
         * @param layoutInflater the layout inflater to be used to create new views.
         * @return the newly created view to be displayed on screen
         */
        View create(LayoutInflater layoutInflater, ViewGroup parent);
    }

    /**
     * A View holder class to handle the list Item showing the Ingredients
     */
    class EditableItemViewHolder extends RecyclerView.ViewHolder {

        @NonNull
        final View mView;

        @Nullable
        final RelativeLayout mDisplayLayout;

        @Nullable
        final RelativeLayout mEditLayout;

        @Nullable
        final TextView mContentView;

        @Nullable
        final EditText mEditableContent;

        @Nullable
        final ImageButton mDeleteButton;

        @Nullable
        final LinearLayout mBottomBorder;

        @Nullable
        T mItem;

        EditableItemViewHolder(@NonNull View view) {
            super(view);
            mView = view;
            mContentView = view.findViewById(R.id.content);
            mDeleteButton = view.findViewById(R.id.btn_remove);
            mDisplayLayout = view.findViewById(R.id.layout_display);
            mEditLayout = view.findViewById(R.id.layout_edit);
            mEditableContent = view.findViewById(R.id.editable_content);
            mBottomBorder = view.findViewById(R.id.bottom_border);

            mView.setOnClickListener(this::onIngredientViewClicked);

            if (mEditableContent != null) {
                mEditableContent.setOnEditorActionListener(this::onEditorAction);
                mEditableContent.setOnFocusChangeListener(this::onEditorFocusChange);
            }

            if (mDeleteButton != null) {
                mDeleteButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Log.i(TAG, "onClick: Remove button clicked by user for ingredient: " + itemFactory.id(mItem));
                        items.remove(getAdapterPosition());
                        Log.i(TAG, "onClick: Notifying data set changed for view refresh");
                        notifyDataSetChanged();
                    }
                });
            }
        }

        /**
         * The Editor Action handler for the editable content.
         */
        private boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
            if (actionId == EditorInfo.IME_ACTION_NEXT) {
                Log.i(TAG, "onEditorAction: User pressed Editor Action Next");
                String text = textView.getText().toString().trim();
                // next should create item only when this is the last entry on the list ie. mItem = null
                if (!text.isEmpty() && mItem == null) {
                    T item = itemFactory.create(text);
                    items.add(item);
                    notifyDataSetChanged();
                } else if (mItem != null){
                    itemFactory.update(mItem, text);
                    enableDisplayMode();
                }
                return true;
            }
            return false;
        }

        /**
         * Listen to clicks on the view, if the view is clicked, then display the edit mode.
         */
        private void onIngredientViewClicked(View view) {
            if (mItem != null && mEditableContent != null) {
                Log.i(TAG, "onIngredientViewClicked: The user clicked for edit: ingredient: " + itemFactory.id(mItem));
                String description = itemFactory.describe(mItem);
                enableEditMode(description);
                mEditableContent.requestFocus();
                mEditableContent.setSelection(description.length());
            }
        }

        /**
         * Handle Editor Focus changes: To make the Edit Text into a Text View on Focus lost
         */
        private void onEditorFocusChange(View view, boolean hasFocus) {
            if (mItem != null && !hasFocus) {
                Log.i(TAG, "onEditorFocusChange: Editor Focus has been lost: " + itemFactory.id(mItem));
                enableDisplayMode();
            }
        }

        /**
         * Bind the view to the ingredient
         *
         * @param ingredient the ingredient value to bind to, null would mean there is no ingredient available
         */
        private void bind(T ingredient) {
            mItem = ingredient;
            if (mItem != null) {
                // display mode is always in between the views, therefore; show the bottom border
                if (mBottomBorder != null) {
                    mBottomBorder.setVisibility(View.VISIBLE);
                }
                enableDisplayMode();
            } else {
                // edit mode is the last item on the list; therefore hide the bottom border
                if (mBottomBorder != null) {
                    mBottomBorder.setVisibility(View.GONE);
                }
                enableEditMode("");
                if (items.size() != 0 && mEditableContent != null) {
                    mEditableContent.requestFocus();
                }
            }
        }

        /**
         * Model as a display field
         */
        private void enableDisplayMode() {
            if (mDisplayLayout != null) {
                mDisplayLayout.setVisibility(View.VISIBLE);
            }

            if (mEditLayout != null) {
                mEditLayout.setVisibility(View.GONE);
            }

            if (mContentView != null) {
                mContentView.setText(itemFactory.describe(mItem));
            }

            if (mEditableContent != null) {
                mEditableContent.setText(itemFactory.describe(mItem));
            }
        }

        /**
         * Model as an edit field
         */
        private void enableEditMode(String value) {
            if (mDisplayLayout != null) {
                mDisplayLayout.setVisibility(View.GONE);
            }

            if (mEditLayout != null) {
                mEditLayout.setVisibility(View.VISIBLE);
            }

            if (mContentView != null) {
                mContentView.setText(value);
            }

            if (mEditableContent != null) {
                mEditableContent.setText(value);
            }
        }

        @Override
        public String toString() {
            return super.toString() + " '" + itemFactory.describe(mItem) + "'";
        }
    }

    /**
     * A Precompiled list of Item Factories for convenience
     */
    public static class ItemFactories {

        /**
         * The Item Factory for Ingredients
         */
        public static final ItemFactory<Ingredient> INGREDIENTS = new ItemFactory<Ingredient>() {

            @Override
            public String id(Ingredient item) {
                return item.getName();
            }

            @Override
            public String describe(Ingredient item) {
                return item.getName();
            }

            @Override
            public Ingredient create(String description) {
                Ingredient item = new Ingredient();
                item.setName(description);
                return item;
            }

            @Override
            public void update(Ingredient item, String description) {
                item.setName(description);
            }
        };

        /**
         * The Item Factory for Ingredients
         */
        public static final ItemFactory<RecipeStep> RECIPE_STEPS = new ItemFactory<RecipeStep>() {

            @Override
            public String id(RecipeStep item) {
                return item.getDescription();
            }

            @Override
            public String describe(RecipeStep item) {
                return item.getDescription();
            }

            @Override
            public RecipeStep create(String description) {
                RecipeStep item = new RecipeStep();
                item.setDescription(description);
                return item;
            }

            @Override
            public void update(RecipeStep item, String description) {
                item.setDescription(description);
            }
        };


    }

}
