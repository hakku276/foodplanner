package np.com.aanalbasaula.foodplanner.database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;

@Dao
public interface RecipeIngredientDao {
    @Insert
    void insert(RecipeIngredient... recipeIngredients);
}
