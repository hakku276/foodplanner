package np.com.aanalbasaula.foodplanner.database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

@Dao
public interface RecipeDao {
    @Insert
    long[] insert(Recipe... recipes);

    @Delete
    void delete(Recipe... recipes);

    @Update
    void update(Recipe... recipes);

    @Query("SELECT * FROM Recipe")
    List<Recipe> getAllRecipes();

}
