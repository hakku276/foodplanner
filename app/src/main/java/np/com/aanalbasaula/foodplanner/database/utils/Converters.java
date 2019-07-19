package np.com.aanalbasaula.foodplanner.database.utils;

import android.arch.persistence.room.TypeConverter;
import android.arch.persistence.room.TypeConverters;

import java.util.Date;

import np.com.aanalbasaula.foodplanner.database.MealType;

/**
 * All the possible type converters to store and retrieve database information
 */
public class Converters {

    @TypeConverter
    public static Date fromTimestamp(Long value) {
        return value == null ? null : new Date(value);
    }

    @TypeConverter
    public static Long dateToTimestamp(Date date) {
        return date == null ? null : date.getTime();
    }

    @TypeConverter
    public static String fromMealType(MealType type) {
        return type == null ? null : type.name();
    }

    @TypeConverter
    public static MealType toMealType(String mealType) {
        return mealType == null ? null: MealType.valueOf(mealType);
    }

}
