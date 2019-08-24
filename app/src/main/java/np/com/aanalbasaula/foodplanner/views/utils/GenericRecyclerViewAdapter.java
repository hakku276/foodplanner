package np.com.aanalbasaula.foodplanner.views.utils;

import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.LinkedList;
import java.util.List;

/**
 * A generic recycler view that can be utilized instead of creating different classes to handle displaying
 * a list of items in a Recycler View. A user of this class should provide a {@link GenericViewHolder} which
 * performs the necessary actions and are responsible for interactions on the elements of the view. And an instance
 * of the {@link GenericViewHolderFactory} which is responsible for creating an instance of the View
 * Holder
 * <p>
 * This Generic View Adapter uses two generic types:
 * <ul>
 * <li>T: The type of View Holder responsible for displaying items</li>
 * <li>I: The type of item to be displayed on the view</li>
 * </ul>
 */
public class GenericRecyclerViewAdapter<I, T extends GenericRecyclerViewAdapter.GenericViewHolder> extends RecyclerView.Adapter<T> {

    /**
     * The generic view holder which is responsible for handling display and the binding of the views
     * to the view holder, as well as interactions to the view.
     */
    public static abstract class GenericViewHolder<I> extends RecyclerView.ViewHolder {

        public GenericViewHolder(View itemView) {
            super(itemView);
        }

        public abstract void bind(I item);
    }

    /**
     * A view holder factory instance that is responsible for creating the respective View Holder.
     * One alternative is to use Reflection to create instances of the view holder. The need for
     * reflection was the the default constructor that takes a View as an argument.
     *
     * @param <T> The View Holder class that this factory is responsible in producing
     */
    public interface GenericViewHolderFactory<T extends GenericViewHolder> {

        /**
         * Create a new instance of the Specific view holder
         *
         * @param view the view that this view holder is responsible for
         * @return the newly created view holder
         */
        T newInstance(View view);

        /**
         * Create a new View that should be displayed on the screen for the specific View Holder.
         *
         * @param inflater the inflater that can be used to inflate new views
         * @param parent   the parent of the to be created view
         * @return the newly created view. (Can be not attached to parent)
         */
        View createView(LayoutInflater inflater, ViewGroup parent);
    }

    @NonNull
    private final GenericViewHolderFactory<T> viewHolderFactory;

    @NonNull
    private final List<I> items;

    public GenericRecyclerViewAdapter(@Nullable List<I> items, @NonNull GenericViewHolderFactory<T> factory) {
        this.viewHolderFactory = factory;

        // to prevent null lists
        if (items == null) {
            items = new LinkedList<>();
        }
        this.items = items;
    }

    @NonNull
    @Override
    public T onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = viewHolderFactory.createView(LayoutInflater.from(parent.getContext()), parent);
        return viewHolderFactory.newInstance(view);
    }

    @Override
    public void onBindViewHolder(@NonNull T holder, int position) {
        I item = items.get(position);
        holder.bind(item);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

}
