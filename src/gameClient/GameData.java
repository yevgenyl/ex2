package gameClient;
import api.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import gameClient.util.AgentJsonDeserializer;
import gameClient.util.GameGraphJsonDeserializer;
import gameClient.util.PokemonJsonDeserializer;
import implementation.DWGraph_Algo;
import implementation.DWGraph_DS;

import java.util.*;

public class GameData implements game_listener{
    private game_service game; // Reference to the game server.
    private directed_weighted_graph graph; // The graph on which this game will operate on.
    ArrayList<GamePokemon> pokemons; // A list of pokemons that should be placed on the graph.
    ArrayList<GameAgent> agents; // A list of agents that are currently on the graph.
    dw_graph_algorithms graph_algorithms; // A set of algorithms which will operate on this game.
    ArrayList<edge_data> pokemonsEdges; // All the edges of all pokemons.
    GameManager manager;
    boolean isInUpdate;

    /**
     * Initialization constructor.
     * @param game -  a reference to the game server.
     */
    public GameData(game_service game){
        this.game = game;
        init();
    }

    /**
     * init the game graph first.
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
        synchronized (this){
            while (isInUpdate){ // If function is consumed by
                try {
                    wait(100);
                }catch (InterruptedException e){
                    e.printStackTrace();
                }
            }
            isInUpdate = true;
            this.agents = parseAgents();
            this.pokemons = parsePokemons();
            this.pokemonsEdges = getPokemonsEdgesInfo();
            isInUpdate = false;
            notifyAll();
        }
    }

    private ArrayList<GamePokemon> parsePokemons(){
        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(ArrayList.class, new PokemonJsonDeserializer()); // Register pokemon list deserializer.
        Gson gson = builder.create();
        return gson.fromJson(game.getPokemons(),ArrayList.class);
    }

    private ArrayList<GameAgent> parseAgents(){
        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(ArrayList.class, new AgentJsonDeserializer()); // Register pokemon list deserializer.
        Gson gson = builder.create();
        return gson.fromJson(game.getAgents(),ArrayList.class);
    }

    private directed_weighted_graph parseGraph(){
        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(DWGraph_DS.class, new GameGraphJsonDeserializer()); // Register pokemon list deserializer.
        Gson gson = builder.create();
        return gson.fromJson(game.getGraph(), DWGraph_DS.class);
    }

    public dw_graph_algorithms getGraph_algorithms() {
            return graph_algorithms;
    }

    public ArrayList<GameAgent> getAgents() {
        return agents;
    }

    public ArrayList<GamePokemon> getPokemons() {
        return pokemons;
    }

    public directed_weighted_graph getGraph() {
        return graph;
    }

    public GameManager getManager() {
        return manager;
    }

    public ArrayList<edge_data> getPokemonsEdges() {
        return pokemonsEdges;
    }

    public game_service getServer(){
        return this.game;
    }

    public boolean isRunning(){
        return game.isRunning();
    }

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

    public GamePokemon mostValuedPokemon(){
        GamePokemon toReturn = null;
        double value = Double.MIN_VALUE;
        for(GamePokemon pokemon : pokemons){
            if(pokemon.getValue() > value) {
                value = pokemon.getValue();
                toReturn = pokemon;
            }
        }
        return toReturn;
    }

    public PriorityQueue<GamePokemon> mostValuedPokemons(){
        PriorityQueue<GamePokemon> pokemons = new PriorityQueue<>(10,new PokemonComperator());
        for(GamePokemon pokemon : getPokemons()){
            pokemons.add(pokemon);
        }
        return pokemons;
    }

    private class PokemonComperator implements Comparator<GamePokemon> {

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

    private ArrayList<edge_data> getPokemonsEdgesInfo(){
        ArrayList<edge_data> edges = new ArrayList<>();
        for(GamePokemon pokemon : pokemons){
            updateEdge(pokemon,graph);
            edges.add(pokemon.getEdge());
        }
        return  edges;
    }

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

    private static boolean isOnEdge(geo_location p, geo_location src, geo_location dest ) {
        boolean ans = false;
        double dist = src.distance(dest);
        double d1 = src.distance(p) + p.distance(dest);
        if(dist>d1-(0.001*0.001)) {ans = true;}
        return ans;
    }
    private static boolean isOnEdge(geo_location p, int s, int d, directed_weighted_graph g) {
        geo_location src = g.getNode(s).getLocation();
        geo_location dest = g.getNode(d).getLocation();
        return isOnEdge(p,src,dest);
    }
    private static boolean isOnEdge(geo_location p, edge_data e, int type, directed_weighted_graph g) {
        int src = g.getNode(e.getSrc()).getKey();
        int dest = g.getNode(e.getDest()).getKey();
        if(type<0 && dest>src) {return false;}
        if(type>0 && src>dest) {return false;}
        return isOnEdge(p,src, dest, g);
    }
}
