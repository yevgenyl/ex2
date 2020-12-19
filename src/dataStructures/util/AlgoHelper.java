package dataStructures.util;
import java.util.HashMap;

/**
 * This class is to be used by algorithms for storing temporal data.
 * @param <T1> the object type for which to store data.
 */

public class AlgoHelper<T1> {
    /*
     * A map representing the object type which needs to store data (T1)
     * The map us capable of storing two common types of data:
     * 1) Double - for storing temporal weight information.. etc.
     * 2) String - to store color information.. etc.
     */
    private HashMap<T1, Pair<Double,String>> data;

    /**
     * Default constructor.
     */
    public AlgoHelper(){
        data = new HashMap<>();
    }

    /**
     * Stores temporal weight data.
     * @param key - the object which needs to store temporal data.
     * @param value - the temporal weight double value to store.
     */
    public void storeTemporalWeight(T1 key, double value){
        if(data.get(key) == null) {
            data.put(key, new Pair<>(value,""));
        }else {
            data.get(key).setFirst(value);
        }
    }

    /**
     * Stores temporal color data.
     * @param key - the object which needs to store temporal data.
     * @param color - the temporal color string value to store.
     */
    public void storeTemporalColor(T1 key, String color){
        if(data.get(key) == null){
            data.put(key,new Pair<>(Double.MAX_VALUE,color));
        }else {
            data.get(key).setSecond(color);
        }
    }

    /**
     * Returns the temporal weight of the specified object.
     * @param key - the object which needs to store temporal data.
     * @return
     */
    public double getWeight(T1 key){
        Pair<Double,String> weightContainer;
        if((weightContainer = data.get(key)) != null){
            return weightContainer.getFirst();
        }else {
            return Double.MAX_VALUE;
        }
    }

    /**
     * Returns the temporal color of the specified object.
     * @param key - the object which needs to store temporal data.
     * @return
     */
    public String getColor(T1 key){
        Pair<Double,String> colorContainer;
        if((colorContainer = data.get(key)) != null){
            return colorContainer.getSecond();
        }else {
            return "WHITE";
        }
    }
}
