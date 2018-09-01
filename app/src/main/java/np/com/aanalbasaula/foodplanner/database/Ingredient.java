package np.com.aanalbasaula.foodplanner.database;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

/**
 * Represents an Ingredient that can be used in a recipe
 */
@Entity
public class Ingredient {

    /**
     * The identifier of the ingredient when on a database
     */
    @PrimaryKey(autoGenerate = true)
    private long id;

    /**
     * Defines the name of the Ingredient
     */
    @ColumnInfo(name = "name")
    private String name;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
