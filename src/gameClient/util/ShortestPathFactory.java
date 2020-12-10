package gameClient.util;

import java.util.LinkedList;
import java.util.List;
import api.*;
import gameClient.GameAgent;
import gameClient.GameData;
import gameClient.GamePokemon;
import implementation.util.Pair;

public class ShortestPathFactory {
    GameData data;
    LinkedList<GamePokemon> stack;
    boolean isConsumed;

    public ShortestPathFactory(GameData data){
        this.data = data;
        stack = new LinkedList<>();
        stack.addAll(data.getPokemons());
        isConsumed = false;
    }

    public Pair<List<node_data>,GamePokemon> buildPath(GameAgent agent){
        synchronized (this){ // Lock function for thread
            while (isConsumed){ // If function is consumed by
                try {
                    wait();
                }catch (InterruptedException e){
                    e.printStackTrace();
                }
            }
            isConsumed = true;
            GamePokemon pokemon;
            while ((pokemon = data.shortestDistancePokemon(agent,stack)) != null && pokemon.isTargeted()){
                stack.remove(pokemon);
            }
            if(pokemon == null){
                stack.addAll(data.getPokemons());
                pokemon = data.shortestDistancePokemon(agent,stack);
            }
            List<node_data> path = data.getGraph_algorithms().shortestPath(agent.getSrc(),pokemon.getEdge().getSrc());
            pokemon.setTargeted(true);
            Pair<List<node_data>,GamePokemon> pair = new Pair<>();
            pair.setFirst(path);
            pair.setSecond(pokemon);
            isConsumed = false;
            notifyAll();
            return pair;
        }
    }

}
