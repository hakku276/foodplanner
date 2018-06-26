package np.com.aanalbasaula.foodplanner.resources;

/**
 * Represents a type that can be loaded for use with the application
 */
public class Resource {
    /**
     * The unique identifier of the resource, requires: uniqueness only among the group, example:
     * two resources of same types cannot have the same id, but resources of different types
     * are allowed to have same id
     */
    protected final int id;

    protected Resource(int id) {
        this.id = id;
    }

    /**
     * Obtain the id of the resource
     *
     * @return the identifier of the resource
     */
    public int getId() {
        return id;
    }
}
