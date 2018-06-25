package np.com.aanalbasaula.foodplanner.resources;

import org.junit.Before;
import org.junit.Test;

import np.com.aanalbasaula.foodplanner.model.IngredientType;

import static org.junit.Assert.*;

/**
 * Target: {@linkplain IngredientTypeEntryConverter}
 */
public class IngredientTypeEntryConverterTest {

    private static final byte[] fakeData = {0x01, 0x00, 0x00, 0x00, 0x53, 0x70, 0x69, 0x63, 0x65, 0x73, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00};

    private static final int ID = 1;
    private static final String PAYLOAD = "Spices";

    private IngredientTypeEntryConverter converter;

    @Before
    public void setup(){
        converter = new IngredientTypeEntryConverter();
    }

    @Test
    public void testDataConversion(){
        Entry entry = new Entry();
        entry.setData(fakeData);

        IngredientType ingredientType = converter.convert(entry);

        assertEquals(ID, ingredientType.getId());
        assertEquals(PAYLOAD, ingredientType.getName());
    }
}
