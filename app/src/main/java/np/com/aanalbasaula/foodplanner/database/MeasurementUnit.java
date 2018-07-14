package np.com.aanalbasaula.foodplanner.database;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity
public class MeasurementUnit {

    /**
     * The identifier for the measurement unit
     */
    @PrimaryKey(autoGenerate = true)
    private int id;

    /**
     * The name of the measurement unit
     */
    @ColumnInfo(name = "name")
    private String name;

    /**
     * The short symbol for the measurement unit
     */
    @ColumnInfo(name =  "symbol")
    private String symbol;

    public String getName() {
        return name;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }
}
