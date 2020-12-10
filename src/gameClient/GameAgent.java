package gameClient;

import implementation.GeoLocation;

public class GameAgent {
    int id;
    double value;
    int src;
    int dest;
    double speed;
    GeoLocation location;

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
