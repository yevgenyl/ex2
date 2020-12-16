package gameClient;

import api.game_listener;
import gameClient.util.MovesManager;

import java.util.ArrayList;

public class GameManager {
    private ArrayList<game_listener> listeners;
    boolean isConsumed;
    private MovesManager movesManager;

    public GameManager(){
        this.listeners = new ArrayList<>();
        isConsumed = false;
        movesManager = new MovesManager();
    }
    public void register(game_listener listener){
        listeners.add(listener);
    }

    public void notifyObserver(){
        synchronized (this) {
            while (isConsumed){
                try {
                    wait();
                }catch (InterruptedException e){
                    e.printStackTrace();
                }
            }
            isConsumed = true;
            for (game_listener listener : listeners) {
                listener.update();
            }
            isConsumed = false;
            notifyAll();
        }
    }

    public MovesManager getMovesManager() {
        return movesManager;
    }
}
