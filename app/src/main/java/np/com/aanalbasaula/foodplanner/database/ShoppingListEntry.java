package np.com.aanalbasaula.foodplanner.database;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Data
@EqualsAndHashCode(of = {"name"})
public class ShoppingListEntry {

    @PrimaryKey(autoGenerate = true)
    private long id;

    @ColumnInfo(name = "name")
    private String name;

    @ColumnInfo(name = "selected")
    private boolean selected;

    public ShoppingListEntry() {
        // empty constructor
    }
}
