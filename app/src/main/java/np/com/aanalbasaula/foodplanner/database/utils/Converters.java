package np.com.aanalbasaula.foodplanner.database.utils;

import android.arch.persistence.room.TypeConverter;
import android.arch.persistence.room.TypeConverters;
import android.os.Parcel;
import android.os.ParcelUuid;

import org.apache.commons.lang3.StringUtils;

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
        return StringUtils.isBlank(mealType) ? null: MealType.valueOf(mealType);
    }

    @TypeConverter
    public static ParcelUuid toUUID(String UUID) {
        if (UUID == null || UUID.isEmpty()) {
            return null;
        }

        return ParcelUuid.fromString(UUID);
    }

    @TypeConverter
    public static String fromUUID(ParcelUuid uuid) {
        if (uuid == null) {
            return null;
        }

        return uuid.toString();
    }

}
