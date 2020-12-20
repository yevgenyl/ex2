package gameClient;
import api.GeoLocation;

/**
 * This class represents an agent (pokemon agent) character.
 */

public class GameAgent {
    private int id, src, dest; // The id (key) of the agent. the source and the destination nodes.
    private double value, speed; // The value and the speed of the agent.
    private GeoLocation location; // The geo location (in the real world) of the agent.

    /**
     * Initialization constructor.
     * @param id - the id of the agent.
     * @param value - the value of the agent (his score).
     * @param src - agent's source node.
     * @param dest - agent's next destination node.
     * @param speed - agent's speed as received from the server.
     * @param location - the geo location (coordinates) of the agent.
     */
    public GameAgent(int id, double value, int src, int dest, double speed, GeoLocation location){
        this.id = id;
        this.value = value;
        this.src = src;
        this.dest = dest;
        this.speed = speed;
        this.location = location;
    }

    /**
     * Returns the geo location (coordinates) of the agent.
     * @return agent's coordinates (long and lat).
     */
    public GeoLocation getLocation() {
        return location;
    }

    /**
     * Returns the value of the agent (his score).
     * @return - the current value of the agent.
     */
    public double getValue() {
        return value;
    }

    /**
     * Returns the current speed of the agent.
     * @return the agent's speed.
     */
    public double getSpeed() {
        return speed;
    }

    /**
     * Sets the location of the agent.
     * @param location
     */
    public void setLocation(GeoLocation location) {
        this.location = location;
    }

    /**
     * Returns the destination node key of the agent.
     * @return destination node.
     */
    public int getDest() {
        return dest;
    }

    /**
     * Returns the source node key of the agent.
     * @return source node.
     */
    public int getSrc() {
        return src;
    }

    /**
     * Returns the id of the agent.
     * @return agent's id.
     */
    public int getId() {
        return id;
    }
}
