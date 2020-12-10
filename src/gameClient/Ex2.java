package gameClient;
import Server.Game_Server_Ex2;
import api.*;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import gameClient.util.ShortestPathFactory;

public class Ex2 implements Runnable{

    private static game_service game;
    GameData data;
    GameFrame gui;
    final Integer MAX_LOOPS = 2;

    @Override
    public void run() {
        game = Game_Server_Ex2.getServer(23); // Connect to server.
        data = new GameData(game); // Create new game data (holds information on pokemons, agents and graph structure).
        placeAgents(getNumOfAgents()); // Place agents on graph.
        data.update(); // Update game data after placing agents.
        gui = new GameFrame(data); // Update game GUI after updating game data.
        game.startGame(); // Start the game.
        int loopCount = 0; // Initialize loop count (If this count is larger then MAX_LOOPS, then it means agent stuck in a loop).
        ShortestPathFactory factory = new ShortestPathFactory(data);
        try {
            Thread agentThread = new Thread(new AgentRunnable(data, gui, (data.getAgents().get(0)), factory));
            Thread agentThread2 = new Thread(new AgentRunnable(data, gui, (data.getAgents().get(1)), factory));
            Thread agentThread3 = new Thread(new AgentRunnable(data, gui, (data.getAgents().get(2)), factory));
            agentThread.start();
            agentThread2.start();
            agentThread3.start();
            agentThread.join();
            agentThread2.join();
            agentThread3.join();
            while (game.isRunning());
        }catch (InterruptedException e){
            e.printStackTrace();
        }
        System.out.println("End game");
        System.out.println(game);
    }


    public static void main(String[] args) throws InterruptedException {
        Thread gameThread = new Thread(new Ex2());
        gameThread.start();
        gameThread.join();
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

    private int getAgentScore(GameAgent agent){
        Gson gson = new Gson();
        JsonObject jsonObject = gson.fromJson(game.toString(),JsonObject.class);
        return 0;
    }

    private void placeAgents(int numOfAgents){
        for(int i = 0; i < numOfAgents; i++){
            game.addAgent((int) (Math.random()* data.getGraph().nodeSize()));
        }
    }
}
