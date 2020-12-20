package gameClient;
import Server.Game_Server_Ex2;
import api.*;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import api.util.Pair;
import java.util.*;

/**
 * This class represents the main game logic and algorithm.
 */

public class Game implements Runnable{

    private static game_service game; // Server instance.
    private GameData data; // All stuff related to the game data (agents, pokemons, graphs...).
    private int numberOfAgents, numOfPokemons, id, level; // Number of pokemons and agents. User id and level choice.
    private HashMap<Integer,Integer> visited; // Maps agents for targets (pokemons). used by the path updating method.
    private List<node_data>[] agentsPath; // Array of lists representing the current path for each agent (array indexes by agents id's).
    private boolean[] needToUpdate; // Tracks agent's need to update their path (when their current path ends).
    private int[] pathIndex, loopCounter;; // Next index in path for each agent. How many loops the agent performed (means agent stuck in a loop).

    //Testing improved algorithm
    private long[][] timesManager; // Time management data for each agent.
    private long last_sleep; // Last actual sleep time of the loop.
    private final int ID = 0, SLEEP = 1, COMPLETE = 2, STATE = 3; // Constants used for timesManager array (for convenience).
    private boolean delayed; // If there was an unexpected delay.
    private List<node_data> lastPath; // Last path of the specified agent (before updating).

    /**
     * Initialization constructor
     * @param id the id of (9 digits) given by the user (student).
     * @param level the chosen game level (from 0 to 23).
     */
    public Game(int id, int level){
        this.id = id;
        this.level = level;
        initGame(level,id);
    }

    @Override
    public void run() {
        while (game.isRunning()) { // Start game loop
                for (int agentID = 0; agentID < numberOfAgents; agentID++) {
                    //Testing improved algorithm
                    if(timesManager[agentID][STATE] != 2) { // We calculate time differences only for agents who don't need to complete their current move.
                        timesManager[agentID][SLEEP] = timesManager[agentID][SLEEP] - last_sleep; // Subtract last sleep time from remaining sleep time for each agent.
                    }else {
                        timesManager[agentID][STATE] = -1; // Set agent agent's state as 'in the middle of finishing catch task' (from pokemon to edge dest).
                        continue; // No need to calculate new tasks for the current agent.
                    }
                    boolean needToContinue; // Set flag for loop continue (If no need to calculate agent's times).

                            if(timesManager[agentID][SLEEP] <= 0){ // Make sure not to throw "timeout value is negative" exception (Thread.Sleep value can't be negative).
                                timesManager[agentID][SLEEP] = 0;
                            }
                            if(timesManager[agentID][SLEEP] <= 0 && timesManager[agentID][COMPLETE] == -1) { // This line means that the agent completed all tasks.
                                needToUpdate[(int) timesManager[agentID][ID]] = true; // In this case we'll generate a new path for the agent.
                                needToContinue = false;
                            }
                            else { // Agent already has a task. skip this agent.
                                needToContinue = true;
                            }

                    if(needToContinue)
                        continue;

                    data.getManager().notifyObserver(); // Update data from server.
                    if (needToUpdate[agentID]) { // Agent finished path, so need to calculate new path.
                        updateAgentPath(agentID); // Update the agent path.
                    }
                }
            manageTimes(); // Manage how long to sleep based on time calculation for each agent.
        }
        System.out.println(game);
    }

    /**
     * Initializes global variables and performs login to the server.
     * @param level - the scenario number.
     * @param id - user's id (9 digits id).
     */
    private void initGame(int level, int id){
        game = Game_Server_Ex2.getServer(level); // Connect to server.
        game.login(id);
        data = new GameData(game); // Create new game data (holds information on pokemons, agents and graph structure).
        visited = new HashMap<>();
        numberOfAgents = getNumOfAgents();
        numOfPokemons = getNumOfPokemons();
        placeAgents(numberOfAgents); // Place agents on graph.
        data.update(); // Update game data after placing agents.
        game.startGame(); // Start the game.
        agentsPath = new List[numberOfAgents]; // Path lists for each agent
        needToUpdate = new boolean[numberOfAgents]; // A flag for each agent (if need to update or not).
        pathIndex = new int[numberOfAgents]; // Current next path for each agent

        for(int i = 0; i < numberOfAgents; i++)
            needToUpdate[i] = true; // Set flag (needToUpdate) true by default.

        //Testing improved algorithm
        timesManager = new long[numberOfAgents][];
        for(int i = 0; i < numberOfAgents; i++) {
            timesManager[i] = new long[4];
            timesManager[i][ID] = i; // Agent id
            timesManager[i][SLEEP] = 0; // Agent sleep time
            timesManager[i][COMPLETE] = -1; // Agent complete time. -1 if none.
            timesManager[i][STATE] = -1; // Agent's state - 0: before catch, 1: on move, 2: need to complete edge after catch.
        }

        last_sleep = 0; // Last actual sleep time of the thread (minimum sleep).
        delayed = false; // If there was an unexpected delay.
        loopCounter = new int[numberOfAgents]; // How many times agent going back and forth.
        lastPath = new LinkedList<>(); // Last path that was generated.
        lastPath.add(null); // Initialize last path as null (for the first time only).
    }

    /**
     * Manages time related decisions (how long to sleep) based on the game strategy.
     */
    private void manageTimes(){
        Pair<Long,Integer> minimumTimeAndIndex = getMinimumSleepTime();
        long minimum = minimumTimeAndIndex.getFirst(); // The minimum sleep time (Compared to all agents future tasks).
        int minimumIndex = minimumTimeAndIndex.getSecond();
            if(timesManager[minimumIndex][STATE] == 1){ // If agent state is "Moving".
                onMove(minimum);
            }else {
                if(timesManager[minimumIndex][STATE] == -1){ // After catching pokemon state
                    onComplete(minimum);
                }else { // Before catching pokemon
                    onCatch(minimum);
                }
            }
            handleAgentIncompleteTasks(minimum,minimumIndex); // Handles the case in which agent is after the catch but still need to complete moving to the edge source.
    }

    /**
     * Applies sleep times strategy on agent who is moving to his target source.
     * @param minimum the minimum sleep time required compared to all agents.
     */
    private void onMove(long minimum){
        try {
            if(delayed){
                delayed = false;
                Thread.sleep(Math.abs(minimum-18));
            }else {
                Thread.sleep(minimum + 1);//+1
            }
            game.move();
            data.getManager().notifyObserver();
        }catch (InterruptedException e){
            e.printStackTrace();
        }
    }

    /**
     * Applies sleep times strategy on agent who needs to complete moving to the edge destination after catching pokemon.
     * @param minimum the minimum sleep time required compared to all agents.
     */
    private void onComplete(long minimum){
        try {
            if(!(numOfPokemons > numberOfAgents)) {
                game.move();
                data.getManager().notifyObserver();
                Thread.sleep(minimum);
                game.move();
                data.getManager().notifyObserver();
            }else { // number of pokemons is less than the number of agents.
                if(numberOfAgents == 1){
                    game.move();
                    data.getManager().notifyObserver();
                    Thread.sleep(minimum);
                    game.move();
                    data.getManager().notifyObserver();
                }else {
                    Thread.sleep(minimum);
                    game.move();
                    data.getManager().notifyObserver();
                }
            }
        }catch (InterruptedException e){
            e.printStackTrace();
        }
    }

    /**
     * Applies sleep times strategy on agent who is trying to catch pokemon.
     * @param minimum the minimum sleep time required compared to all agents.
     */
    private void onCatch(long minimum){
        try {
            Thread.sleep(minimum);
            game.move();
            data.getManager().notifyObserver();
        }catch (InterruptedException e){
            e.printStackTrace();
        }
    }

    /**
     * Handles tasks of agents who got two tasks:
     * 1) need to catch pokemon.
     * 2) need to finish moving to edge destination after catching a pokemon.
     * @param minimum the minimum sleep time required compared to all agents.
     * @param minimumIndex the minimum sleep time index in timesManager array.
     */
    private void handleAgentIncompleteTasks(long minimum, int minimumIndex){
        if(timesManager[minimumIndex][COMPLETE] != -1){ // If agent is in the middle of catch, then need to complete the edge.
            timesManager[minimumIndex][SLEEP] = timesManager[minimumIndex][COMPLETE]; // Switch between last sleep (before catching the pokemon) and the time left to complete moving on the edge.
            timesManager[minimumIndex][COMPLETE] = -1; // No more complete tasks (mark as unused).
            timesManager[minimumIndex][STATE] = 2; // Mark as "need to complete the task" to finish the move on the edge (until destination).
        }
        last_sleep = minimum; // Save last sleep time for calculation at the next iteration.
    }

    /**
     * Sorts the timesManager array to ge the minimum sleep time required by agents.
     * @return Pair - representing minimum time, and the index of this time in the timesManager array.
     */
    private Pair<Long,Integer> getMinimumSleepTime(){
    //Testing improved algorithm
    long minimum = Long.MAX_VALUE;
    int minimumIndex = 0;
    for(int i = 0; i < timesManager.length; i++){
        if(timesManager[i][SLEEP] < minimum) {
            minimum = timesManager[i][SLEEP];
            minimumIndex = i;
        }
    }
    Pair<Long,Integer> pair = new Pair<>();
    pair.setFirst(minimum);
    pair.setSecond(minimumIndex);
    return pair;
}

    /**
     * Updates agent's path to target pokemon.
     * @param agentID the id of the agent that should be updated.
     */
    private void updateAgentPath(int agentID){
        needToUpdate[agentID] = false;
        Pair<List<node_data>,GamePokemon> pathPokemonPair = buildPathPokemonPair(agentID);
        int src = pathPokemonPair.getFirst().get(0).getKey(); // Get first value of the path
        int dest = pathPokemonPair.getSecond().getEdge().getDest(); // Get pokemon with the shortest path and/or most valued.
        GameAgent agent = data.getAgents().get(agentID);
        GamePokemon pokemon = pathPokemonPair.getSecond();
        List<node_data> path = pathPokemonPair.getFirst();

        if (agentsPath[agentID].size() == 1) { // Means that we are located one step before pokemon.
            catchPokemon(src,dest, agent, pokemon,agentID);
        } else { // We are located more than one step behind the pokemon.
            moveAgent(agent,agentID,path);
        }

    }

    /**
     * Builds a new path and target pokemon.
     * @param agentID the id of the specified agent.
     * @return a pair representing the shortest path (first value in pair) to the pokemon (second value in pair).
     */
    private Pair<List<node_data>,GamePokemon> buildPathPokemonPair(int agentID){
        Pair<List<node_data>,GamePokemon> pair = new Pair<>();
        needToUpdate[agentID] = false;
        GameAgent agent = data.getAgents().get(agentID);
        visited.put(agentID,-1);
        GamePokemon pokemon = data.shortestDistancePokemon(agent, data.getPokemons());

        boolean targeted = false; // Helper mark to determine if pokemon is targeted (by some agent).
        int in = 0; // Index in the collection
        for(Integer src : visited.values()){
            if((pokemon.getEdge().getSrc()) == src && (in != agentID)) // If pokemon's source node is present in the 'visited' map.
                targeted = true; // set this pokemon as 'targeted'.
            in++;
        }
        if(targeted){ // If pokemon is targeted.
            int tries = 3; // Try finding another pokemon. we have 3 tries.
            int priority = 1; // The priority for pokemon's value. 1 the most valued, 2 the second most valued. 3 the third most valued.
            data.getManager().notifyObserver(); // Updated game's data.
            while (targeted && tries > 0){ // While still targeted and we have tries left.
                pokemon = data.mostValuedPokemon(priority); // Get the most valued pokemon by priority.
                targeted = false; // Set targeted to false. (we'll try to to set this flag to true).
                in = 0;
                for(Integer src : visited.values()){ // For each value of the visited map.
                    if((pokemon.getEdge().getSrc()) == src && (in != agentID)) // If pokemon's source node is present in the 'visited' map.
                        targeted = true; // set this pokemon as 'targeted'.
                    in++;
                }
                tries--;
                priority++;
            }
            //pokemon = data.mostValuedPokemon();
        }
        visited.put(agentID, pokemon.getEdge().getSrc());
        List<node_data> path = data.getGraph_algorithms().shortestPath(agent.getSrc(), pokemon.getEdge().getSrc());

        /*
         * Checking for loops. If there is a loop (more than 2 back and forth walks), changes the next sleep time.
         */
        if(lastPath.get(lastPath.size()-1) != null && (lastPath.get(lastPath.size()-1).getKey() == path.get(0).getKey() || lastPath.get(0).getKey() == path.get(path.size()-1).getKey())){
            loopCounter[agentID]++;
        }else {
            loopCounter[agentID] = 0;
        }
        if(loopCounter[agentID] >= 2){ // Means agent is on loop. In this case we'll change the sleep time of the next iteration by some constant.
            delayed = true;
        }
        lastPath = path;

        agentsPath[agentID] = path;
        pair.setFirst(agentsPath[agentID]);
        pair.setSecond(pokemon);
        return pair;
    }

    /**
     * Calculates the next sleep time for agent until landing on the destination node.
     * @param agent - the agent which need to be moved.
     * @param agentID - the id of the agent.
     * @param path - the current path of the agent.
     */
    private void moveAgent(GameAgent agent, int agentID, List<node_data> path){
        pathIndex[agentID] = 1;
        needToUpdate[agentID] = false;
        try {
            game.chooseNextEdge(agent.getId(), agentsPath[agentID].get(pathIndex[agentID]).getKey()); // Choose next edge as next node in path.
            double distanceFromTarget = data.getGraph().getEdge(path.get(pathIndex[agentID] - 1).getKey(), agentsPath[agentID].get(pathIndex[agentID]).getKey()).getWeight();
            long sleepTime = timeToComplete(distanceFromTarget, data.getAgents().get(agentID).getSpeed(), 1000); // how much time to sleep before reaching the end of edge.

            //Save agent's time information in thr timesManager array.
            for(int i = 0; i < timesManager.length; i++) { // Find current agent
                if (timesManager[i][ID] == agentID) {
                    timesManager[i][SLEEP] = sleepTime;
                    timesManager[i][STATE] = 1;
                }
            }
            //Thread.sleep(sleepTime); // While we sleeping, server is moving the agent.
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Calculates the time needed for sleep before catching a pokemon. And the time needed after catch to finish moving to the edge destination.
     * @param src the source node of the edge.
     * @param dest the destination node of the edge.
     * @param agent the agent which will perform the action.
     * @param pokemon the pokemon who will be catched.
     * @param agentID the id of the agent which will perform the action.
     */
    private void catchPokemon(int src, int dest, GameAgent agent, GamePokemon pokemon, int agentID){
        double weight = data.getGraph().getEdge(src, dest).getWeight(); // The weight (the cost) for moving on the specified edge.
        pathIndex[agentID] = 0; // Next index for agent will be zero (new path will be calculated after catching the pokemon).
        try {
            game.chooseNextEdge(agent.getId(), pokemon.getEdge().getDest()); // Choose next edge as pokemon destination.
            double distanceFromTarget = distanceFromRatio(data.getGraph().getNode(src), data.getGraph().getNode(dest), pokemon.getLocation(), weight); // distance from pokemon.
            long sleepTime = timeToComplete(distanceFromTarget, data.getAgents().get(agentID).getSpeed(), 1000); // how much time to sleep before catching the pokemon.

            int agentIndexInTimesManager = 0;
            for(int i = 0; i < timesManager.length; i++) { // Find current agent
                if (timesManager[i][ID] == agentID) {
                    agentIndexInTimesManager = i;
                }
            }
            timesManager[agentIndexInTimesManager][SLEEP] = sleepTime;
            timesManager[agentIndexInTimesManager][STATE] = 0; // State 0 means catching pokemon.

            double remainedDistance = distanceFromRatio(data.getGraph().getNode(dest),data.getGraph().getNode(src),pokemon.getLocation(),weight);
            sleepTime = timeToComplete(remainedDistance,data.getAgents().get(agentID).getSpeed(),1000); // how much time left to sleep until landing on edge's destination.

            timesManager[agentIndexInTimesManager][COMPLETE] = sleepTime;

            needToUpdate[agentID]=true; // need to update agent's path after the catch.
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Returns the number of agents that should be placed on the graph.
     * @return the number of agents on the map (graph).
     */
    private int getNumOfAgents(){
        Gson gson = new Gson();
        JsonObject jsonObject = gson.fromJson(game.toString(),JsonObject.class);
        JsonObject object = jsonObject.get("GameServer").getAsJsonObject();
        int numOfAgents = object.get("agents").getAsInt();
        return numOfAgents;
    }

    /**
     * Returns the number of pokemons that are on the graph.
     * @return the number of pokemons on the map (graph).
     */
    private int getNumOfPokemons(){
        Gson gson = new Gson();
        JsonObject jsonObject = gson.fromJson(game.toString(),JsonObject.class);
        JsonObject object = jsonObject.get("GameServer").getAsJsonObject();
        int numOfPokemons = object.get("pokemons").getAsInt();
        return numOfPokemons;
    }

    /**
     * Place agents on the map (graph). At first time the agents will be placed at the source of the edge of the the most valued pokemons.
     * @param numOfAgents - how many agents to place (depends on the server).
     */
    private void placeAgents(int numOfAgents){
        PriorityQueue<GamePokemon> pokemons = data.mostValuedPokemons();
        for(int i = 0; i < numOfAgents; i++){
            GamePokemon pokemon = pokemons.poll();
            game.addAgent(pokemon.getEdge().getSrc());
            visited.put(i,pokemon.getEdge().getSrc());
        }
    }

    /**
     * Calculates the time needed to complete a move on specified distance at specified speed (with time unit.. mostly used in milliseconds).
     * @param distance - the distance from target.
     * @param speed - the speed of the moving agent.
     * @param time_unit - the time unit (mostly used 1000 for milliseconds).
     * @return
     */
    private long timeToComplete(double distance, double speed, long time_unit){
        return (long) ((distance/speed)*time_unit);
    }

    /**
     * Calculates the distance on the edge between the agent to the target (pokemon or end of edge).
     * @param n1 - the source node.
     * @param n2 - the destination node.
     * @param pokemon - the pokemon in between (between source to destination nodes).
     * @param weight - the weight of the edge.
     * @return - the distance between agent to chosen target on the specified edge.
     */
    private double distanceFromRatio(node_data n1, node_data n2, geo_location pokemon, double weight){
        double geo_ratio = ratio(n1,n2,pokemon);
        double distance = weight/geo_ratio;
        return distance;
    }

    /**
     * Calculates the ratio (in the real world) between two distances with given geo locations.
     * Used to calculate the ratio between src-pokemon to src-dest.
     * @param n1 the source node of the edge.
     * @param n2 the destination node of the edge.
     * @param pokemon the pokemon.
     * @return the ratio in the real world.
     */
    private double ratio(node_data n1, node_data n2, geo_location pokemon){
        geo_location src = n1.getLocation(), dest = n2.getLocation();
        double distance1 = src.distance(dest); // The distance between destination node to src node (by geo coordinates).
        double distance2 = src.distance(pokemon); // The distance between destination node to pokemon.
        double ratio = distance1/distance2;
        return ratio; // return ratio.
    }

    /**
     * Returns the data object used by this class.
     * @return - the data object.
     */
    public GameData getData() {
        return data;
    }
}
