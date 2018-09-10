package np.com.aanalbasaula.foodplanner.database;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.migration.Migration;
import android.content.Context;

@Database(entities = {Recipe.class, Ingredient.class, RecipeIngredient.class}, version = 2)
public abstract class AppDatabase extends RoomDatabase {

    private static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("Create table RecipeIngredient(\n" +
                    "\t`id` INTEGER NOT NULL,\n" +
                    "\t`recipe_id` INTEGER NOT NULL,\n" +
                    "\t`ingredient_id` INTEGER NOT NULL,\n" +
                    "\tPRIMARY KEY(`id`),\n" +
                    "\tFOREIGN KEY(`recipe_id`) REFERENCES Recipe(`id`) ON DELETE CASCADE,\n" +
                    "\tFOREIGN KEY(`ingredient_id`) REFERENCES Ingredient(`id`) ON DELETE CASCADE\n" +
                    ")");
        }
    };
    private static AppDatabase instance;

    public static AppDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(), AppDatabase.class, "recipes.db").addMigrations(MIGRATION_1_2).build();
        }
        return instance;
    }

    public abstract RecipeDao getRecipeDao();

    public abstract IngredientDao getIngredientDao();

    public abstract RecipeIngredientDao getRecipeIngredientDao();
}
