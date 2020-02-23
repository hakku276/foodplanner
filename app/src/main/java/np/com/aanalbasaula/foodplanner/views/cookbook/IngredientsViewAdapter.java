package np.com.aanalbasaula.foodplanner.views.cookbook;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
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

    private final boolean isEditMode;

    /**
     * Create a view adapter, provided the items to be displayed
     *
     * @param items the items to be displayed
     */
    IngredientsViewAdapter(@Nullable List<Ingredient> items, boolean isEditMode, @Nullable IngredientListChangedListener listener) {
        if (items == null) {
            items = new LinkedList<>();
        }
        this.isEditMode = isEditMode;
        this.items = items;
        this.listener = listener;
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
        return new IngredientViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, int position) {
        Ingredient item = null;
        if (position < items.size()) {
            item = items.get(position);
        }
        IngredientViewHolder ingredientViewHolder = (IngredientViewHolder) holder;
        ingredientViewHolder.bind(item);
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
        final RelativeLayout mDisplayLayout;
        final RelativeLayout mEditLayout;
        final TextView mContentView;
        final EditText mEditableContent;
        final ImageButton mDeleteButton;
        Ingredient mItem;

        IngredientViewHolder(View view) {
            super(view);
            mView = view;
            mContentView = view.findViewById(R.id.content);
            mDeleteButton = view.findViewById(R.id.btn_remove);
            mDisplayLayout = view.findViewById(R.id.layout_display_ingredient);
            mEditLayout = view.findViewById(R.id.layout_edit_ingredient);
            mEditableContent = view.findViewById(R.id.editable_content);

            mView.setOnClickListener(this::onIngredientViewClicked);
            mEditableContent.setOnEditorActionListener(this::onEditorAction);
            mEditableContent.setOnFocusChangeListener(this::onEditorFocusChange);

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

        /**
         * The Editor Action handler for the editable content.
         */
        private boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent){
            if (actionId == EditorInfo.IME_ACTION_NEXT) {
                Log.i(TAG, "onEditorAction: User pressed Editor Action Next");
                String text = mEditableContent.getText().toString().trim();
                // next should create item only when this is the last entry on the list ie. mItem = null
                if (!text.isEmpty() && mItem == null) {
                    Ingredient ingredient = new Ingredient();
                    ingredient.setName(text);
                    items.add(ingredient);
                    notifyDataSetChanged();
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
                Log.i(TAG, "onIngredientViewClicked: The user clicked for edit: ingredient: " + mItem.getName());
                mDisplayLayout.setVisibility(View.GONE);
                mEditLayout.setVisibility(View.VISIBLE);
                mEditableContent.requestFocus();
                mEditableContent.setSelection(mItem.getName().length());
            }
        }

        /**
         * Handle Editor Focus changes: To make the Edit Text into a Text View on Focus lost
         */
        private void onEditorFocusChange(View view, boolean b) {
            if (mItem != null) {
                Log.i(TAG, "onEditorFocusChange: Editor Focus Changed: " + mItem.getName());
            }
        }

        /**
         * Bind the view to the ingredient
         *
         * @param ingredient the ingredient value to bind to, null would mean there is no ingredient available
         */
        private void bind(Ingredient ingredient) {
            mItem = ingredient;
            if (mItem != null) {
                mDisplayLayout.setVisibility(View.VISIBLE);
                mEditLayout.setVisibility(View.GONE);
                mContentView.setText(mItem.getName());
                mEditableContent.setText(mItem.getName());
            } else {
                mDisplayLayout.setVisibility(View.GONE);
                mEditLayout.setVisibility(View.VISIBLE);
                mContentView.setText("");
                mEditableContent.setText("");
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
            mContentView.setText(mItem.getName());
            mEditableContent.setText(mItem.getName());
        }

        /**
         * Model as an edit field
         */
        private void enableEditMode() {
            mDisplayLayout.setVisibility(View.GONE);
            mEditLayout.setVisibility(View.VISIBLE);
            mContentView.setText("");
            mEditableContent.setText("");
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }
}
