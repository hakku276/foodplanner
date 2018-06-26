package np.com.aanalbasaula.foodplanner.resources;

import np.com.aanalbasaula.foodplanner.utils.LanguageUtils;

/**
 * Register the resource types here with their individual loading and decoding mechanism.
 * This should be used to decouple the loading of the file into raw data and then into an
 * Object. One can allocate different strategies for loading and converting the files using
 * these entries
 * <p>
 * Refer {@link ResourceManager} on adding and maintaining ResourceTypes
 */
enum ResourceType {
    INGREDIENT_TYPE("ingredients_types", new BlockResourceFileReader(16), new BlockFileIngredientTypeEntryConverter());

    /**
     * The name of the file to target (without extension or language specification)
     */
    private final String filename;

    /**
     * The File reader, depending on what format the file is saved in
     */
    private final ResourceFileReader fileReader;

    /**
     * The raw data to Object converter for the resource type
     */
    private final EntryConverter converter;

    /**
     * Define a new resource type
     *
     * @param filename   the target file to load, without extension and language specification; requires:
     *                   the files to be saved in a dat format
     * @param fileReader the reader which can read this file and extract raw data
     * @param converter  the converter which converts raw data into java objects
     */
    ResourceType(String filename, ResourceFileReader fileReader, EntryConverter converter) {
        this.filename = filename;
        this.fileReader = fileReader;
        this.converter = converter;
    }

    /**
     * Get the language specific filename
     *
     * @return filename including the language and the extension dat
     */
    public String getFilename() {
        return filename + "-" + LanguageUtils.getISO639LanguageCode() + ".dat";
    }

    /**
     * Get the default filename
     *
     * @return default english filename
     */
    public String getDefaultFilename() {
        return filename + "-en.dat";
    }

    /**
     * The file reader which is capable of reading the file
     *
     * @return the file reader
     */
    public ResourceFileReader getFileReader() {
        return fileReader;
    }

    /**
     * The entry converter which is capable of converting the raw data provided
     * by the {@link ResourceFileReader}
     *
     * @return the converter
     */
    public EntryConverter getConverter() {
        return converter;
    }
}
