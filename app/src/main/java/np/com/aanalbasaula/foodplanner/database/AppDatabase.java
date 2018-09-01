package np.com.aanalbasaula.foodplanner.database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import java.net.ContentHandler;

@Database(entities = {Recipe.class, Ingredient.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {

    private static AppDatabase instance;

    public abstract RecipeDao getRecipeDao();
    public abstract IngredientDao getIngredientDao();

    public static AppDatabase getInstance(Context context){
        if(instance == null){
            instance = Room.databaseBuilder(context.getApplicationContext(), AppDatabase.class, "recipes.db").build();
        }
        return instance;
    }
}
