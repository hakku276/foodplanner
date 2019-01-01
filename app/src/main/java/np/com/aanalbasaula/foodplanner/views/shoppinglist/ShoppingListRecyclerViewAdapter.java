package np.com.aanalbasaula.foodplanner.views.shoppinglist;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import np.com.aanalbasaula.foodplanner.R;
import np.com.aanalbasaula.foodplanner.database.CartItem;

public class ShoppingListRecyclerViewAdapter extends RecyclerView.Adapter<ShoppingListRecyclerViewAdapter.ViewHolder> {

    @Nullable
    private List<CartItem> items;

    public ShoppingListRecyclerViewAdapter(@Nullable List<CartItem> cartItems){
    this.items = cartItems;
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
        if (items != null){
            holder.mItem = items.get(position);
            holder.mContentView.setText(items.get(position).getName());
        }
    }

    @Override
    public int getItemCount() {
        return (items == null) ? 0: items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        final View mView;
        final TextView mContentView;
        CartItem mItem;

        ViewHolder(View view) {
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
