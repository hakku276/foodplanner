package np.com.aanalbasaula.foodplanner.resources;

import android.content.Context;

import np.com.aanalbasaula.foodplanner.model.IngredientType;

public class ResourceManager {
    private static ResourceRegistry<IngredientType> ingredientTypes;

    public static void init(Context context) {
        if (ingredientTypes == null) {
            ingredientTypes = new ResourceRegistry<>(context, ResourceType.INGREDIENTS);
        }
    }

    IngredientType ingredientType(int id) {
        return ingredientTypes.get(id);
    }

}
