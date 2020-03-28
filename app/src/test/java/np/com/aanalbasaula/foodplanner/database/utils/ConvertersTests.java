package np.com.aanalbasaula.foodplanner.database.utils;

import static org.junit.Assert.*;

import org.junit.Test;
import org.mockito.internal.util.collections.ListUtil;

import java.util.Date;

import np.com.aanalbasaula.foodplanner.database.MealType;

/**
 * Tests related to Database converters
 */
public class ConvertersTests {

    /**
     * Simple conversion from date to timestamp
     */
    @Test
    public void testDateToTimestamp() {
        Date date = new Date();
        long timestamp = Converters.dateToTimestamp(date);
        assertEquals(date.getTime(), timestamp);
    }

    /**
     * A test case when a null date object is retuned
     */
    @Test
    public void testNullDateToTimestamp() {
        Long timestamp = Converters.dateToTimestamp(null);
        assertNull(timestamp);
    }

    /**
     * A simple conversion from timestamp to date
     */
    @Test
    public void testTimestampToDate() {
        long timestamp = 100000000L;
        Date date = Converters.fromTimestamp(timestamp);
        assertEquals(timestamp, date.getTime());
    }

    /**
     * A conversion from null timestamp to date
     */
    @Test
    public void testNullTimestampToDate() {
        Date date = Converters.fromTimestamp(null);
        assertNull(date);
    }

    /**
     * A simple conversion from meal type enum to String
     */
    @Test
    public void testMealTypeConverter() {
        MealType mealType = MealType.BREAKFAST;
        assertEquals(MealType.BREAKFAST.name(), Converters.fromMealType(mealType));
    }

    /**
     * Conversion of null meal type
     */
    @Test
    public void testNullMealTypeConverter() {
        assertNull(Converters.fromMealType(null));
    }

    /**
     * Simple conversion from String to Mealtype
     */
    @Test
    public void testStringToMealTypeConversion() {
        assertEquals(MealType.BREAKFAST, Converters.toMealType("BREAKFAST"));
    }

    /**
     * Conversion from Null String to Mealtype
     */
    @Test
    public void testNullStringToMealTypeConversion() {
        assertNull(Converters.toMealType(null));
    }

    /**
     * Conversion from Empty String to Mealtype
     */
    @Test
    public void testEmptyStringToMealTypeConversion() {
        assertNull(Converters.toMealType(""));
    }

}
