package np.com.aanalbasaula.foodplanner.utils;

import android.widget.DatePicker;

import java.util.Calendar;
import java.util.Date;

public class DateUtils {

    private DateUtils() {
        // empty constructor to hide implementation
    }

    /**
     * Extract the date from the date picker.
     * @param datePicker the date picker object where the user made a selection
     * @return
     */
    public static Date extractDateFromPicker(DatePicker datePicker) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(datePicker.getYear(), datePicker.getMonth(), datePicker.getDayOfMonth());
        return calendar.getTime();
    }
}
