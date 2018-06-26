package np.com.aanalbasaula.foodplanner.resources;

/**
 * A converter which converts the raw {@link Entry} to the defined type T
 *
 * @param <T> the Type to which this converter converts the entry to;
 *            requires: The Type should extend Resource
 */
interface EntryConverter<T extends Resource> {
    /**
     * Convert the raw entry into POJO
     *
     * @param entry the raw entry
     * @return the declared type
     */
    T convert(Entry entry);
}
