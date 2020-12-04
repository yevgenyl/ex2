package implementation;
import api.*;

public class GeoLocation implements geo_location {
    private double x;
    private double y;
    private double z;

    /**
     * Initialization constructor
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
}
