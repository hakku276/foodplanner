package np.com.aanalbasaula.foodplanner.resources;

import np.com.aanalbasaula.foodplanner.utils.NumberUtils;

/**
 * Converts the ingredient type raw {@link Entry} into {@link IngredientType} stored in a block resource file
 */
class BlockFileIngredientTypeEntryConverter implements EntryConverter<Resource> {

    /**
     * The size of the block in the file
     */
    private static final int ENTRY_SIZE = 16;

    /**
     * The number of bytes taken up by the index within the block
     */
    private static final int INDEX_SIZE = 4;

    /**
     * The Size allocated to store Ingredient Type data
     */
    private static final int DATA_SIZE = ENTRY_SIZE - INDEX_SIZE;

    BlockFileIngredientTypeEntryConverter() {
    }

    @Override
    public Resource convert(Entry entry) {
        int id = NumberUtils.byteArrayToInt(entry.getData(), 0);
        String name = new String(entry.getData(), INDEX_SIZE, DATA_SIZE);
        return new Resource(id);
    }

}
