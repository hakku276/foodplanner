package np.com.aanalbasaula.foodplanner.database;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.migration.Migration;
import android.content.Context;
import android.support.annotation.NonNull;

@Database(entities = {Recipe.class, Ingredient.class, RecipeIngredient.class, CartItem.class}, version = 4)
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

    private static final Migration MIGRATION_2_3 = new Migration(2, 3) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("CREATE TABLE CartItem(" +
                    "`id` INTEGER NOT NULL, " +
                    "`name` TEXT, " +
                    "PRIMARY KEY(`id`))");
        }
    };

    private static final Migration MIGRATION_3_4 = new Migration(3, 4) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE CartItem ADD COLUMN `active` INTEGER DEFAULT 0");
        }
    };
    private static AppDatabase instance;

    public static AppDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(), AppDatabase.class, "recipes.db").addMigrations(MIGRATION_3_4).build();
        }
        return instance;
    }

    public abstract RecipeDao getRecipeDao();

    public abstract IngredientDao getIngredientDao();

    public abstract RecipeIngredientDao getRecipeIngredientDao();

    public abstract  ShoppingCartDao getShoppingCartDao();
}
