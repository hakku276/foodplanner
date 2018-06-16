package np.com.aanalbasaula.foodplanner.resources;

import android.content.Context;

import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * The file reader interface to load the raw entries from the file into the memory
 */
interface ResourceFileReader {
    /**
     * Open the file for reading
     *
     * @param context  the context of the application
     * @param filename the name of the file in the assets directory
     * @return true if successful, else false
     * @throws FileNotFoundException if the file could not be found
     */
    boolean open(Context context, String filename) throws FileNotFoundException;

    /**
     * Close the file
     *
     * @return true if successful, else false
     */
    boolean close();

    /**
     * Get the size of the raw resource file
     *
     * @return the actual number of resource entries
     */
    long fileSize();

    /**
     * Get the number of items held by the resource file
     *
     * @return the positive integer count of files, 0 if file empty
     */
    int count();

    /**
     * Get the next entry from the file
     *
     * @return the next database entry, or null if there exists none
     * @throws IOException if the file could not be read
     */
    Entry nextEntry();

    /**
     * Check if the file has remaining entries to read
     *
     * @return true if there exists extra entries to read, else false
     */
    boolean hasNextEntry();
}
