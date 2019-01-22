package np.com.aanalbasaula.foodplanner.views.shoppinglist;

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

import java.util.List;

import np.com.aanalbasaula.foodplanner.R;
import np.com.aanalbasaula.foodplanner.database.CartItem;

class ShoppingListRecyclerViewAdapter extends RecyclerView.Adapter<ShoppingListRecyclerViewAdapter.ViewHolder> {

    private static final String TAG = ShoppingListRecyclerViewAdapter.class.getSimpleName();

    @Nullable
    private List<CartItem> items;

    @Nullable
    private ShoppingListStatusListener listener;

    ShoppingListRecyclerViewAdapter(@Nullable List<CartItem> cartItems, @Nullable ShoppingListStatusListener listener) {
        this.items = cartItems;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_list_item_shopping_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (items != null) {
            holder.mItem = items.get(position);
            holder.mContentView.setText(items.get(position).getName());

            //setup the on click listener
            holder.mCheckbox.setOnCheckedChangeListener(new CartItemCheckStateListener(position, holder.mContentView));
        }
    }

    @Override
    public int getItemCount() {
        return (items == null) ? 0 : items.size();
    }

    /**
     * The listener which is notified when the list status has changed, example a remove has been requested by the user
     */
    interface ShoppingListStatusListener {
        /**
         * The item has been requested to be removed by the user
         *
         * @param position the position of the item
         */
        void onItemRemoveRequested(int position);

        /**
         * The user has requested the removed item to be restored
         *
         * @param position the position of the item in the list
         */
        void onItemRestoreRequested(int position);
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        final View mView;
        final TextView mContentView;
        final CheckBox mCheckbox;
        CartItem mItem;

        ViewHolder(View view) {
            super(view);
            mView = view;
            mContentView = view.findViewById(R.id.content);
            mCheckbox = view.findViewById(R.id.checkbox);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }

    /**
     * The check state listener for the cart item, which listens to the user interaction on the Checkbox.
     * The checkbox when set to true will trigger appropriate view updates.
     */
    private class CartItemCheckStateListener implements CompoundButton.OnCheckedChangeListener {

        private int position;
        private TextView mTextView;

        private CartItemCheckStateListener(int position, TextView textView) {
            this.mTextView = textView;
            this.position = position;
        }

        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
            Log.i(TAG, "onCheckedChanged: Checkbox state changed for: " + mTextView.getText() + " to new state: " + b);
            int paintFlags = mTextView.getPaintFlags();
            paintFlags = b ? (paintFlags | Paint.STRIKE_THRU_TEXT_FLAG) : (paintFlags & (~Paint.STRIKE_THRU_TEXT_FLAG));
            mTextView.setPaintFlags(paintFlags);

            if (listener != null) {
                if (b) {
                    listener.onItemRemoveRequested(position);
                } else {
                    listener.onItemRestoreRequested(position);
                }
            }
        }
    }
}
