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

@Database(entities = {MealCourse.class, Recipe.class, ShoppingListEntry.class, Ingredient.class, RecipeStep.class}, version = 10)
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

    private static final Migration MIGRATION_6_7 = new Migration(6, 7) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE `Recipe` ADD COLUMN `imageName` TEXT");
        }
    };

    private static final Migration MIGRATION_7_8 = new Migration(7, 8) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("CREATE TABLE `RecipeStep` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `order` INTEGER NOT NULL, `description` TEXT, `recipeId` INTEGER NOT NULL, FOREIGN KEY (`recipeId`) REFERENCES `Recipe`(`id`) ON DELETE CASCADE)");
        }
    };

    private static final Migration MIGRATION_8_9 = new Migration(8, 9) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE `Recipe` ADD COLUMN `prep_time` INTEGER NOT NULL DEFAULT 0");
        }
    };

    private static final Migration MIGRATION_9_10 = new Migration(9, 10) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE `Recipe` ADD COLUMN `portion_size` INTEGER NOT NULL DEFAULT 0");
        }
    };

    private static AppDatabase instance;

    public static AppDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(), AppDatabase.class, "recipes.db")
                    .addMigrations(MIGRATION_4_5, MIGRATION_5_6, MIGRATION_6_7, MIGRATION_7_8, MIGRATION_8_9, MIGRATION_9_10)
                    .build();
        }
        return instance;
    }

    public abstract MealCourseDao getMealCourseDao();

    public abstract RecipeDao getRecipeDao();

    public abstract ShoppingListDao getShoppingListDao();

    public abstract  IngredientDao getIngredientDao();

    public abstract RecipeStepDao getRecipeStepDao();

}
