package np.com.aanalbasaula.foodplanner.views.cookbook;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import np.com.aanalbasaula.foodplanner.R;
import np.com.aanalbasaula.foodplanner.database.Recipe;

/**
 * {@link RecyclerView.Adapter} that can display a {@link np.com.aanalbasaula.foodplanner.database.Recipe}.
 */
public class CookbookViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    // the items to be shown on the view
    @Nullable
    private List<Recipe> items;

    /**
     * Create a view adapter, provided the items to be displayed
     *
     * @param items    the items to be displayed
     */
    CookbookViewAdapter(@NonNull List<Recipe> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_list_item_recipe, parent, false);
        return new RecipeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, int position) {
        if (items != null) {
            Recipe item = items.get(position);
            RecipeViewHolder dateViewHolder = (RecipeViewHolder) holder;
            dateViewHolder.mItem = item.getName();
            dateViewHolder.mContentView.setText(dateViewHolder.mItem);
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
    class RecipeViewHolder extends RecyclerView.ViewHolder {
        final View mView;
        final TextView mContentView;
        String mItem;

        RecipeViewHolder(View view) {
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
