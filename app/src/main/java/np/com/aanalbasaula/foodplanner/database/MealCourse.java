package np.com.aanalbasaula.foodplanner.database;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.PrimaryKey;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

import lombok.Data;

/**
 * A food item that is eaten in a meal. For instance, Lunch contains: Lasagna, Salad.
 * Lasagna and Salad are each a single MealCourse.
 */
@Entity(foreignKeys = {@ForeignKey(entity = Recipe.class, parentColumns = "id", childColumns = "recipeId", onDelete = ForeignKey.SET_NULL)})
@Data
public class MealCourse implements Parcelable {

    private static final int TRUE = 1;
    private static final int FALSE = 0;

    @PrimaryKey(autoGenerate = true)
    private long id;

    @ColumnInfo(name = "date")
    private Date date;

    @ColumnInfo(name = "name")
    private String name;

    @ColumnInfo(name = "type")
    private MealType type;

    @ColumnInfo(name = "recipeId")
    private Long recipeId;

    public MealCourse() {
        // empty constructor
    }

    protected MealCourse(Parcel in) {
        id = in.readLong();
        name = in.readString();
        type = MealType.valueOf(in.readString());
        int recipeIdNotNull = in.readInt();
        if (recipeIdNotNull == TRUE) {
            recipeId = in.readLong();
        }
    }

    public static final Creator<MealCourse> CREATOR = new Creator<MealCourse>() {
        @Override
        public MealCourse createFromParcel(Parcel in) {
            return new MealCourse(in);
        }

        @Override
        public MealCourse[] newArray(int size) {
            return new MealCourse[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeLong(id);
        parcel.writeString(name);
        parcel.writeString(type.name());
        parcel.writeInt(recipeId != null ? TRUE : FALSE); // is recipe ID not null
        if (recipeId != null) {
            parcel.writeLong(recipeId);
        }
    }

}
