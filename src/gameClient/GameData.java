package gameClient;
import api.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import gameClient.util.AgentJsonDeserializer;
import gameClient.util.GameGraphJsonDeserializer;
import gameClient.util.PokemonJsonDeserializer;
import dataStructures.DWGraph_Algo;
import dataStructures.DWGraph_DS;
import java.util.*;

/**
 * This class is responsible for parsing the game data from the server and passing it to the client (Game class).
 */

public class GameData implements game_listener{
    private game_service game; // Reference to the game server.
    private directed_weighted_graph graph; // The graph on which this game will operate on.
    ArrayList<GamePokemon> pokemons; // A list of pokemons that should be placed on the graph.
    ArrayList<GameAgent> agents; // A list of agents that are currently on the graph.
    dw_graph_algorithms graph_algorithms; // A set of algorithms which will operate on this game.
    ArrayList<edge_data> pokemonsEdges; // The edges on which the pokemons located.
    GameManager manager; // This class is responsible for managing update calls from the client.
    boolean isInUpdate; // This variable is used for thread safety. denotes if data ia already in update process.

    /**
     * Initialization constructor.
     * @param game -  a reference to the game server.
     */
    public GameData(game_service game){
        this.game = game;
        init();
    }

    /**
     * Initializes game data.
     * This is the only time when graph data is updated (nodes, edges..).
     */
    private void init(){
        this.graph = parseGraph();
        this.graph_algorithms = new DWGraph_Algo();
        this.graph_algorithms.init(this.graph);
        this.pokemons = parsePokemons();
        this.pokemonsEdges = getPokemonsEdgesInfo();
        this.manager = new GameManager();
        manager.register(this);
        isInUpdate = false;
    }

    /**
     * This method updates this class properties by parsing Json objects from the server.
     */
    public void update(){
        synchronized (this){ // Check if it's already in use by some other thread.
            while (isInUpdate){ // If function is consumed by
                try {
                    wait(100); // If it's already in update. Wait 100 milliseconds and try again.
                }catch (InterruptedException e){
                    e.printStackTrace();
                }
            }
            isInUpdate = true; // Mark this method: "in use".
            this.agents = parseAgents(); // update agent's data.
            this.pokemons = parsePokemons(); // update pokemons data.
            this.pokemonsEdges = getPokemonsEdgesInfo(); // update pokemons edges list.
            isInUpdate = false;
            notifyAll(); // Notify the other thread's that this method is now free to use.
        }
    }

    /**
     * Performs parsing to the Json of pokemons as provided by the server.
     * @return a list of pokemon objects.
     */
    private ArrayList<GamePokemon> parsePokemons(){
        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(ArrayList.class, new PokemonJsonDeserializer()); // Register pokemon list deserializer.
        Gson gson = builder.create();
        return gson.fromJson(game.getPokemons(),ArrayList.class);
    }

    /**
     * Performs parsing to the Json of agents as provided by the server.
     * @return a list of agent objects.
     */
    private ArrayList<GameAgent> parseAgents(){
        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(ArrayList.class, new AgentJsonDeserializer()); // Register pokemon list deserializer.
        Gson gson = builder.create();
        return gson.fromJson(game.getAgents(),ArrayList.class);
    }

    /**
     * Performs parsing to the Json of graph as provided by the server.
     * @return a directed weighted graph object.
     */
    private directed_weighted_graph parseGraph(){
        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(DWGraph_DS.class, new GameGraphJsonDeserializer()); // Register pokemon list deserializer.
        Gson gson = builder.create();
        return gson.fromJson(game.getGraph(), DWGraph_DS.class);
    }

    /**
     * Returns the current total score depending on the number of catches and catch score (of each pokemon).
     * @return the current total score.
     */
    public int getScore(){
        Gson gson = new Gson();
        JsonObject jsonObject = gson.fromJson(game.toString(),JsonObject.class);
        JsonObject object = jsonObject.get("GameServer").getAsJsonObject();
        int score = object.get("grade").getAsInt();
        return score;
    }

    /**
     * Returns the current number of moves that was performed by agents.
     * @return current total number of moves.
     */
    public int getMoves(){
        Gson gson = new Gson();
        JsonObject jsonObject = gson.fromJson(game.toString(),JsonObject.class);
        JsonObject object = jsonObject.get("GameServer").getAsJsonObject();
        int moves = object.get("moves").getAsInt();
        return moves;
    }

    /**
     * Returns the set of algorithms which applied on the current graph.
     * @return the graph algorithms object.
     */
    public dw_graph_algorithms getGraph_algorithms() {
            return graph_algorithms;
    }

    /**
     * Returns an ArrayList of the current agent objects.
     * @return an ArrayList of objects representing the current agents in the game.
     */
    public ArrayList<GameAgent> getAgents() {
        return agents;
    }

    /**
     * Returns an ArrayList of the current pokemons that present in the game.
     * @return an ArrayList of objects representing the current pokemons in the game.
     */
    public ArrayList<GamePokemon> getPokemons() {
        return pokemons;
    }

    /**
     * Returns a pointer to the current graph.
     * @return the current graph (map) on which this game operates on.
     */
    public directed_weighted_graph getGraph() {
        return graph;
    }

    /**
     * Returns a pointer to the game manager (which responsible for handling data update requests).
     * @return a pointer to the game manager.
     */
    public GameManager getManager() {
        return manager;
    }

    /**
     * Returns the edges list of all pokemons that are currently in the game.
     * @return an ArrayList of objects representing the edges of all pokemons that are currently present in the game.
     */
    public ArrayList<edge_data> getPokemonsEdges() {
        return pokemonsEdges;
    }

    /**
     * returns a pointer to the game server.
     * @return a pointer to the game server object.
     */
    public game_service getServer(){
        return this.game;
    }

    /**
     * Returns the current state of the game.
     * @return true/false depending on if the game is running or not (if the timer ends).
     */
    public boolean isRunning(){
        return game.isRunning();
    }

    /**
     * Returns the pokemon with the shortest distance from the given agent.
     * @param agent the agent on which this calculations performed on.
     * @param list the list of all pokemons.
     * @return the pokemon object represents the shortest distance pokemon from agent.
     */
    public GamePokemon shortestDistancePokemon(GameAgent agent, List<GamePokemon> list){
                GamePokemon toReturn = null;
                double dist = Double.MAX_VALUE;
                for (GamePokemon pokemon : list) {
                    double current = graph_algorithms.shortestPathDist(agent.getSrc(), pokemon.getEdge().getSrc());
                    if (current < dist) {
                        dist = current;
                        toReturn = pokemon;
                    }
                }
                return toReturn;
    }

    /**
     * Returns the pokemon with the largest value.
     * @param priority the desired priority (1st, 2nd, 3rd most valued..).
     * @return the pokemon object with the largest value (compared to all pokemons in the game).
     */
    public GamePokemon mostValuedPokemon(int priority){
        if(priority >= 0){ // If user choose specified priority.
            PriorityQueue<GamePokemon> queue = mostValuedPokemons(); // Get the most valued pokemons list.
            if(queue.size() >= priority){ // If the size of this list is less than the chosen priority.
                for(int i = 0; i < (priority-1); i++){ // How many pokemons to remove until desired priority have reached.
                    queue.poll(); // Remove unwanted pokemons.
                }
                return queue.poll(); // Finally remove the desired priority pokemon.
            }
        }
        GamePokemon toReturn = mostValuedPokemons().poll(); // Set most valued by default.
        double value = Double.MIN_VALUE;  // Set default largest values as Double.MIN_VALUE
        for(GamePokemon pokemon : pokemons){ // For each pokemon
            if(pokemon.getValue() > value) { // If we found larger value.
                value = pokemon.getValue(); //
                toReturn = pokemon;
            }
        }
        return toReturn;
    }

    /**
     * Return priority queue soretd with comparator (most valued will be at the head of the queue).
     * @return a priority queue of pokemons sorted from largest value (at head) to lowest value.
     */
    public PriorityQueue<GamePokemon> mostValuedPokemons(){
        PriorityQueue<GamePokemon> pokemons = new PriorityQueue<>(10,new PokemonComperator());
        for(GamePokemon pokemon : getPokemons()){
            pokemons.add(pokemon);
        }
        return pokemons;
    }

    /**
     * This class represent a comparator class for pokemons objects.
     */
    private class PokemonComperator implements Comparator<GamePokemon> {

        /**
         * Override of the compare method.
         * @param o1 the first pokemon object.
         * @param o2 the second pokemon object.
         * @return int: 1, 0, -1 depending on if the first is greater, equal or less than the second pokemon.
         */
        @Override
        public int compare(GamePokemon o1, GamePokemon o2) {
            int comp = Double.compare(o1.getValue(),o2.getValue());
            if(comp == -1){
                return 1;
            }else if(comp == 1){
                return -1;
            }else {
                return 0;
            }
        }
    }

    /**
     * Returns a list representing the edges with pokemons on it.
     * @return an ArrayList of edges on which pokemons exist.
     */
    private ArrayList<edge_data> getPokemonsEdgesInfo(){
        ArrayList<edge_data> edges = new ArrayList<>();
        for(GamePokemon pokemon : pokemons){
            updateEdge(pokemon,graph);
            edges.add(pokemon.getEdge());
        }
        return  edges;
    }

    /**
     * Updates the edges data for each pokemon.
     * @param fr the pokemon which needs an update to it's edge data.
     * @param g the graph on which this pokemon is located.
     */
    public void updateEdge(GamePokemon fr, directed_weighted_graph g) {
        Iterator<node_data> itr = g.getV().iterator();
        while(itr.hasNext()) {
            node_data v = itr.next();
            Iterator<edge_data> iter = g.getE(v.getKey()).iterator();
            while(iter.hasNext()) {
                edge_data e = iter.next();
                boolean f = isOnEdge(fr.getLocation(), e,fr.getType(), g);
                if(f) {fr.setEdge(e);}
            }
        }
    }

    /**
     * Checks if the pokemon is on the edge between src to dest.
     * @param p - the pokemon's location
     * @param src - the source node location.
     * @param dest - the destination node location.
     * @return true/false depending on if it's on the edge or not.
     */
    private static boolean isOnEdge(geo_location p, geo_location src, geo_location dest ) {
        boolean ans = false;
        double dist = src.distance(dest);
        double d1 = src.distance(p) + p.distance(dest);
        if(dist>d1-(0.001*0.001)) {ans = true;}
        return ans;
    }

    /**
     * Checks if the pokemon is on the edge between src to dest.
     * @param p the pokemon's location.
     * @param s the source node index.
     * @param d the destination node index.
     * @param g the graph on which this pokemon is located.
     * @return true/false depending on if it's on the edge or not.
     */
    private static boolean isOnEdge(geo_location p, int s, int d, directed_weighted_graph g) {
        geo_location src = g.getNode(s).getLocation();
        geo_location dest = g.getNode(d).getLocation();
        return isOnEdge(p,src,dest);
    }

    /**
     * Checks if the pokemon is on the specified edge.
     * @param p the pokemon's location.
     * @param e the edge to check.
     * @param type pokemon type: 1 id it's located from the lower index source or -1 if the opposite.
     * @param g the graph on which this pokemon is located.
     * @return true/false depending on if it's on the edge or not.
     */
    private static boolean isOnEdge(geo_location p, edge_data e, int type, directed_weighted_graph g) {
        int src = g.getNode(e.getSrc()).getKey();
        int dest = g.getNode(e.getDest()).getKey();
        if(type<0 && dest>src) {return false;}
        if(type>0 && src>dest) {return false;}
        return isOnEdge(p,src, dest, g);
    }
}
