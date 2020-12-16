package gameClient.util;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;

import api.*;
import gameClient.GameAgent;
import gameClient.GameData;
import gameClient.GamePokemon;
import implementation.util.Pair;

public class PathFactory {
    GameData data;
    GamePokemon lastPokemon;
    boolean isConsumed;
    ArrayList<GamePokemon> targeted;

    public PathFactory(GameData data){
        this.data = data;
        isConsumed = false;
        lastPokemon = null;
        targeted = new ArrayList<>();
    }

    public Pair<List<node_data>,GamePokemon> buildPath(GameAgent agent, long sleep){
        synchronized (this){ // Lock function for thread
            while (isConsumed){ // If function is consumed by
                try {
                    wait(100);
                }catch (InterruptedException e){
                    e.printStackTrace();
                }
            }
            isConsumed = true;
            GamePokemon pokemon;
            while(true){
                pokemon = data.shortestDistancePokemon(agent,data.getPokemons());
                if(targeted.contains(pokemon)){
                    data.getManager().notifyObserver();
                }else {
                    break;
                }
            }
            targeted.add(pokemon);
            pokemon.setTargeted(true);
            /*
            if(pokemon.equals(lastPokemon)){
                pokemon = data.mostValuedPokemon();
                lastPokemon = pokemon;
            }

             */
            /*
            while ((pokemon = data.shortestDistancePokemon(agent,stack)) != null && pokemon.isTargeted()){
                stack.remove(pokemon);
                if(stack.isEmpty()){
                    stack.addAll(data.getPokemons());
                }
            }
             */
            data.getManager().notifyObserver();
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

    public void removeTrageted(GamePokemon pokemon){
        targeted.remove(pokemon);
    }

}
