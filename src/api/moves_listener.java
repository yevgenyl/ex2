package api;

import gameClient.GameAgent;
import implementation.GeoLocation;

import java.util.Timer;

public interface moves_listener {
    public void onMove(GameAgent agent, geo_location destination, double speed, int time_milis);
}
