package np.com.aanalbasaula.foodplanner.model;

import np.com.aanalbasaula.foodplanner.R;

public class Ingredient {

    private long id;
    private String name;
    private Type type;
    private Quantity quantity;


    public enum Type{
        SPICE(0), VEGETABLE(1), FRUIT(2), Berry(3);

        private int stringResource;

        private Type(int resourceId){
            this.stringResource = resourceId;
        }
    }
}
