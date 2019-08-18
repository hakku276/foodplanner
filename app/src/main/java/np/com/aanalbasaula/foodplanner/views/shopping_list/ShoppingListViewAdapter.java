package np.com.aanalbasaula.foodplanner.views.shopping_list;

import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import lombok.Getter;
import np.com.aanalbasaula.foodplanner.R;
import np.com.aanalbasaula.foodplanner.database.ShoppingListEntry;
import np.com.aanalbasaula.foodplanner.utils.UIUtils;

/**
 * A shopping list view adapter to display {@linkplain ShoppingListEntry} on a {@linkplain RecyclerView}.
 */
public class ShoppingListViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String TAG = ShoppingListViewAdapter.class.getSimpleName();

    // the items to be shown on the view
    @Nullable
    private List<ShoppingListEntry> items;

    // the items that the person has selected
    @NonNull
    @Getter
    private final Set<ShoppingListEntry> selectedItems;

    @Nullable
    private ShoppingListSelectionChangeListener mListener;

    /**
     * Create a view adapter, provided the items to be displayed
     *
     * @param items    the items to be displayed
     * @param listener the listener waiting for selection changes
     */
    ShoppingListViewAdapter(@NonNull List<ShoppingListEntry> items, @Nullable ShoppingListSelectionChangeListener listener) {
        this.items = items;
        this.mListener = listener;
        this.selectedItems = new HashSet<>(items.size());

        // prepare already selected list
        for (ShoppingListEntry entry :
                items) {
            if (entry.isSelected()) {
                selectedItems.add(entry);
            }
        }

        // notify that the shopping list selection entries have changed
        if (listener != null) {
            listener.onShoppingListSelectionChanged(selectedItems);
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_list_item_shopping_list, parent, false);
        return new ShoppingListEntryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, int position) {
        if (items != null) {
            ShoppingListEntry item = getItem(position);
            ShoppingListEntryViewHolder viewHolder = (ShoppingListEntryViewHolder) holder;
            viewHolder.mItem = item;
            viewHolder.mContentView.setText(viewHolder.mItem.getName());
            viewHolder.mCheckBox.setChecked(item.isSelected());
        }
    }

    @Override
    public int getItemCount() {
        if (items != null) {
            return items.size();
        }
        return 0;
    }

    /**
     * Get all the shopping list items being handled by this adapter
     * @return the complete list of shopping list entries
     */
    public List<ShoppingListEntry> getItems() {
        if (items == null) {
            return new LinkedList<>();
        }
        return items;
    }

    /**
     * Retrieve the item from the defined position. The logic of the function may determine
     * a mechanism to return objects in different order. The only guarantee provided is, from 0 < position < size,
     * all of the items are returned.
     *
     * @param position the item to retrieve from position
     * @return the entry at the defined position
     */
    private ShoppingListEntry getItem(int position) {
        if (items == null || items.size() == 0) {
            throw new RuntimeException("The items should not be retrieved when there are not items");
        }
        // return items in reverse order so that the items are shown in reverse
        int reversePosition = items.size() - 1 - position;
        return items.get(reversePosition);
    }

    /**
     * A View holder class to handle the list Item showing the Dates
     */
    class ShoppingListEntryViewHolder extends RecyclerView.ViewHolder implements CompoundButton.OnCheckedChangeListener {
        final View mView;
        final TextView mContentView;
        final CheckBox mCheckBox;
        ShoppingListEntry mItem;

        ShoppingListEntryViewHolder(View view) {
            super(view);
            mView = view;
            mContentView = view.findViewById(R.id.content);
            mCheckBox = view.findViewById(R.id.cb_selected);
            mCheckBox.setOnCheckedChangeListener(this);

            mView.setOnClickListener(v -> {
                // toggle checkbox
                mCheckBox.setChecked(!mCheckBox.isChecked());
            });
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }

        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean newState) {
            Log.i(TAG, "onCheckedChanged: Shopping list item state changed to: " + newState);
            mItem.setSelected(newState);
            if (newState) {
                // the text view should be struck out
                UIUtils.addPaintFlagToTextView(mContentView, Paint.STRIKE_THRU_TEXT_FLAG);
                selectedItems.add(mItem);
            } else {
                // reset text view
                UIUtils.removePaintFlatFromTextView(mContentView, Paint.STRIKE_THRU_TEXT_FLAG);
                selectedItems.remove(mItem);
            }

            if (mListener != null) {
                mListener.onShoppingListEntrySelectionChanged(mItem);
                mListener.onShoppingListSelectionChanged(selectedItems);
            }
        }
    }

    /**
     * An interface to listen to selection changes on the adapter
     */
    public interface ShoppingListSelectionChangeListener {

        /**
         * The shopping list entry selection that was changed by the user.
         *
         * @param entry the entry changed
         */
        void onShoppingListEntrySelectionChanged(ShoppingListEntry entry);

        /**
         * The user has changed the selection.
         *
         * @param entries the selected entries
         */
        void onShoppingListSelectionChanged(Set<ShoppingListEntry> entries);

    }

}
