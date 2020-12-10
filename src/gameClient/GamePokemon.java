package gameClient;

import api.edge_data;
import implementation.GeoLocation;

public class GamePokemon {
    private double value;
    private int type;
    private GeoLocation location;
    private edge_data edge;
    private boolean targeted;

    public GamePokemon(double value, int type, GeoLocation location){
        this.value = value;
        this.type = type;
        this.location = location;
        edge = null;
        targeted = false;
    }

    public double getValue() {
        return value;
    }

    public void setEdge(edge_data edge) {
        this.edge = edge;
    }

    public edge_data getEdge() {
        return edge;
    }

    public void setTargeted(boolean targeted) {
        this.targeted = targeted;
    }

    public boolean isTargeted() {
        return targeted;
    }

    public int getType() {
        return type;
    }

    public GeoLocation getLocation() {
        return location;
    }

    @Override
    public String toString() {
        return "GamePokemon{" +
                "value=" + value +
                ", type=" + type +
                ", location=" + location +
                '}';
    }
}
