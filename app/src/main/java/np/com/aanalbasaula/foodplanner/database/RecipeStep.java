package np.com.aanalbasaula.foodplanner.database;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.PrimaryKey;

import lombok.Data;

@Entity(foreignKeys = {@ForeignKey(entity = Recipe.class, parentColumns = "id", childColumns = "recipeId", onDelete = ForeignKey.CASCADE)})
@Data
public class RecipeStep {

    @PrimaryKey(autoGenerate = true)
    private long id;

    // The order number of the step.
    private int order;

    // The content of the step
    private String description;

    // The id of the recipe this ingredient belongs to
    private long recipeId;

}
