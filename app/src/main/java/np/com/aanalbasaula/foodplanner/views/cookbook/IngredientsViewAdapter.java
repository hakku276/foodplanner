package np.com.aanalbasaula.foodplanner.views.cookbook;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.LinkedList;
import java.util.List;

import np.com.aanalbasaula.foodplanner.R;
import np.com.aanalbasaula.foodplanner.database.Ingredient;

/**
 * {@link RecyclerView.Adapter} that can display a {@link np.com.aanalbasaula.foodplanner.database.Ingredient}.
 */
public class IngredientsViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String TAG = IngredientsViewAdapter.class.getSimpleName();

    // the items to be shown on the view
    @NonNull
    private List<Ingredient> items;

    // the listener for click event on the recipes
    @Nullable
    private IngredientListChangedListener listener;

    /**
     * Create a view adapter, provided the items to be displayed
     *
     * @param items the items to be displayed
     */
    IngredientsViewAdapter(List<Ingredient> items, @Nullable IngredientListChangedListener listener) {
        if (items == null) {
            this.items = new LinkedList<>();
        }
        this.items = items;
        this.listener = listener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_list_item_ingredient, parent, false);
        return new IngredientViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, int position) {
        Ingredient item = items.get(position);
        IngredientViewHolder ingredientViewHolder = (IngredientViewHolder) holder;
        ingredientViewHolder.mItem = item;
        ingredientViewHolder.mContentView.setText(ingredientViewHolder.mItem.getName());
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    /**
     * Add ingredient to be displayed on the list to the user.
     *
     * @param ingredient the new ingredient to be added into the list
     */
    void addIngredient(Ingredient ingredient) {
        items.add(ingredient);

        // notify listener
        if (listener != null) {
            listener.onIngredientsChanged(items);
        }
    }

    public List<Ingredient> getIngredients() {
        return items;
    }

    /**
     * A listener interface to listen to clicks changes on the ingredients list
     */
    public interface IngredientListChangedListener {

        /**
         * The list of ingredients was modified by the user. In this case
         * it was removed from the list.
         *
         * @param ingredients the new list of ingredients
         */
        void onIngredientsChanged(List<Ingredient> ingredients);
    }

    /**
     * A View holder class to handle the list Item showing the Ingredients
     */
    class IngredientViewHolder extends RecyclerView.ViewHolder {
        final View mView;
        final TextView mContentView;
        final ImageButton mDeleteButton;
        Ingredient mItem;

        IngredientViewHolder(View view) {
            super(view);
            mView = view;
            mContentView = view.findViewById(R.id.content);
            mDeleteButton = view.findViewById(R.id.btn_remove);
            mDeleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.i(TAG, "onClick: Remove button clicked by user for ingredient: " + mItem.getName());
                    items.remove(getAdapterPosition());
                    Log.i(TAG, "onClick: Notifying data set changed for view refresh");
                    notifyDataSetChanged();

                    if (listener != null) {
                        Log.i(TAG, "onClick: Notifying listener on state changed");
                        listener.onIngredientsChanged(items);
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
