package np.com.aanalbasaula.foodplanner.database;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

/**
 * Represents a recipe created by the user
 */
@Entity
public class Recipe {

    /**
     * The unique identifier of the recipe
     */
    @PrimaryKey(autoGenerate = true)
    private long id;

    /**
     * The name of the recipe
     */
    @ColumnInfo(name = "name")
    private String name;

    public Recipe(){

    }

    public Recipe(String name){
        this.name = name;
    }

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
