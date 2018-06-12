package np.com.aanalbasaula.foodplanner.resources;

import java.nio.ByteBuffer;

/**
 * Represents a raw resource entry
 */
public class Entry {

    private byte[] data;

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }
}
