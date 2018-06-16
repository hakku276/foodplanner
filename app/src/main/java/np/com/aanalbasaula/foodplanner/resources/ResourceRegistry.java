package np.com.aanalbasaula.foodplanner.resources;

import android.content.Context;
import android.util.Log;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

/**
 * A generic class to hold all the resource IDs mapped to its resource
 *
 * @param <T> the type of resource held by this registry
 */
class ResourceRegistry<T extends Resource> {
    private static final String TAG = ResourceRegistry.class.getSimpleName();


    private final List<T> resources;

    private ResourceRegistry(Context context, ResourceType type) {
        EntryConverter<T> converter = (EntryConverter<T>) type.getConverter();
        ResourceFileReader resourceReader = type.getFileReader();
        String filename = type.getFilename();
        List<T> resources = new ArrayList<>(0);
        ;
        try {
            if (resourceReader.open(context, filename)) {
                resources = new ArrayList<>(resourceReader.count());
            }
        } catch (FileNotFoundException e) {
            Log.e(TAG, "ResourceRegistry: Could not find file " + filename, e);
            Log.i(TAG, "ResourceRegistry: Trying to load default resource file rather than localized file");
            try {
                filename = type.getDefaultFilename();
                if (resourceReader.open(context, filename)) {
                    resources = new ArrayList<>(resourceReader.count());
                }
            } catch (FileNotFoundException ex) {
                Log.e(TAG, "ResourceRegistry: Could not even load default resource file", ex);
            }
        }
        this.resources = resources;
        loadResources(resourceReader, converter);
    }

    /**
     * Load the resources from the FileReader into the list
     *
     * @param reader    the reader, opened and untouched
     * @param converter the entry to type converter
     */
    private void loadResources(ResourceFileReader reader, EntryConverter<T> converter) {
        while (reader.hasNextEntry()) {
            Entry entry = reader.nextEntry();
            T object = converter.convert(entry);
            resources.add(object);
        }
    }

    T get(int index) {
        return (T) resources.get(index).duplicate();
    }
}
