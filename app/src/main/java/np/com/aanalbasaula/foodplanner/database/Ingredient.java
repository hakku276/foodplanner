package np.com.aanalbasaula.foodplanner.database;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

/**
 * Represents an Ingredient Resource that can be used in a recipe, with all the details.
 */
@Entity
public class Ingredient {

    /**
     * The identifier of the ingredient when on a database
     */
    @PrimaryKey(autoGenerate = true)
    private int id;

    /**
     * Defines the name of the Ingredient
     */
    @ColumnInfo(name = "name")
    private String name;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
