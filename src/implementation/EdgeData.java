package implementation;
import api.*;

public class EdgeData implements edge_data{

    private int src;
    private int dest;
    private double weight;
    private String info;
    private int tag;

    /**
     * Initialization constructor
     */
    public EdgeData(int src, int dest, double weight){
        this.src = src;
        this.dest = dest;
        this.weight = weight;
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

    @Override
    public int getSrc() {
        return this.src;
    }

    @Override
    public int getDest() {
        return this.dest;
    }

    @Override
    public double getWeight() {
        return this.weight;
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
}
