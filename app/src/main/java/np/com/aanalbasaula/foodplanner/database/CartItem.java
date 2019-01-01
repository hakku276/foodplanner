package np.com.aanalbasaula.foodplanner.database;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

/**
 * Represents a single item on the Shopping cart.
 */
@Entity
public class CartItem {

    /**
     * The identifier of the Card Item
     */
    @PrimaryKey(autoGenerate = true)
    private long id;

    /**
     * The name of the item in the Cart
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
