package gameClient;
import api.game_listener;
import java.util.ArrayList;

/**
 * This class is responsible for handling data update requests.
 */
public class GameManager {
    private ArrayList<game_listener> listeners; // An ArrayList of all registered listeners for update events.
    boolean isConsumed; // A flag used for thread safety.

    /**
     * Default constructor.
     */
    public GameManager(){
        this.listeners = new ArrayList<>();
        isConsumed = false;
    }

    /**
     * Registers new event listener.
     * @param listener - the listener who need's to be registered for event updates.
     */
    public void register(game_listener listener){
        listeners.add(listener);
    }

    /**
     * Notifies all registered listeners for update events.
     */
    public void notifyObserver(){
        synchronized (this) { // Synchronized for thread safety.
            while (isConsumed){
                try {
                    wait();
                }catch (InterruptedException e){
                    e.printStackTrace();
                }
            }
            isConsumed = true;
            for (game_listener listener : listeners) { // Update the list of listeners.
                listener.update();
            }
            isConsumed = false;
            notifyAll();
        }
    }
}
