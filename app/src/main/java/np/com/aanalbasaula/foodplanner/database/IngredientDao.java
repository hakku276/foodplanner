package np.com.aanalbasaula.foodplanner.database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

/**
 * The Data access object interface for Ingredients
 */
@Dao
public interface IngredientDao {
    @Insert
    long[] insert(Ingredient... ingredients);

    @Update
    void update(Ingredient... ingredients);

    @Delete
    void delete(Ingredient... ingredients);

    @Query("SELECT * from Ingredient")
    List<Ingredient> getAllIngredients();
}
