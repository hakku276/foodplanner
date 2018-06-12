package np.com.aanalbasaula.foodplanner.resources;

import android.content.Context;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class BlockFileReader implements ResourceFileReader {

    private static final String TAG = BlockFileReader.class.getSimpleName();
    /**
     * The size of the block in the file to read
     */
    private final int mBlockSize;
    /**
     * The buffered input stream to read from
     */
    private BufferedInputStream mStream;
    private long mFileSize;

    BlockFileReader(int blockSize) {
        this.mBlockSize = blockSize;
    }

    @Override
    public boolean open(Context context, String filename) throws FileNotFoundException {
        boolean success = false;
        try {
            mStream = new BufferedInputStream(context.getAssets().open(filename));
            success = true;
            mFileSize = mStream.available();
        } catch (IOException e) {
            if (e instanceof FileNotFoundException) {
                throw (FileNotFoundException) e;
            }
            if (mStream != null) {
                try {
                    mStream.close();
                } catch (IOException ex) {
                    Log.e(TAG, "open: The stream could not be closed after facing exception", ex);
                    Log.e(TAG, "open: Cause of initial failure", e);
                }
                mStream = null;
            }
        }
        return success;
    }

    @Override
    public boolean close() {
        return false;
    }

    @Override
    public long fileSize() {
        return mFileSize;
    }

    @Override
    public Entry nextEntry() throws IOException {
        Entry raw = new Entry();
        if (mStream.available() >= mBlockSize) {
            byte[] buffer = new byte[mBlockSize];
            //if the read is less than expected, then return a null
            if (mStream.read(buffer) < mBlockSize) {
                return null;
            }
            raw.setData(buffer);
            return raw;
        } else {
            return null;
        }
    }

    @Override
    public boolean hasNextEntry() {
        if (mStream != null) {
            try {
                return mStream.available() >= mBlockSize;
            } catch (IOException e) {
                Log.e(TAG, "hasNextEntry: Could not check available size in stream", e);
            } finally {
                try {
                    mStream.close();
                } catch (IOException e) {
                    Log.e(TAG, "hasNextEntry: Failed to close stream after error", e);
                }
                mStream = null;
            }
        }
        return false;
    }
}
