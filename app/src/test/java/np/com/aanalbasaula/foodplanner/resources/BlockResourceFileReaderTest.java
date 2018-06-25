package np.com.aanalbasaula.foodplanner.resources;

import android.content.Context;
import android.content.res.AssetManager;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Target Class: {@linkplain BlockResourceFileReader}
 */
public class BlockResourceFileReaderTest {

    private BlockResourceFileReader resourceReader;
    private Context mockContext;
    private AssetManager mockAssetManager;
    private InputStream fileStream;

    @Before
    public void setup() throws IOException {
        resourceReader = new BlockResourceFileReader(16);
        mockContext = Mockito.mock(Context.class);
        mockAssetManager = Mockito.mock(AssetManager.class);

        try {
            fileStream = new FileInputStream("./src/main/assets/test_blockfile_16.dat");
        } catch (IOException e) {
            e.printStackTrace();
        }

        when(mockContext.getAssets()).thenReturn(mockAssetManager);
        when(mockAssetManager.open("test_blockfile_16.dat")).thenReturn(fileStream);
    }

    @After
    public void tearDown() {
        resourceReader.close();
    }

    /**
     * Test whether the file loads or not and whether the mocks are being used or not
     *
     * @throws Exception in case of file handling exceptions
     */
    @Test
    public void testFileLoad_shouldNotThrowException() throws Exception {
        assertTrue(resourceReader.open(mockContext, "test_blockfile_16.dat"));
        assertTrue(resourceReader.hasNextEntry());

        verify(mockAssetManager, times(1)).open("test_blockfile_16.dat");
        verify(mockContext, times(1)).getAssets();
    }

    /**
     * Test the block read in the file, original file should contain only 64 bytes with 16 blocks each
     * numbered from 0 to 63. Test whether it loads or not
     *
     * @throws Exception in case of file handling issues
     */
    @Test
    public void testBlocks_shouldHaveIncreasingNumbers() throws Exception {
        assertTrue(resourceReader.open(mockContext, "test_blockfile_16.dat"));

        assertEquals(4, resourceReader.count());

        int i = 0;
        while (resourceReader.hasNextEntry()) {
            Entry block = resourceReader.nextEntry();
            for (byte b : block.getData()) {
                assertEquals(i, b);
                i++;
            }
        }
    }

}
