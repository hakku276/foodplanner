package np.com.aanalbasaula.foodplanner.resources;

import np.com.aanalbasaula.foodplanner.utils.LanguageUtils;

enum ResourceType {
    INGREDIENTS("ingredients", new BlockResourceFileReader(16), new IngredientTypeEntryConverter());

    private final String filename;
    private final ResourceFileReader fileReader;
    private final EntryConverter converter;

    ResourceType(String filename, ResourceFileReader fileReader, EntryConverter converter){
        this.filename = filename;
        this.fileReader = fileReader;
        this.converter =converter;
    }

    public String getFilename() {
        return filename + "-" + LanguageUtils.getISO639LanguageCode() + ".dat";
    }

    public String getDefaultFilename(){
        return filename+"-en.dat";
    }

    public ResourceFileReader getFileReader() {
        return fileReader;
    }

    public EntryConverter getConverter() {
        return converter;
    }
}
