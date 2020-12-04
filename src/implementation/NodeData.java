package implementation;
import api.*;

import java.util.Objects;

public class NodeData implements node_data{

    private int key;
    private geo_location location;
    private double weight;
    private String info;
    private int tag;

    /**
     * Default constructor
     */
    public NodeData(){
        this.key = -1;
        this.location = null;
        this.weight = -1;
        this.info = null;
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

    @Override
    public int getKey() {
        return this.key;
    }

    @Override
    public geo_location getLocation() {
        return this.location;
    }

    @Override
    public void setLocation(geo_location p) {
        this.location = p;
    }

    @Override
    public double getWeight() {
        return this.weight;
    }

    @Override
    public void setWeight(double w) {
        this.weight = w;
    }

    @Override
    public String getInfo() {
        return this.info;
    }

    @Override
    public void setInfo(String s) {
        this.info = s;
    }

    @Override
    public int getTag() {
        return this.tag;
    }

    @Override
    public void setTag(int t) {
        this.tag = t;
    }

    @Override
    public String toString() {
        return ""+key;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NodeData nodeData = (NodeData) o;
        return key == nodeData.key;
    }

    @Override
    public int hashCode() {
        return Objects.hash(key);
    }
}
