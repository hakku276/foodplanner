package np.com.aanalbasaula.foodplanner.database;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.PrimaryKey;

import lombok.Data;

@Entity(foreignKeys = {@ForeignKey(entity = Recipe.class, parentColumns = "id", childColumns = "recipeId", onDelete = ForeignKey.CASCADE)})
@Data
public class Ingredient {

    @PrimaryKey(autoGenerate = true)
    private long id;

    private String name;

    // The id of the recipe this ingredient belongs to
    private long recipeId;

}
