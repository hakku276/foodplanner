package np.com.aanalbasaula.foodplanner.resources;

/**
 * Represents a raw resource entry
 */
class Entry {

    /**
     * The raw stored data
     */
    private byte[] data;

    /**
     * Get the data held by the resource entry
     *
     * @return the data this entry represents
     */
    byte[] getData() {
        return data;
    }

    /**
     * Set the data parameter of the entry
     *
     * @param data the data that this entry represents
     */
    void setData(byte[] data) {
        this.data = data;
    }
}
