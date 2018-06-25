package np.com.aanalbasaula.foodplanner.model;

import np.com.aanalbasaula.foodplanner.resources.Resource;

/**
 * Represents an Ingredient Type
 */
public class IngredientType extends Resource{
    private final String name;

    public IngredientType(int id, String name) {
        super(id);
        this.name = name.trim();
    }

    public String getName() {
        return name;
    }
}
