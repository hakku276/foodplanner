package np.com.aanalbasaula.foodplanner.views.cookbook;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;
import java.util.Optional;

import np.com.aanalbasaula.foodplanner.R;
import np.com.aanalbasaula.foodplanner.database.Recipe;

/**
 * {@link RecyclerView.Adapter} that can display a {@link np.com.aanalbasaula.foodplanner.database.Recipe}.
 */
public class CookbookViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String TAG = CookbookViewAdapter.class.getSimpleName();

    // the items to be shown on the view
    @Nullable
    private List<Recipe> items;

    // the listener for click event on the recipes
    @Nullable
    private RecipeClickListener listener;

    /**
     * Create a view adapter, provided the items to be displayed
     *
     * @param items    the items to be displayed
     */
    CookbookViewAdapter(@NonNull List<Recipe> items, @Nullable RecipeClickListener listener) {
        this.items = items;
        this.listener = listener;
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
     * A listener interface to listen to clicks to the Recipe items on the recipe list.
     */
    public interface RecipeClickListener {

        /**
         * A certain recipe item was clicked by the user.
         * @param recipe the recipe object that was clicked
         */
        void onRecipeItemClicked(Recipe recipe);
    }

    /**
     * A View holder class to handle the list Item showing the Dates
     */
    class RecipeViewHolder extends RecyclerView.ViewHolder {
        final View mView;
        final TextView mContentView;
        Recipe mItem;

        RecipeViewHolder(View view) {
            super(view);
            mView = view;
            mContentView = view.findViewById(R.id.content);
            mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listener != null) {
                        Log.i(TAG, "onClick: Recipe item clicked");
                        listener.onRecipeItemClicked(mItem);
                    }
                }
            });
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }
}
