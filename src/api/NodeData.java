package api;
import java.util.Objects;

/**
 * This interface represents the set of operations applicable on a
 * node (vertex) in a (directional) weighted graph.
 *
 */

public class NodeData implements node_data{

    private int key; // The key of this node.
    private geo_location location; // The geo location of this node.
    private double weight; // The weight of this node.
    private String info; // The info (temporal data) of this node.
    private int tag; // The tag (temporal data) of this node.

    /**
     * Default constructor
     */
    public NodeData(){
        this.key = -1;
        this.location = null;
        this.weight = -1;
        this.info = "";
        this.tag = 0;
    }

    /**
     * Copy constructor
     * @param node the node to copy.
     */
    public NodeData(node_data node){
        this.key = node.getKey();
        this.location = new GeoLocation(node.getLocation());
        this.weight = node.getWeight();
        this.info = node.getInfo();
        this.tag = node.getTag();
    }

    /**
     * setKey - set key for this NodeData with permission.
     * @param permission - this will ensure that only DWGraph_DS class will be able to modify the key of this object.
     * @param key - the key to be modified.
     */
    public void setKey(DWGraph_DS.GraphPermission permission, int key){
        this.key = key;
    }

    /**
     * Returns the key (id) associated with this node.
     * @return the key of this node.
     */
    @Override
    public int getKey() {
        return this.key;
    }

    /** Returns the location of this node, if
     * none return null.
     * @return the location of this node or null if none.
     */
    @Override
    public geo_location getLocation() {
        return this.location;
    }

    /** Allows changing this node's location.
     * @param p - new new location  (position) of this node.
     */
    @Override
    public void setLocation(geo_location p) {
        this.location = p;
    }

    /**
     * Returns the weight associated with this node.
     * @return the weight associated with this node.
     */
    @Override
    public double getWeight() {
        return this.weight;
    }

    /**
     * Allows changing this node's weight.
     * @param w - the new weight
     */
    @Override
    public void setWeight(double w) {
        this.weight = w;
    }

    /**
     * Returns the remark (meta data) associated with this node.
     * @return the remark.
     */
    @Override
    public String getInfo() {
        return this.info;
    }

    /**
     * Allows changing the remark (meta data) associated with this node.
     * @param s the new info String.
     */
    @Override
    public void setInfo(String s) {
        this.info = s;
    }

    /**
     * Temporal data (aka color: e,g, white, gray, black)
     * which can be used be algorithms
     * @return the temporal tag if this node.
     */
    @Override
    public int getTag() {
        return this.tag;
    }

    /**
     * Allows setting the "tag" value for temporal marking an node - common
     * practice for marking by algorithms.
     * @param t - the new value of the tag
     */
    @Override
    public void setTag(int t) {
        this.tag = t;
    }

    /**
     * Override of the toString method.
     * @return a string representation of this node.
     */
    @Override
    public String toString() {
        return ""+key;
    }

    /**
     * Override of the equals method.
     * @param o - the other NodeData object to compare with.
     * @return true/flase depending if these two objects are equal.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NodeData nodeData = (NodeData) o;
        return key == nodeData.key && Objects.equals(location,nodeData.location);
    }

    /**
     * Override of the hashCode method.
     * @return the hash sum of this object.
     */
    @Override
    public int hashCode() {
        return Objects.hash(key,location);
    }
}
