package np.com.aanalbasaula.foodplanner.database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

@Dao
public interface MealCourseDao {

    @Insert
    long[] insert(MealCourse... items);

    @Delete
    void delete(MealCourse... items);

    @Update
    void update(MealCourse... items);

    @Query("SELECT * FROM MealCourse")
    List<MealCourse> getAllMealCourses();

    // cast((julianday('now') - 2440587.5)*86400000 as integer) provides current epoch time
    // The date should be greater than yesterday therefore the extra -1
    @Query("SELECT * FROM MealCourse WHERE date > cast((julianday('now') - 2440587.5 - 1)*86400000 as integer) order by type asc, date asc")
    List<MealCourse> getAllMealCoursesInFuture();
}
