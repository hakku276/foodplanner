package np.com.aanalbasaula.foodplanner.database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

@Dao
public interface ShoppingListDao {

    @Insert
    long[] insert(ShoppingListEntry... entries);

    @Delete
    void delete(ShoppingListEntry... entries);

    @Update
    void update(ShoppingListEntry... entries);

    @Query("SELECT * from ShoppingListEntry")
    List<ShoppingListEntry> getShoppingList();

}
