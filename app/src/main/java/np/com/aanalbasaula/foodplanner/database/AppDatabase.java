package np.com.aanalbasaula.foodplanner.database;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;
import android.arch.persistence.room.migration.Migration;
import android.content.Context;
import android.support.annotation.NonNull;

import np.com.aanalbasaula.foodplanner.database.utils.Converters;

@Database(entities = {MealCourse.class, Recipe.class, ShoppingListEntry.class}, version = 4)
@TypeConverters({Converters.class})
public abstract class AppDatabase extends RoomDatabase {

    private static final Migration MIGRATION_2_3 = new Migration(2, 3) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("CREATE TABLE `ShoppingListEntry` (`id` INTEGER NOT NULL, `name` TEXT, PRIMARY KEY(`id`))");
        }
    };

    private static final Migration MIGRATION_3_4 = new Migration(3,4) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE `ShoppingListEntry` ADD COLUMN `selected` INTEGER NOT NULL DEFAULT 0");
        }
    };

    private static AppDatabase instance;

    public static AppDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(), AppDatabase.class, "recipes.db")
                    .addMigrations(MIGRATION_2_3, MIGRATION_3_4)
                    .build();
        }
        return instance;
    }

    public abstract MealCourseDao getMealCourseDao();

    public abstract RecipeDao getRecipeDao();

    public abstract ShoppingListDao getShoppingListDao();

}
