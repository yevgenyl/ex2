package gameClient;
import api.*;
import gameClient.util.PathFactory;
import implementation.GeoLocation;
import implementation.util.Pair;

import java.util.List;

public class AgentRunnable implements Runnable{
    private GameData data;
    private GameFrame gui;
    final Integer MAX_LOOPS = 2;
    Integer id;
    PathFactory factory;
    int loopCount;
    long sleep_time;

    public AgentRunnable(GameData game, GameFrame frame, GameAgent agent){
        this.data = game;
        this.gui = frame;
        this.id = agent.getId();
        //this.factory = factory;
        loopCount = 0;
        sleep_time = 100;
    }

    @Override
    public void run() {
        GameAgent agent = null;
        GamePokemon pokemon = null;
        List<node_data> path = null;
        Pair<List<node_data>, GamePokemon> pair = null;
        boolean needToUpdatePath = true;
        int pathPointer = 0;
        long sleepTime = 0;
        while (data.isRunning()) {
            data.getManager().notifyObserver();
            if (needToUpdatePath) {
                needToUpdatePath = false;
                agent = data.getAgents().get(id);
                pokemon = data.shortestDistancePokemon(agent, data.getPokemons());
                path = data.getGraph_algorithms().shortestPath(agent.getSrc(), pokemon.getEdge().getSrc());
                int src = path.get(0).getKey();
                int dest = pokemon.getEdge().getDest();
                if (path.size() == 1) { // Means we located one step before pokemon.
                    double weight = data.getGraph().getEdge(src, dest).getWeight();
                    pathPointer = 0;
                    try {
                        data.getServer().chooseNextEdge(agent.getId(), pokemon.getEdge().getDest()); // Choose next edge as pokemon destination.
                        data.getServer().move();
                        data.getManager().notifyObserver(); // Update GUI and game board.
                        double distanceFromTarget = distanceFromRatio(data.getGraph().getNode(src), data.getGraph().getNode(dest), pokemon.getLocation(), weight); // distance from pokemon.
                        sleepTime = timeToComplete(distanceFromTarget, data.getAgents().get(id).getSpeed(), 1000); // how much time to sleep before catching the pokemon.
                        Thread.sleep(20); // While we sleeping, server is moving the agent.
                        data.getServer().move();
                        data.getManager().notifyObserver(); // Update GUI and game board.
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } else { // We are located more than one step behind the pokemon.
                    pathPointer = 1;
                    try {
                        data.getServer().chooseNextEdge(agent.getId(), path.get(pathPointer).getKey()); // Choose next edge as next node in path.
                        data.getServer().move();
                        data.getManager().notifyObserver(); // Update GUI and game board.
                        double distanceFromTarget = data.getGraph().getEdge(path.get(pathPointer - 1).getKey(), path.get(pathPointer).getKey()).getWeight();
                        sleepTime = timeToComplete(distanceFromTarget, data.getAgents().get(id).getSpeed(), 1000); // how much time to sleep before reaching the end of edge.
                        Thread.sleep(20); // While we sleeping, server is moving the agent.
                        data.getServer().move(); // Move agent
                        data.getManager().notifyObserver(); // Update GUI and game board.
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            } else {
                pathPointer++;
                if (pathPointer >= (path.size() - 1)) {
                    needToUpdatePath = true; // If it's the last index in the path, then need to update path at next iteration.
                    if (pathPointer > (path.size() - 1)) { // need to update now.
                        continue;
                    }
                }
                try {
                    data.getServer().chooseNextEdge(agent.getId(), path.get(pathPointer).getKey()); // Choose next edge as next node in path.
                    data.getServer().move();
                    data.getManager().notifyObserver(); // Update GUI and game board.
                    double distanceFromTarget = data.getGraph().getEdge(path.get(pathPointer - 1).getKey(), path.get(pathPointer).getKey()).getWeight();
                    sleepTime = timeToComplete(distanceFromTarget, data.getAgents().get(id).getSpeed(), 1000); // how much time to sleep before reaching the end of edge.
                    Thread.sleep(20);
                    data.getServer().move(); // Move agent
                    data.getManager().notifyObserver(); // Update GUI and game board.
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            /*
            data.getManager().notifyObserver();
                if (data.getAgents().get(id).getDest() != -1) {
                    //int src = data.getAgents().get(id).getSrc();
                    //int dest = data.getAgents().get(id).getDest();
                    //double weight = data.getGraph().getEdge(src,dest).getWeight();
                    //sleep_time = (long) ((weight/agent.getSpeed())*100);
                    try {
                        data.getServer().move();
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } else {
                    if(needToUpdatePath) {
                        agent = data.getAgents().get(id);
                        pair = factory.buildPath(agent, sleep_time);
                        pokemon = pair.getSecond();
                        needToUpdatePath = false;
                    }
                    if (agent.getSrc() == pokemon.getEdge().getSrc()) { // Agent located one step behind pokemon
                        data.getServer().chooseNextEdge(agent.getId(), pokemon.getEdge().getDest());
                        needToUpdatePath = true;
                    } else {
                        path = pair.getFirst();
                        pathPointer++;
                        if(pathPointer < path.size()) {
                            data.getServer().chooseNextEdge(agent.getId(), path.get(pathPointer).getKey());
                        }else {
                            pathPointer = 0;
                            needToUpdatePath = true;
                        }
                    }

                }
             */
        }
    }

    private long timeToComplete(double distance, double speed, long time_unit){
        return (long) ((distance/speed)*time_unit);
    }

    private double distanceFromRatio(node_data n1, node_data n2, geo_location pokemon, double weight){
        double geo_ratio = ratio(n1,n2,pokemon);
        double distance = weight/geo_ratio;
        return distance;
    }

    private double ratio(node_data n1, node_data n2, geo_location pokemon){
        geo_location src = n1.getLocation(), dest = n2.getLocation();
        double distance1 = src.distance(dest); // The distance between destination node to src node (by geo coordinates).
        double distance2 = src.distance(pokemon); // The distance between destination node to pokemon.
        double ratio = distance1/distance2;
        return ratio; // return ratio.
    }
}
