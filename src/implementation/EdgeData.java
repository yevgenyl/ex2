package implementation;
import api.*;

import java.util.Objects;

public class EdgeData implements edge_data{

    private int src;
    private int dest;
    private double weight;
    private String info;
    private int tag;

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

    @Override
    public String toString() {
        return "{"+src+","+dest+"}";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EdgeData edgeData = (EdgeData) o;
        return src == edgeData.src &&
                dest == edgeData.dest;
    }

    @Override
    public int hashCode() {
        return Objects.hash(src, dest);
    }
}
