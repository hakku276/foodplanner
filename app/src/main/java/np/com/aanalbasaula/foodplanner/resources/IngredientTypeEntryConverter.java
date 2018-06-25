package np.com.aanalbasaula.foodplanner.resources;

import np.com.aanalbasaula.foodplanner.model.IngredientType;
import np.com.aanalbasaula.foodplanner.utils.NumberUtils;

public class IngredientTypeEntryConverter implements EntryConverter<IngredientType> {

    private static final int ENTRY_SIZE = 16;
    private static final int INDEX_SIZE = 4;
    private static final int DATA_SIZE = ENTRY_SIZE - INDEX_SIZE;

    IngredientTypeEntryConverter() {
    }

    @Override
    public IngredientType convert(Entry entry) {
        int id = NumberUtils.byteArrayToInt(entry.getData(), 0);
        String name = new String(entry.getData(), INDEX_SIZE, DATA_SIZE);
        IngredientType type = new IngredientType(id, name);
        return type;
    }

}
