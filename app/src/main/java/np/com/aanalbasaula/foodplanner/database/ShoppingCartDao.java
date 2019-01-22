package np.com.aanalbasaula.foodplanner.database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

@Dao
public interface ShoppingCartDao {

    @Insert
    long[] insert(CartItem... items);

    @Delete
    void delete(CartItem... items);

    @Update
    void update(CartItem... items);

    @Query("SELECT * FROM CartItem")
    List<CartItem> getAllCartItems();

    @Query("SELECT * FROM CartItem WHERE CartItem.active=1")
    List<CartItem> getActiveCartItems();
}
