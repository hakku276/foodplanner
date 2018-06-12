package np.com.aanalbasaula.foodplanner.resources;

interface EntryConverter<T> {
    /**
     * Convert the raw entry into POJO
     *
     * @param entry the raw entry
     * @return the declared type
     */
    T convert(Entry entry);
}
