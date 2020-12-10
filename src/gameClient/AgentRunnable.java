package gameClient;
import api.*;
import gameClient.util.ShortestPathFactory;
import implementation.util.Pair;

import java.util.List;

public class AgentRunnable implements Runnable, game_listener{
    private GameData data;
    private GameAgent agent;
    private GameFrame gui;
    final Integer MAX_LOOPS = 2;
    Integer id;
    ShortestPathFactory factory;
    int loopCount;

    public AgentRunnable(GameData game, GameFrame frame, GameAgent agent, ShortestPathFactory factory){
        this.data = game;
        this.agent = agent;
        this.gui = frame;
        this.id = agent.getId();
        this.factory = factory;
        loopCount = 0;
    }

    @Override
    public void run() {
        GameAgent agent;
        GamePokemon pokemon;
        List<node_data> path;
        data.getManager().register(this);
        while (data.isRunning()) {
                if (data.getAgents().get(id).getDest() != -1) {
                    System.out.println("OK");
                    data.getServer().move();
                    gui.update(); // Update GUI
                    data.getManager().notifyObserver();
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } else {
                    gui.update(); // Update GUI
                    data.getManager().notifyObserver();
                    agent = data.getAgents().get(id);
                    Pair<List<node_data>, GamePokemon> pair = factory.buildPath(agent);
                    pokemon = pair.getSecond();
                    //pokemon = gameData.getPokemons().get(0);
                    if (agent.getSrc() == pokemon.getEdge().getSrc()) { // Agent located one step behind pokemon
                        data.getServer().chooseNextEdge(agent.getId(), pokemon.getEdge().getDest());
                    } else {
                        path = pair.getFirst();
                        //path = gameData.getGraph_algorithms().shortestPath(agent.getSrc(), pokemon.getEdge().getSrc());
                        data.getServer().chooseNextEdge(agent.getId(), path.get(1).getKey());
                        System.out.println("Hah");
                    }
                }
        }
    }

    @Override
    public void update() {
        System.out.println(Thread.currentThread().getName()+ "Updated");
    }
}
