package np.com.aanalbasaula.foodplanner.database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface RecipeIngredientDao {

    @Query("SELECT ingredient_id FROM RECIPEINGREDIENT WHERE recipe_id=:recipe_id")
    long[] getAllIngredientIdsForRecipe(long recipe_id);

    @Insert
    void insert(RecipeIngredient... recipeIngredients);
}
