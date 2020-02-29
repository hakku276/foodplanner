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

/**
 * {@link RecyclerView.Adapter} that can display any Object that can be convert to and from and String description.
 * The To and from conversion will be performed using the supplied {@linkplain ItemFactory}
 *
 * @param <T> The Type of item which should be
 */
public class GenericItemDisplayViewAdapter<T> extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String TAG = GenericItemDisplayViewAdapter.class.getSimpleName();

    // the items to be shown on the view
    @NonNull
    private List<T> items;

    private final ItemFactory<T> itemFactory;

    private final boolean isEditMode;

    /**
     * Create a view adapter, provided the items to be displayed
     *
     * @param items the items to be displayed
     */
    GenericItemDisplayViewAdapter(@Nullable List<T> items, boolean isEditMode, ItemFactory<T> itemFactory) {
        if (items == null) {
            items = new LinkedList<>();
        }
        this.isEditMode = isEditMode;
        this.items = items;
        this.itemFactory = itemFactory;
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_list_item_ingredient, parent, false);
        return new GenericItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, int position) {
        T item = null;
        if (position < items.size()) {
            item = items.get(position);
        }
        GenericItemViewHolder genericItemViewHolder = (GenericItemViewHolder) holder;
        genericItemViewHolder.bind(item);
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
     * A View holder class to handle the list Item showing the Ingredients
     */
    class GenericItemViewHolder extends RecyclerView.ViewHolder {
        final View mView;
        final RelativeLayout mDisplayLayout;
        final RelativeLayout mEditLayout;
        final TextView mContentView;
        final EditText mEditableContent;
        final ImageButton mDeleteButton;
        final LinearLayout mBottomBorder;
        T mItem;

        GenericItemViewHolder(View view) {
            super(view);
            mView = view;
            mContentView = view.findViewById(R.id.content);
            mDeleteButton = view.findViewById(R.id.btn_remove);
            mDisplayLayout = view.findViewById(R.id.layout_display_ingredient);
            mEditLayout = view.findViewById(R.id.layout_edit_ingredient);
            mEditableContent = view.findViewById(R.id.editable_content);
            mBottomBorder = view.findViewById(R.id.bottom_border);

            mView.setOnClickListener(this::onIngredientViewClicked);
            mEditableContent.setOnEditorActionListener(this::onEditorAction);
            mEditableContent.setOnFocusChangeListener(this::onEditorFocusChange);

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

        /**
         * The Editor Action handler for the editable content.
         */
        private boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
            if (actionId == EditorInfo.IME_ACTION_NEXT) {
                Log.i(TAG, "onEditorAction: User pressed Editor Action Next");
                String text = mEditableContent.getText().toString().trim();
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
            if (mItem != null) {
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
                mBottomBorder.setVisibility(View.VISIBLE);
                enableDisplayMode();
            } else {
                // edit mode is the last item on the list; therefore hide the bottom border
                mBottomBorder.setVisibility(View.GONE);
                enableEditMode("");
                if (items.size() != 0) {
                    mEditableContent.requestFocus();
                }
            }
        }

        /**
         * Model as a display field
         */
        private void enableDisplayMode() {
            mDisplayLayout.setVisibility(View.VISIBLE);
            mEditLayout.setVisibility(View.GONE);
            mContentView.setText(itemFactory.describe(mItem));
            mEditableContent.setText(itemFactory.describe(mItem));
        }

        /**
         * Model as an edit field
         */
        private void enableEditMode(String value) {
            mDisplayLayout.setVisibility(View.GONE);
            mEditLayout.setVisibility(View.VISIBLE);
            mContentView.setText(value);
            mEditableContent.setText(value);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
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

    }

}
