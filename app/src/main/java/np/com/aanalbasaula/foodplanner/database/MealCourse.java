package np.com.aanalbasaula.foodplanner.database;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import java.util.Date;

import lombok.Data;

/**
 * A food item that is eaten in a meal. For instance, Lunch contains: Lasagna, Salad.
 * Lasagna and Salad are each a single MealCourse.
 */
@Entity
@Data
public class MealCourse {

    @PrimaryKey(autoGenerate = true)
    private long id;

    @ColumnInfo(name = "date")
    private Date date;

    @ColumnInfo(name = "name")
    private String name;

    @ColumnInfo(name = "type")
    private MealType type;
}
