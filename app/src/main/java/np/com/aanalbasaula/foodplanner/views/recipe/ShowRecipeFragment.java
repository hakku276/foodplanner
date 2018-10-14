package np.com.aanalbasaula.foodplanner.views.recipe;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import np.com.aanalbasaula.foodplanner.R;
import np.com.aanalbasaula.foodplanner.database.Recipe;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ShowRecipeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ShowRecipeFragment extends Fragment {

    private Recipe recipe;

    public ShowRecipeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param recipe The recipe to show on the fragment
     * @return A new instance of fragment ShowRecipeFragment.
     */
    public static ShowRecipeFragment newInstance(Recipe recipe) {
        ShowRecipeFragment fragment = new ShowRecipeFragment();
        fragment.recipe = recipe;
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v =  inflater.inflate(R.layout.fragment_show_recipe, container, false);
        TextView tvRecipeName = v.findViewById(R.id.tv_recipe_name);
        tvRecipeName.setText(recipe.getName());

        RecyclerView rvIngredients = v.findViewById(R.id.rv_ingredients);
        IngredientRecyclerViewAdapter adapter = new IngredientRecyclerViewAdapter(recipe.getIngredients());
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        rvIngredients.setLayoutManager(layoutManager);
        rvIngredients.setItemAnimator(new DefaultItemAnimator());
        rvIngredients.setAdapter(adapter);

        return v;
    }
}
