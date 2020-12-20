package gameClient;
import api.edge_data;
import api.GeoLocation;
import java.util.Objects;

/**
 * This class represents a pokemon character.
 */
public class GamePokemon {
    private double value; // The value of the pokemon (how much it worth in the game).
    private int type; // The type can be 1 or -1, representing the direction of the pokemon on the edge.
    private GeoLocation location; // The geo location (in the real world) of the pokemon.
    private edge_data edge; // The current edge on which this pokemon located.
    private boolean targeted; // If currently targeted by some agent.

    /**
     * Initialization constructor
     * @param value - the value of the pokemon (how much it worth in the game).
     * @param type the type can be 1 or -1, representing the direction of the pokemon on the edge.
     * @param location the geo location (in the real world) of the pokemon.
     */
    public GamePokemon(double value, int type, GeoLocation location){
        this.value = value;
        this.type = type;
        this.location = location;
        edge = null;
        targeted = false;
    }

    /**
     * Return the value of this pokemon.
     * @return the value of this pokemon.
     */
    public double getValue() {
        return value;
    }

    /**
     * Sets the specified edge to this pokemon.
     * @param edge the edge to be set.
     */
    public void setEdge(edge_data edge) {
        this.edge = edge;
    }

    /**
     * Returns the edge on which this pokemon is currently located.
     * @return the edge on which this pokemon located.
     */
    public edge_data getEdge() {
        return edge;
    }

    /**
     * Sets this pokemon as targeted (by some agent).
     * @param targeted
     */
    public void setTargeted(boolean targeted) {
        this.targeted = targeted;
    }

    /**
     * Returns true if this pokemon is targeted (the agent on it's way to pokemon).
     * @return true/false depending on if this pokemon is trageted or not.
     */
    public boolean isTargeted() {
        return targeted;
    }

    /**
     * Returns the type of this pokemon (the direction on edge)
     * 1 - means the pokemon located on the edge from the smaller source index.
     * -1 - means the pokemon located on the edge from the larger source index.
     * @return the type (direction) of this pokemon on the edge.
     */
    public int getType() {
        return type;
    }

    /**
     * Returns the geo location of the pokemon.
     * @return
     */
    public GeoLocation getLocation() {
        return location; // The geo location of the pokemon.
    }

    /**
     * Override of the equals function for comparing pokemon objects.
     * @param o - the other object.
     * @return true/false depending on if object 1 equals to object 2.
     */
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

    /**
     * Override of the default hashCode of this object.
     * @return the hash sum of this object.
     */
    @Override
    public int hashCode() {
        return Objects.hash(value, type, location, edge);
    }

    /**
     * Ovveride of the default toString function.
     * @return a string representation of this pokemon.
     */
    @Override
    public String toString() {
        return "GamePokemon{" +
                "value=" + value +
                ", type=" + type +
                ", location=" + location +
                '}';
    }
}
