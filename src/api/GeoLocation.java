package api;
import java.util.Objects;

/**
 * This class represents a geo location consists of long,lat,alt (x,y,z).
 */

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
     * @param x the x value of this coordinate.
     * @param y the y value of this coordinate.
     * @param z the z value of this coordinate.
     */
    public GeoLocation(double x, double y, double z){
        this.x = x;
        this.y = y;
        this.z = z;
    }

    /**
     * Copy constructor
     * @param location - the  other location object to copy from.
     */
    public GeoLocation(geo_location location){
        if(location != null) {
            this.x = location.x();
            this.y = location.y();
            this.z = location.z();
        }
    }

    /**
     * Returns the x value of this coordinate.
     * @return the x value of the coordinate.
     */
    @Override
    public double x() {
        return this.x;
    }

    /**
     * Returns the y value of this coordinate.
     * @return the y value of this coordinate.
     */
    @Override
    public double y() {
        return this.y;
    }

    /**
     * Returns the z value of this coordinate.
     * @return the z value of this coordinate.
     */
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

    /**
     * Override of the toString method.
     * @return a string representation of this coordinate.
     */
    @Override
    public String toString() {
        return "{" +
                "x=" + x +
                ", y=" + y +
                ", z=" + z +
                '}';
    }

    /**
     * Override if the equals method.
     * @param o - the other geo location object to compare with.
     * @return true/false depending on if these two objects are equal.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GeoLocation location = (GeoLocation) o;
        return Double.compare(location.x, x) == 0 &&
                Double.compare(location.y, y) == 0 &&
                Double.compare(location.z, z) == 0;
    }

    /**
     * Override of the hashCode method.
     * @return the hash sum of this object.
     */
    @Override
    public int hashCode() {
        return Objects.hash(x, y, z);
    }
}
