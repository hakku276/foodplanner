package np.com.aanalbasaula.foodplanner.database.utils;

import android.util.Log;

import np.com.aanalbasaula.foodplanner.database.Ingredient;
import np.com.aanalbasaula.foodplanner.database.IngredientDao;
import np.com.aanalbasaula.foodplanner.database.MealCourse;
import np.com.aanalbasaula.foodplanner.database.MealCourseDao;
import np.com.aanalbasaula.foodplanner.database.Recipe;
import np.com.aanalbasaula.foodplanner.database.RecipeDao;
import np.com.aanalbasaula.foodplanner.database.RecipeStep;
import np.com.aanalbasaula.foodplanner.database.RecipeStepDao;
import np.com.aanalbasaula.foodplanner.database.ShoppingListDao;
import np.com.aanalbasaula.foodplanner.database.ShoppingListEntry;

/**
 * A collection of predefined Database Entry Creation strategies, that can be used with
 * {@link EntryCreator}
 */
public class EntryCreationStrategies {
    private static final String TAG = EntryCreationStrategies.class.getSimpleName();

    private EntryCreationStrategies() {
        // empty private constructor to hide default constructor
    }

    /**
     * Entry creation Strategy to insert Meal Course using a MealCourseDAO
     */
    public static EntryCreator.CreationStrategy<MealCourseDao, MealCourse> mealCourseCreationStrategy = (dao, entries) -> {
        long[] ids = dao.insert(entries);
        for (int i = 0; i < ids.length; i++) {
            entries[i].setId(ids[i]);
        }
    };

    /**
     * Entry creation Strategy to insert Recipe using a RecipeDAO
     */
    public static EntryCreator.CreationStrategy<RecipeDao, Recipe> recipeCreationStrategy = (dao, entries) -> {
        long[] ids = dao.insert(entries);
        for (int i = 0; i < ids.length; i++) {
            entries[i].setId(ids[i]);
        }
    };

    /**
     * Entry creation Strategy to insert Shopping List entry using a Shopping List DAO
     */
    public static EntryCreator.CreationStrategy<ShoppingListDao, ShoppingListEntry> shoppingListEntryCreationStrategy = (dao, entries) -> {
        long[] ids = dao.insert(entries);
        for (int i = 0; i < ids.length; i++) {
            entries[i].setId(ids[i]);
        }
    };

    /**
     * Entry creation Strategy to insert Ingredient entry using a Ingredient DAO
     */
    public static EntryCreator.CreationStrategy<IngredientDao, Ingredient> ingredientEntryCreationStrategy = (dao, entries) -> {
        long[] ids = dao.insert(entries);
        for (int i = 0; i < ids.length; i++) {
            entries[i].setId(ids[i]);
        }
    };

    /**
     * Entry creation Strategy to insert Ingredient entry using a Ingredient DAO
     */
    public static EntryCreator.CreationStrategy<RecipeStepDao, RecipeStep> recipeStepEntryCreationStrategy = (dao, entries) -> {
        long[] ids = dao.insert(entries);
        for (int i = 0; i < ids.length; i++) {
            entries[i].setId(ids[i]);
        }
    };

}
