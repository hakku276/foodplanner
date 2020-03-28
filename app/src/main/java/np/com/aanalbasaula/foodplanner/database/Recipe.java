package np.com.aanalbasaula.foodplanner.database;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.os.Parcel;
import android.os.ParcelUuid;
import android.os.Parcelable;

import java.util.List;
import java.util.UUID;

import lombok.Data;

/**
 * A recipe representation that a user can add to the cookbook.
 */
@Entity
@Data
public class Recipe implements Parcelable {

    @PrimaryKey(autoGenerate = true)
    private long id;

    @ColumnInfo(name = "name")
    private String name;

    /**
     * The total preparation time in minutes
     */
    @ColumnInfo(name = "prep_time")
    private int preparationTime;

    /**
     * The total portion size in people
     */
    @ColumnInfo(name = "portion_size")
    private int portionSize;

    /**
     *  The name of the image file for this recipe
     */
    @ColumnInfo(name = "imageName")
    private ParcelUuid imageName;

    @Ignore
    // NOTE: will not be loaded eagerly, try loading using IngredientsDAO, a Foreign key reference is available in ingredients
    private List<Ingredient> ingredients;

    @Ignore
    // NOTE: will not be loaded eagerly, try loading using RecipeStepsDAO, a Foreign key reference is available in recipeSteps
    private List<RecipeStep> recipeSteps;

    public Recipe() {
        // required empty constructor by the Room Framework
    }

    protected Recipe(Parcel in) {
        id = in.readLong();
        name = in.readString();
        imageName = in.readParcelable(ParcelUuid.class.getClassLoader());
        preparationTime = in.readInt();
    }

    public static final Creator<Recipe> CREATOR = new Creator<Recipe>() {
        @Override
        public Recipe createFromParcel(Parcel in) {
            return new Recipe(in);
        }

        @Override
        public Recipe[] newArray(int size) {
            return new Recipe[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(name);
        dest.writeParcelable(imageName, flags);
        dest.writeInt(preparationTime);
    }
}
