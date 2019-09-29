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

@Database(entities = {MealCourse.class, Recipe.class, ShoppingListEntry.class, Ingredient.class}, version = 6)
@TypeConverters({Converters.class})
public abstract class AppDatabase extends RoomDatabase {

    private static final Migration MIGRATION_4_5 = new Migration(4, 5) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("CREATE TABLE `Ingredient` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `name` TEXT, `recipeId` INTEGER NOT NULL, FOREIGN KEY (`recipeId`) REFERENCES `Recipe`(`id`) ON DELETE CASCADE)");
        }
    };

    private static final Migration MIGRATION_5_6 = new Migration(5, 6) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE `MealCourse` ADD COLUMN `recipeId` INTEGER REFERENCES Recipe(id) ON DELETE SET NULL");
        }
    };

    private static AppDatabase instance;

    public static AppDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(), AppDatabase.class, "recipes.db")
                    .addMigrations(MIGRATION_4_5, MIGRATION_5_6)
                    .build();
        }
        return instance;
    }

    public abstract MealCourseDao getMealCourseDao();

    public abstract RecipeDao getRecipeDao();

    public abstract ShoppingListDao getShoppingListDao();

    public abstract  IngredientDao getIngredientDao();

}
