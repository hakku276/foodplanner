package np.com.aanalbasaula.foodplanner.utils;

public class NumberUtils {

    /**
     * Converts the provided byte array into an integer
     * Requires: byte array expects little endian format, minimum 4 bytes available from offset
     *
     * @param data   the byte array to parse into integer
     * @param offset the offset from where the parsing should start
     * @return the integer value as converted from the start point
     */
    public static int byteArrayToInt(byte[] data, int offset) {
        return (data[offset] & 0xff) | ((data[offset + 1] & 0xff) << 8) | ((data[offset + 2] & 0xff) << 16) | ((data[offset + 3] & 0xff) << 24);
    }
}
