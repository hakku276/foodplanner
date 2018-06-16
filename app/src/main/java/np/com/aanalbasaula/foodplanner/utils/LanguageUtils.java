package np.com.aanalbasaula.foodplanner.utils;

import java.util.Locale;

public class LanguageUtils {

    /**
     * Get the current default language code for the jvm
     *
     * @return the language code in english (IETF)
     */
    public static String getCurrentIETFLanguageCode() {
        return Locale.getDefault().toString();
    }

    /**
     * Get the current default JVM language in ISO639 format
     *
     * @return the language code in english (ISO639)
     */
    public static String getISO639LanguageCode() {
        String ietf = Locale.getDefault().toString();
        StringBuilder builder = new StringBuilder(3);
        for (int i = 0; i < ietf.length(); i++) {
            // break once you have faced an underscore ex: en_US, drops the US
            if (ietf.charAt(i) == '_') {
                break;
            }
            builder.append(ietf.charAt(i));
        }
        return builder.toString();
    }
}
