package np.com.aanalbasaula.foodplanner;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import java.util.List;

import np.com.aanalbasaula.foodplanner.database.AppDatabase;
import np.com.aanalbasaula.foodplanner.database.Recipe;
import np.com.aanalbasaula.foodplanner.database.utils.LoadRecipesAsync;
import np.com.aanalbasaula.foodplanner.views.recipe.LoadScreenFragment;
import np.com.aanalbasaula.foodplanner.views.recipe.NewRecipeActivity;
import np.com.aanalbasaula.foodplanner.views.recipe.ShowAllRecipesFragment;
import np.com.aanalbasaula.foodplanner.views.recipe.ViewRecipeActivity;
import np.com.aanalbasaula.foodplanner.views.shoppinglist.ShoppingListFragment;

public class MainActivity extends AppCompatActivity implements ShowAllRecipesFragment.ShowAllRecipesFragmentListener {
    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int REQUEST_NEW_RECIPE = 100;

    /**
     * The pager view which is responsible for showing the different major features of the application
     */
    private ViewPager mViewPager;

    /**
     * The Pager Adapter which helps the View Pager to show multiple features
     */
    private FeaturePagerAdapter mFeaturePager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.i(TAG, "onCreate: Creating the Tabbed Action bar");
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        TabLayout tabLayout = findViewById(R.id.tab_layout);
        setSupportActionBar(toolbar);

        Log.i(TAG, "onCreate: Creating the Pager View");
        mFeaturePager = new FeaturePagerAdapter(getSupportFragmentManager(),
                new Fragment[]{ShowAllRecipesFragment.newInstance(), ShoppingListFragment.newInstance()},
                new String[] {getString(R.string.feature_recipes), getString(R.string.feature_shopping_list)});
        mViewPager = (ViewPager) findViewById(R.id.view_pager);
        mViewPager.setAdapter(mFeaturePager);

        Log.i(TAG, "onCreate: Making the view pager work with the tab layout");
        tabLayout.setupWithViewPager(mViewPager);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i(TAG, "onStart: View Starting up");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_new_recipe:
                Log.i(TAG, "onOptionsItemSelected: New recipe option menu clicked");
                startNewRecipeCreation();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    private void startNewRecipeCreation() {
        Intent intent = new Intent(this, NewRecipeActivity.class);
        startActivityForResult(intent, REQUEST_NEW_RECIPE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_NEW_RECIPE && resultCode == NewRecipeActivity.RESULT_RECIPE_ADDED) {
            Log.i(TAG, "onActivityResult: Recipe was added");
            Recipe recipe = data.getParcelableExtra(NewRecipeActivity.EXTRA_RECIPE);
            //showAllRecipesFragment.onNewRecipeAdded(recipe);
        } else {
            Log.i(TAG, "onActivityResult: Request code: " + requestCode + " result code: " + resultCode);
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onListFragmentInteraction(Recipe item) {
        Log.i(TAG, "onListFragmentInteraction: User interacted with item: " + item.toString());
        Intent intent = new Intent(this, ViewRecipeActivity.class);
        intent.putExtra(ViewRecipeActivity.EXTRA_RECIPE, item);
        startActivity(intent);
    }
}

/**
 * A Pager Adapter to page through the major features of the app in a tabbed view.
 */
class FeaturePagerAdapter extends FragmentPagerAdapter {

    /**
     * The fragment items being shown by the Adapter.
     */
    @NonNull
    private final Fragment[] fragments;

    /**
     * The names of the fragment sections
     */
    @NonNull
    private final String[] names;

    /**
     * Create a Application Feature Pager, with the provided fragment views and the titles relating to the same views
     *
     * @param fm     the support fragment manager
     * @param views  the fragment views provided to be displayed
     * @param titles the title to the fragment views
     */
    FeaturePagerAdapter(FragmentManager fm, @NonNull Fragment[] views, @NonNull String[] titles) {
        super(fm);
        if (views.length != titles.length) {
            throw new IllegalArgumentException("The views count and the titles count do not match.");
        }
        fragments = views;
        names = titles;
    }

    /**
     * Replaces the current fragment with the new fragment in the defined position
     *
     * @param position the position where the fragment is to be replaced, should be between 0 and length of {@linkplain FeaturePagerAdapter#fragments}.
     * @param view     the new fragment view to replace the current view with
     */
    void replaceView(int position, Fragment view) {
        fragments[position] = view;
        notifyDataSetChanged();
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return names[position];
    }

    @Override
    public Fragment getItem(int position) {
        return fragments[position];
    }

    @Override
    public int getCount() {
        return fragments.length;
    }
}
