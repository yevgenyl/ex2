package gameClient;

import implementation.GeoLocation;

public class GameAgent {
    private int id;
    private double value;
    private int src;
    private int dest;
    private double speed;
    private GeoLocation location;
    private GeoLocation velocity;
    private GeoLocation acceleration;

    public GameAgent(int id, double value, int src, int dest, double speed, GeoLocation location){
        this.id = id;
        this.value = value;
        this.src = src;
        this.dest = dest;
        this.speed = speed;
        this.location = location;
    }

    public GeoLocation getLocation() {
        return location;
    }

    public double getValue() {
        return value;
    }

    public double getSpeed() {
        return speed;
    }

    public void setLocation(GeoLocation location) {
        this.location = location;
    }

    public int getDest() {
        return dest;
    }

    public int getSrc() {
        return src;
    }

    public int getId() {
        return id;
    }
}
