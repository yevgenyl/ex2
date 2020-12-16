package gameClient;

import api.edge_data;
import implementation.GeoLocation;

import java.util.Objects;

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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GamePokemon pokemon = (GamePokemon) o;
        return Double.compare(pokemon.value, value) == 0 &&
                type == pokemon.type &&
                Objects.equals(location, pokemon.location) &&
                Objects.equals(edge, pokemon.edge);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value, type, location, edge);
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
