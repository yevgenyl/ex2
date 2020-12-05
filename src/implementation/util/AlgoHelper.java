package implementation.util;

import java.util.HashMap;

public class AlgoHelper<T1> {

    private HashMap<T1, Pair<Double,String>> data;

    public AlgoHelper(){
        data = new HashMap<T1, Pair<Double,String>>();
    }

    public void storeTemporalWeight(T1 key, double value){
        if(data.get(key) == null) {
            data.put(key, new Pair<>(value,""));
        }else {
            data.get(key).setFirst(value);
        }
    }

    public void storeTemporalColor(T1 key, String color){
        if(data.get(key) == null){
            data.put(key,new Pair<>(Double.MAX_VALUE,color));
        }else {
            data.get(key).setSecond(color);
        }
    }

    public double getWeight(T1 key){
        Pair<Double,String> weightContainer;
        if((weightContainer = data.get(key)) != null){
            return weightContainer.getFirst();
        }else {
            return Double.MAX_VALUE;
        }
    }

    public String getColor(T1 key){
        Pair<Double,String> colorContainer;
        if((colorContainer = data.get(key)) != null){
            return colorContainer.getSecond();
        }else {
            return "WHITE";
        }
    }
}
