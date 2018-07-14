package np.com.aanalbasaula.foodplanner.resources;

import android.content.Context;

/**
 * A Resource Manager class which is responsible for managing all the Resource related tasks.
 * An invocation on {@link ResourceManager#init(Context)} will initialize the ResourceManager.
 * All the resources will be loaded into memory if not done so. But, if the resources have already
 * been loaded, then does nothing. Make a call to {@link ResourceManager#reload(Context)} to
 * reload the resources after initialization.
 * <p>
 * Any new resource type should be registered into the {@link ResourceType} enumeration and then
 * a ResourceRegistry should be added here to hold the resources. And finally the init and the
 * reload methods should be implemented in order to load the resource from the file and reload it
 * as necessary.
 */
public class ResourceManager {
//    private static ResourceRegistry<IngredientType> ingredientTypes;

    /**
     * Initialize the Resource Manager by loading all the required resources into memory.
     *
     * @param context the calling context
     */
    public static void init(final Context context) {
//        if (ingredientTypes == null) {
//            ingredientTypes = new ResourceRegistry<>(context, ResourceType.INGREDIENT_TYPE);
//        }
    }

    /**
     * Reloads the data held by the Resource Manager
     *
     * @param context the calling context
     */
    public static void reload(final Context context) {
        throw new UnsupportedOperationException("Currently reloading the resource at runtime is not supported");
    }

    /**
     * Get the ingredient type for the defined id
     *
     * @param id the identifier for an Ingredient type resource; requires: the id to be a valid identifier
     * @return the IngredientType
     */
//    IngredientType ingredientType(final int id) {
//        return ingredientTypes.get(id);
//    }

}
