package api;
import java.util.Objects;

/**
 * This interface represents the set of operations applicable on a
 * directional edge(src,dest) in a (directional) weighted graph.
 *
 */

public class EdgeData implements edge_data{

    private int src; // The source node.
    private int dest; // The destination node.
    private double weight; // The weight of this edge.
    private String info; // Temporal String data.
    private int tag; // Temporal int data.

    /**
     * Default constructor.
     */
    public EdgeData(){
        this.src = -1;
        this.dest = -1;
        this.weight = -1;
        this.info = "";
        this.tag = -1;
    }

    /**
     * Initialization constructor
     * @param src - the source node key.
     * @param dest - the destination node key.
     * @param weight - the weight of this edge.
     */
    public EdgeData(int src, int dest, double weight){
        this.src = src;
        this.dest = dest;
        this.weight = weight;
        info = "";
        tag = -1;
    }

    /**
     * Copy constructor
     */
    public EdgeData(edge_data edge){
        this.src = edge.getSrc();
        this.dest = edge.getDest();
        this.weight = edge.getWeight();
        this.info = edge.getInfo();
        this.tag = edge.getTag();
    }

    /**
     * The id of the source node of this edge.
     * @return the id.
     */
    @Override
    public int getSrc() {
        return this.src;
    }

    /**
     * The id of the destination node of this edge
     * @return the id.
     */
    @Override
    public int getDest() {
        return this.dest;
    }

    /**
     * Returns the weight of this edge.
     * @return the weight of this edge (positive value).
     */
    @Override
    public double getWeight() {
        return this.weight;
    }

    /**
     * Returns the remark (meta data) associated with this edge.
     * @return the remark.
     */
    @Override
    public String getInfo() {
        return this.info;
    }

    /**
     * Allows changing the remark (meta data) associated with this edge.
     * @param s the remark to set.
     */
    @Override
    public void setInfo(String s) {
        this.info = s;
    }

    /**
     * Temporal data (aka color: e,g, white, gray, black)
     * which can be used be algorithms
     * @return the temporal tag data.
     */
    @Override
    public int getTag() {
        return this.tag;
    }

    /**
     * This method allows setting the "tag" value for temporal marking an edge - common
     * practice for marking by algorithms.
     * @param t - the new value of the tag
     */
    @Override
    public void setTag(int t) {
        this.tag = t;
    }

    /**
     * Override of the toString method.
     * @return a string representation of this edge object.
     */
    @Override
    public String toString() {
        return "{"+src+","+dest+"}";
    }

    /**
     * Override of the equals method.
     * @param o - the other object to compare with.
     * @return true/false depending on if these two object are equals.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EdgeData edgeData = (EdgeData) o;
        return src == edgeData.src &&
                dest == edgeData.dest&&
                weight==edgeData.weight;
    }

    /**
     * Override of the hashCode method.
     * @return the hash sum of this object.
     */
    @Override
    public int hashCode() {
        return Objects.hash(src, dest);
    }
}
