package gameClient.util;

import java.util.ArrayList;
import api.*;
import gameClient.GameAgent;
import implementation.GeoLocation;

public class MovesManager {
    ArrayList<moves_listener> listeners;
    boolean isConsumed;
    public MovesManager(){
        this.listeners = new ArrayList<>();
        isConsumed = false;
    }
    public void register(moves_listener listener){
        listeners.add(listener);
    }

    public void notifyObserver(GameAgent agent, GeoLocation destination, double speed, int time_milis){
        for (moves_listener listener : listeners){
            listener.onMove(agent,destination,speed,time_milis);
        }
    }
}
