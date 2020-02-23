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
     *  The name of the image file for this recipe
     */
    @ColumnInfo(name = "imageName")
    private ParcelUuid imageName;

    @Ignore
    private List<Ingredient> ingredients;

    public Recipe() {
        // required empty constructor by the Room Framework
    }

    protected Recipe(Parcel in) {
        id = in.readLong();
        name = in.readString();
        imageName = in.readParcelable(ParcelUuid.class.getClassLoader());
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
    }
}
