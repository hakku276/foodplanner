package np.com.aanalbasaula.foodplanner.views.shopping_list;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import np.com.aanalbasaula.foodplanner.R;
import np.com.aanalbasaula.foodplanner.database.ShoppingListEntry;

public class ShoppingListViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String TAG = ShoppingListViewAdapter.class.getSimpleName();

    // the items to be shown on the view
    @Nullable
    private List<ShoppingListEntry> items;

    /**
     * Create a view adapter, provided the items to be displayed
     *
     * @param items    the items to be displayed
     */
    ShoppingListViewAdapter(@NonNull List<ShoppingListEntry> items) {
        this.items = items;
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
            ShoppingListEntry item = items.get(position);
            ShoppingListEntryViewHolder dateViewHolder = (ShoppingListEntryViewHolder) holder;
            dateViewHolder.mItem = item;
            dateViewHolder.mContentView.setText(dateViewHolder.mItem.getName());
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
     * A View holder class to handle the list Item showing the Dates
     */
    class ShoppingListEntryViewHolder extends RecyclerView.ViewHolder {
        final View mView;
        final TextView mContentView;
        ShoppingListEntry mItem;

        ShoppingListEntryViewHolder(View view) {
            super(view);
            mView = view;
            mContentView = view.findViewById(R.id.content);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }
}
