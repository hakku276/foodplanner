package np.com.aanalbasaula.foodplanner.resources;

import android.content.Context;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * A Resource file reader that accesses the file in blocks of defined sizes
 */
public class BlockResourceFileReader implements ResourceFileReader {

    private static final String TAG = BlockResourceFileReader.class.getSimpleName();
    /**
     * The size of the block in the file to read
     */
    private final int mBlockSize;
    /**
     * The buffered input stream to read from
     */
    private BufferedInputStream mStream;
    private long mFileSize;

    BlockResourceFileReader(int blockSize) {
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
    public int count() {
        return (int) (mFileSize / mBlockSize);
    }

    @Override
    public Entry nextEntry() {
        Entry raw = new Entry();
        try {
            if (mStream != null && mStream.available() >= mBlockSize) {
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
        } catch (IOException e) {
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
        return null;
    }

    @Override
    public boolean hasNextEntry() {
        if (mStream != null) {
            try {
                return mStream.available() >= mBlockSize;
            } catch (IOException e) {
                Log.e(TAG, "hasNextEntry: Could not check available size in stream", e);
                try {
                    mStream.close();
                } catch (IOException ex) {
                    Log.e(TAG, "hasNextEntry: Failed to close stream after error", ex);
                }
                mStream = null;
            }
        }
        return false;
    }
}
