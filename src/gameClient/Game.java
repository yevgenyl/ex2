package gameClient;

import Server.Game_Server_Ex2;
import api.*;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import implementation.util.Pair;

import java.util.*;

public class Game implements Runnable{
    private static game_service game;
    GameData data;
    GameFrame gui;
    int numberOfAgents;
    HashMap<Integer,Integer> visited;
    List<node_data>[] agentsPath;
    boolean[] needToUpdate;
    int[] pathIndex;
    long[] beforeCatchTime;
    long[] afterCatchTime;
    long[] delay;
    long[] times;
    boolean onDelay;
    enum state {CATCH,COMPLETE,RELAX};
    Pair<Long, state>[] states;
    int id, level;

    //Testing improved algorithm
    long[][] timesManager;
    long last_sleep;
    final int ID = 0, SLEEP = 1, COMPLETE = 2, STATE = 3;

    public Game(int id, int level){
        this.id = id;
        this.level = level;
        initGame(level,id);
    }

    @Override
    public void run() {
        while (game.isRunning()) { // Start game loop
            Arrays.sort(delay); // Sort delays array (delay: the state between catching pokemon to end of edge).
            if(delay[0] <= 0) {
                for (int agentID = 0; agentID < numberOfAgents; agentID++) {
                    //Testing improved algorithm
                    if(timesManager[agentID][STATE] != 2) { // only for agents who don't need to complete move
                        timesManager[agentID][SLEEP] = timesManager[agentID][SLEEP] - last_sleep; // Subtract last sleep time from remaining sleep time for each agent.
                    }else {
                        timesManager[agentID][STATE] = -1;
                        continue;
                    }
                    //System.out.println("Sleep: "+timesManager[agentID][SLEEP]);
                    int agentIndexInTimesManager = 0;
                    boolean needToContinue = false; // Set flag for loop continue (If no need to calculate agent's times).
                    for(int i = 0; i < timesManager.length; i++){ // Find current agent
                        if(timesManager[i][ID] == agentID){ // If agent was found
                            agentIndexInTimesManager = i;
                            if(timesManager[i][SLEEP] <= 0){ // Make sure not to throw "timeout value is negative" exception.
                                timesManager[i][SLEEP] = 0;
                            }
                            if(timesManager[i][SLEEP] <= 0 && timesManager[i][COMPLETE] == -1) { // this line means the agent completed sleep tasks, so need to update times.
                                needToUpdate[(int) timesManager[i][ID]] = true;
                                needToContinue = false;
                                break;
                            }
                            else {
                                needToContinue = true;
                                break;
                            }
                        }
                    }
                    if(needToContinue)
                        continue;

                    data.getManager().notifyObserver(); // Update data from server.
                    if (needToUpdate[agentID]) { // Agent finished path, so need to calculate new path.
                        updateAgentPath(agentID); // Update the agent path.
                    } else {
                        pathIndex[agentID]++;
                        if (pathIndex[agentID] >= (agentsPath[agentID].size() - 1)) {
                            needToUpdate[agentID] = true; // If it's the last index in the path, then need to update path at next iteration.
                            if (pathIndex[agentID] > (agentsPath[agentID].size() - 1)) { // need to update now.
                                continue;
                            }
                        }
                        try {
                            game.chooseNextEdge(agentID, agentsPath[agentID].get(pathIndex[agentID]).getKey()); // Choose next edge as next node in path.
                            double distanceFromTarget = data.getGraph().getEdge(agentsPath[agentID].get(pathIndex[agentID] - 1).getKey(), agentsPath[agentID].get(pathIndex[agentID]).getKey()).getWeight();
                            long sleepTime = timeToComplete(distanceFromTarget, data.getAgents().get(agentID).getSpeed(), 1000); // how much time to sleep before reaching the end of edge.
                            times[agentID] = sleepTime;
                            states[agentID].setFirst(sleepTime);
                            states[agentID].setSecond(state.RELAX);
                            timesManager[agentIndexInTimesManager][SLEEP] = sleepTime;
                            timesManager[agentIndexInTimesManager][STATE] = 1;
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }else {
                onDelay = true;
            }
            //Testing improved algorithm
            long minimum = Long.MAX_VALUE;
            int minimumIndex = 0;
            for(int i = 0; i < timesManager.length; i++){
                if(timesManager[i][SLEEP] < minimum) {
                    minimum = timesManager[i][SLEEP];
                    minimumIndex = i;
                }
            }
            try {
                //System.out.println(timesManager[minimumIndex][STATE]);
                if(timesManager[minimumIndex][STATE] == 1){
                    Thread.sleep(minimum+1);
                    game.move();
                    data.getManager().notifyObserver();
                }else {
                    if(timesManager[minimumIndex][STATE] == -1){ // After catching pokemon state
                        game.move();
                        data.getManager().notifyObserver();
                        Thread.sleep(minimum);
                        game.move();
                        data.getManager().notifyObserver();
                    }else {
                        //game.move();
                        //data.getManager().notifyObserver();
                        Thread.sleep(minimum);
                        game.move();
                        data.getManager().notifyObserver();
                    }
                }
            }catch (InterruptedException e){
                e.printStackTrace();
            }
            if(timesManager[minimumIndex][COMPLETE] != -1){ // If agent is in the middle of catch, then need to complete the edge.
                timesManager[minimumIndex][SLEEP] = timesManager[minimumIndex][COMPLETE];
                timesManager[minimumIndex][COMPLETE] = -1;
                timesManager[minimumIndex][STATE] = 2;
            }
            last_sleep = minimum; // Save last sleep time for calculation at the next iteration.
            //manageTimes();
        }
        System.out.println("End game");
        System.out.println(game);
    }

    private void initGame(int level, int id){
        game = Game_Server_Ex2.getServer(level); // Connect to server.
        game.login(id);
        data = new GameData(game); // Create new game data (holds information on pokemons, agents and graph structure).
        visited = new HashMap<>();
        numberOfAgents = getNumOfAgents();
        placeAgents(numberOfAgents); // Place agents on graph.
        data.update(); // Update game data after placing agents.
        game.startGame(); // Start the game.
        int pathPointer = 0;
        long sleepTime = 0;
        agentsPath = new List[numberOfAgents]; // Path lists for each agent
        needToUpdate = new boolean[numberOfAgents]; // A flag for each agent (if need to update or not).
        pathIndex = new int[numberOfAgents]; // Current next path for each agent
        beforeCatchTime = new long[numberOfAgents]; // Sleep time before catching pokemons;
        afterCatchTime = new long[numberOfAgents]; // Sleep time after catching pokemon until reaching end of edge.
        delay = new long[numberOfAgents];
        times = new long[numberOfAgents];
        states = new Pair[numberOfAgents];
        onDelay = false;
        for(int i = 0; i < states.length; i++){
            states[i] = new Pair<>();
        }
        for(int i = 0; i < numberOfAgents; i++)
            needToUpdate[i] = true; // Set flag (needToUpdate) true by default.
        for(int i =0; i < numberOfAgents; i++)
            delay[i] = 0;

        //Testing improved algorithm
        timesManager = new long[numberOfAgents][];
        for(int i = 0; i < numberOfAgents; i++) {
            timesManager[i] = new long[4];
            timesManager[i][ID] = i; // Agent id
            timesManager[i][SLEEP] = 0; // Agent sleep time
            timesManager[i][COMPLETE] = -1; // Agent complete time. -1 if none.
            timesManager[i][STATE] = -1;
        }
        last_sleep = 0; // Last actual sleep time of the thread (minimum sleep).
    }

    private void manageTimes(){
        Pair<Long,Integer> getMinimum = getMinimumSleepTime();
        long minimum = getMinimum.getFirst();
        int index = getMinimum.getSecond();
        if(onDelay){
            onDelay(minimum);
        }else {
            if (states[index].getSecond() == state.CATCH) {
                onCatch(minimum);
            } else {
                onRelax(minimum);
            }
        }
    }

    /**
     *
     */
    private void onDelay(long minimum){
        try {
            onDelay = false;
            game.move();
            data.getManager().notifyObserver();
            Thread.sleep(Math.min(minimum,delay[0]));
            game.move();
            data.getManager().notifyObserver();
            for(int i = 0; i < delay.length; i++)
                delay[i] = 0;
            System.out.println("Delay");
        }catch (InterruptedException e){
            e.printStackTrace();
        }
    }

    /**
     *
     * @return
     */
    private void onCatch(long minimum){
        try {
            game.move();
            data.getManager().notifyObserver();
            Thread.sleep(minimum);
            game.move();
            data.getManager().notifyObserver();
            System.out.println("Catch");
        }catch (InterruptedException e){
            e.printStackTrace();
        }
    }

    /**
     *
     * @return
     */
    private void onRelax(long minimum){
        try {
            game.move();
            data.getManager().notifyObserver();
            Thread.sleep(minimum);
            data.getManager().notifyObserver();
            game.move();
            System.out.println("Other");
        }catch (InterruptedException e){
            e.printStackTrace();
        }
    }

    private Pair<Long,Integer> getMinimumSleepTime(){
        Pair<Long,Integer> minimumSleep = new Pair<>();
        long minimum = Long.MAX_VALUE;
        int index = 0;
        for(int i = 0; i < states.length; i++) {
            if (states[i].getFirst() < minimum) {
                minimum = states[i].getFirst();
                index = i;
            }
            System.out.print(states[i].getFirst()+", ");
        }
        System.out.println(" min={"+minimum+"}");
        minimumSleep.setFirst(minimum);
        minimumSleep.setSecond(index);
        return minimumSleep;
    }

    /**
     * On update path
     */
    private void updateAgentPath(int agentID){
        Pair<List<node_data>,GamePokemon> pathPokemonPair = buildPathPokemonPair(agentID);
        int src = pathPokemonPair.getFirst().get(0).getKey(); // Get first value of the path
        int dest = pathPokemonPair.getSecond().getEdge().getDest(); // Get pokemon with the shortest path and/or most valued.
        GameAgent agent = data.getAgents().get(agentID);
        GamePokemon pokemon = pathPokemonPair.getSecond();
        List<node_data> path = pathPokemonPair.getFirst();

        if (agentsPath[agentID].size() == 1) { // Means we located one step before pokemon.
            catchPokemon(src,dest, agent, pokemon,agentID);
        } else { // We are located more than one step behind the pokemon.
            moveAgent(agent,agentID,path);
        }
    }

    /**
     * Build a new path and target pokemon.
     * @param agentID
     * @return
     */
    private Pair<List<node_data>,GamePokemon> buildPathPokemonPair(int agentID){
        Pair<List<node_data>,GamePokemon> pair = new Pair<>();
        needToUpdate[agentID] = false;
        GameAgent agent = data.getAgents().get(agentID);
        visited.put(agentID,-1);
        GamePokemon pokemon = data.shortestDistancePokemon(agent, data.getPokemons());

        boolean targeted = false;
        for(Integer src : visited.values()){
            if(pokemon.getEdge().getSrc() == src)
                targeted = true;
        }
        if(targeted){
            pokemon = data.mostValuedPokemon();
        }

        visited.put(agentID, pokemon.getEdge().getSrc());
        List<node_data> path = data.getGraph_algorithms().shortestPath(agent.getSrc(), pokemon.getEdge().getSrc());
        agentsPath[agentID] = path;
        pair.setFirst(agentsPath[agentID]);
        pair.setSecond(pokemon);
        return pair;
    }

    private void moveAgent(GameAgent agent, int agentID, List<node_data> path){
        //pathPointer = 1;
        pathIndex[agentID] = 1;
        //needToUpdate[agentID] = false;
        try {
            game.chooseNextEdge(agent.getId(), agentsPath[agentID].get(pathIndex[agentID]).getKey()); // Choose next edge as next node in path.
            //game.move();
            //data.getManager().notifyObserver(); // Update GUI and game board.
            double distanceFromTarget = data.getGraph().getEdge(path.get(pathIndex[agentID] - 1).getKey(), agentsPath[agentID].get(pathIndex[agentID]).getKey()).getWeight();
            long sleepTime = timeToComplete(distanceFromTarget, data.getAgents().get(agentID).getSpeed(), 1000); // how much time to sleep before reaching the end of edge.
            times[agentID] = sleepTime;
            states[agentID].setFirst(sleepTime);

            //Testing improved algorithm
            states[agentID].setSecond(state.RELAX);
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
     * One step behind pokemon logic
     */
    private void catchPokemon(int src, int dest, GameAgent agent, GamePokemon pokemon, int agentID){
        double weight = data.getGraph().getEdge(src, dest).getWeight();
        pathIndex[agentID] = 0;
        try {
            game.chooseNextEdge(agent.getId(), pokemon.getEdge().getDest()); // Choose next edge as pokemon destination.
            //game.move();
            //data.getManager().notifyObserver(); // Update GUI and game board.
            double distanceFromTarget = distanceFromRatio(data.getGraph().getNode(src), data.getGraph().getNode(dest), pokemon.getLocation(), weight); // distance from pokemon.
            long sleepTime = timeToComplete(distanceFromTarget, data.getAgents().get(agentID).getSpeed(), 1000); // how much time to sleep before catching the pokemon.
            //Thread.sleep(sleepTime); // While we sleeping, server is moving the agent.
            beforeCatchTime[agentID] = sleepTime;
            states[agentID].setFirst(sleepTime);
            states[agentID].setSecond(state.CATCH);

            //Testing improved algorithm
            int agentIndexInTimesManager = 0;
            for(int i = 0; i < timesManager.length; i++) { // Find current agent
                if (timesManager[i][ID] == agentID) {
                    agentIndexInTimesManager = i;
                }
            }
            timesManager[agentIndexInTimesManager][SLEEP] = sleepTime;
            timesManager[agentIndexInTimesManager][STATE] = 0;
            System.out.println("Time before catch "+sleepTime);

            //game.move();
            //data.getManager().notifyObserver(); // Update GUI and game board.
            double remainedDistance = distanceFromRatio(data.getGraph().getNode(dest),data.getGraph().getNode(src),pokemon.getLocation(),weight);
            sleepTime = timeToComplete(remainedDistance,data.getAgents().get(agentID).getSpeed(),1000);
            afterCatchTime[agentID] = sleepTime;
            //Thread.sleep(sleepTime);
            //game.move();
            //data.getManager().notifyObserver(); // Update GUI and game board.

            //Testing improved algorithm (Maybe need to get back).
            //delay[agentID] = afterCatchTime[agentID]-beforeCatchTime[agentID];

            //Testing improved algorithm
            timesManager[agentIndexInTimesManager][COMPLETE] = sleepTime;
            System.out.println("Time after catch "+sleepTime);
            //Testing improved algorithm
            needToUpdate[agentID]=true;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Returns the number of agents that should be placed on the graph.
     * @return agents number.
     */
    private int getNumOfAgents(){
        Gson gson = new Gson();
        JsonObject jsonObject = gson.fromJson(game.toString(),JsonObject.class);
        JsonObject object = jsonObject.get("GameServer").getAsJsonObject();
        int numOfAgents = object.get("agents").getAsInt();
        return numOfAgents;
    }

    private void placeAgents(int numOfAgents){
        PriorityQueue<GamePokemon> pokemons = data.mostValuedPokemons();
        for(int i = 0; i < numOfAgents; i++){
            GamePokemon pokemon = pokemons.poll();
            game.addAgent(pokemon.getEdge().getSrc());
            visited.put(i,pokemon.getEdge().getSrc());
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

    public GameData getData() {
        return data;
    }
}
