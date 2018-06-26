package np.com.aanalbasaula.foodplanner.resources;

import android.content.Context;
import android.content.res.AssetManager;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.io.FileInputStream;
import java.io.IOException;

import np.com.aanalbasaula.foodplanner.model.IngredientType;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

public class ResourceRegistryTest {

    private ResourceRegistry<IngredientType> registry;
    private FileInputStream fileStream;

    @Before
    public void setup() throws IOException {
        Context context = Mockito.mock(Context.class);
        AssetManager manager = Mockito.mock(AssetManager.class);
        try {
            fileStream = new FileInputStream("./src/main/assets/test_ingredient_types.dat");
        } catch (IOException e) {
            e.printStackTrace();
        }
        when(manager.open(anyString())).thenReturn(fileStream);
        when(context.getAssets()).thenReturn(manager);
        registry = new ResourceRegistry<>(context, ResourceType.INGREDIENT_TYPE);
    }

    @Test
    public void testResourcesGotLoaded() {
        assertEquals(4, registry.count());

        assertEquals(1, registry.get(1).getId());
        assertEquals("Spices", registry.get(1).getName());
        assertEquals(4, registry.get(4).getId());
        assertEquals("Berry", registry.get(4).getName());
    }

    @Test
    public void testUnmodifiability() {
        assertEquals(1, registry.get(1).getId());
        assertEquals("Spices", registry.get(1).getName());

        IngredientType type = registry.get(1);
        type = new IngredientType(5, "False");

        assertEquals(1, registry.get(1).getId());
        assertEquals("Spices", registry.get(1).getName());
    }

}
