package np.com.aanalbasaula.foodplanner;

import org.junit.Test;

import np.com.aanalbasaula.foodplanner.utils.LanguageUtils;

import static org.junit.Assert.assertEquals;

public class LanguageUtilsTest {

    @Test
    public void testGetCurrentIETFLanguage_shouldReturnEN_US() {
        assertEquals("en_US", LanguageUtils.getCurrentIETFLanguageCode());
    }

    @Test
    public void testGetCurrentISO639LanguageCode_shouldReturnEN(){
        assertEquals("en", LanguageUtils.getISO639LanguageCode());
    }
}
