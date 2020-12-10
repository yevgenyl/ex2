package gameClient;

import api.game_listener;

import java.util.ArrayList;

public class GameManager {
    private ArrayList<game_listener> listeners;
    public GameManager(){
        this.listeners = new ArrayList<>();
    }
    public void register(game_listener listener){
        listeners.add(listener);
    }

    public void notifyObserver(){
        for(game_listener listener : listeners){
            listener.update();
        }
    }
}
