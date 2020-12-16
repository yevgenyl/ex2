package implementation;
import api.*;

import java.util.Objects;

public class GeoLocation implements geo_location {
    private double x;
    private double y;
    private double z;

    /**
     * Default constructor.
     */
    public GeoLocation(){
        this.x = -1;
        this.y = -1;
        this.z = -1;
    }

    /**
     * Initialization constructor.
     * @param x
     * @param y
     * @param z
     */
    public GeoLocation(double x, double y, double z){
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public GeoLocation(geo_location location){
        if(location != null) {
            this.x = location.x();
            this.y = location.y();
            this.z = location.z();
        }
    }

    @Override
    public double x() {
        return this.x;
    }

    @Override
    public double y() {
        return this.y;
    }

    @Override
    public double z() {
        return this.z;
    }

    /**
     * Distance between two 3D points
     * According to the formula: d = Sqrt(((x2-x1)^2 + (y2-y1)^2 + (z2-z1)^2)).
     * Note: this function may return inaccurate results because of the way java represents double values.
     * @param g destination node.
     * @return the distance between this node to destination node.
     */
    @Override
    public double distance(geo_location g) {
        double x1 = x, y1 = y, z1 = z; // Prepare source variables.
        double x2 = g.x(), y2 = g.y(), z2 = g.z(); // Prepare destination variables.
        double part1 = Math.pow((x2 - x1),2); // Part 1 according distance formula.
        double part2 = Math.pow((y2 - y1),2); // Part 2 according distance formula.
        double part3 = Math.pow((z2 - z1),2); // Part 3 according distance formula.
        double sum = part1 + part2 + part3; // Sum all parts together according distance formula.
        return Math.sqrt(sum); // Return the square root of the sum according to distance formula.
    }

    public static GeoLocation add(geo_location g1, geo_location g2){
        GeoLocation toReturn = new GeoLocation(g1.x()+g2.x(),g1.y()+g2.y(),g1.z()+g2.z());
        return toReturn;
    }

    public static GeoLocation sub(geo_location g1, geo_location g2){
        GeoLocation toReturn = new GeoLocation(g1.x()-g2.x(),g1.y()-g2.y(),g1.z()-g2.z());
        return toReturn;
    }

    public static GeoLocation mult(geo_location g1, double scalar){
        GeoLocation toReturn = new GeoLocation(g1.x()*scalar,g1.y()*scalar,g1.z()*scalar);
        return toReturn;
    }

    public static GeoLocation div(geo_location g1, double scalar){
        GeoLocation toReturn = new GeoLocation(g1.x()/scalar,g1.y()/scalar,g1.z()/scalar);
        return toReturn;
    }

    public static double magnitude(geo_location g1){
        return Math.sqrt(g1.x()*g1.x() + g1.y()*g1.y() + g1.z()*g1.z());
    }

    public static GeoLocation normalize(geo_location g1){
        double m = magnitude(g1);
        if(m !=0 ){
            return div(g1,m);
        }else {
            return new GeoLocation(0,0,0);
        }
    }

    public static GeoLocation direction(GeoLocation source, GeoLocation destination){
        return GeoLocation.sub(destination,source);
    }

    @Override
    public String toString() {
        return "{" +
                "x=" + x +
                ", y=" + y +
                ", z=" + z +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GeoLocation location = (GeoLocation) o;
        return Double.compare(location.x, x) == 0 &&
                Double.compare(location.y, y) == 0 &&
                Double.compare(location.z, z) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y, z);
    }
}
