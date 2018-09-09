package np.com.aanalbasaula.foodplanner.database;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.PrimaryKey;

import static android.arch.persistence.room.ForeignKey.CASCADE;

/**
 * Represents the ingredient used in a recipe. Also acts as the join table for the many to many
 * relationship
 */
@Entity(foreignKeys = {@ForeignKey(entity = Recipe.class, parentColumns = "id", childColumns = "recipe_id", onDelete = CASCADE),
        @ForeignKey(entity = Ingredient.class, parentColumns = "id", childColumns = "ingredient_id", onDelete = CASCADE)})
public class RecipeIngredient {
    @PrimaryKey(autoGenerate = true)
    private long id;

    @ColumnInfo(name = "recipe_id")
    private long recipeId;

    @ColumnInfo(name = "ingredient_id")
    private long ingredientId;
}