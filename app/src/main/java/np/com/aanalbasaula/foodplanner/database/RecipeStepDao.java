package np.com.aanalbasaula.foodplanner.database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

@Dao
public interface RecipeStepDao {
    @Insert
    long[] insert(RecipeStep... ingredients);

    @Delete
    void delete(RecipeStep... ingredients);

    @Update
    void update(RecipeStep... ingredients);

    @Query("SELECT * FROM RecipeStep where recipeId = :recipeId" )
    List<RecipeStep> getRecipeSteps(long recipeId);
}
