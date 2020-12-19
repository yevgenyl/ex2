package dataStructures.util;

/**
 * This class represents a simple and generic implementation of Pair data structure.
 * @param <T1> - The first object type.
 * @param <T2> - The second object type.
 */

public class Pair<T1,T2> {
    private T1 first; // The first object to be stored.
    private T2 second; // The second object to be stored.

    /**
     * Default constructor.
     */
    public Pair(){
        this.first = null;
        this.second = null;
    }

    /**
     * Initialization constructor.
     * @param first - the first object to be stored.
     * @param second - the second object to be stored.
     */
    public Pair(T1 first, T2 second){
        this.first = first;
        this.second = second;
    }

    /**
     * Returns the first object which is stored.
     * @return the first object or null if none.
     */
    public T1 getFirst(){
        return this.first;
    }

    /**
     * Returns the second object which is stored.
     * @return the second object or null if none.
     */
    public T2 getSecond() {
        return second;
    }

    /**
     * Sets the first object of this pair.
     * @param first
     */
    public void setFirst(T1 first) {
        this.first = first;
    }

    /**
     * Sets the second object of this pair.
     * @param second
     */
    public void setSecond(T2 second) {
        this.second = second;
    }
}
