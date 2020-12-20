package gameClient;
import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

/**
 * This class is responsible for handling the resources,images,icons and backgrounds of the game.
 */

public class GameResources {
    private Image pikachu, bulbasaur, eevee, jigglypuff,meowth, squirtle;
    private Image ash;
    private Image background1, background2, login;
    private Image rock, pokaball;
    public GameResources(){
        /**
         * Pokemons
         */
        pikachu = new ImageIcon("./res/pikachu2.png").getImage();
        bulbasaur = new ImageIcon("./res/bulbasaur.png").getImage();
        eevee = new ImageIcon("./res/eevee.png").getImage();
        jigglypuff = new ImageIcon("./res/jigglypuff.png").getImage();
        meowth = new ImageIcon("./res/meowth.png").getImage();
        squirtle = new ImageIcon("./res/squirtle.png").getImage();
        /**
         * Agents
         */
        ash = new ImageIcon("./res/ash2.png").getImage();
        /**
         * Backgrounds
         */
        background1 = new ImageIcon("./res/background1.png").getImage();
        background2 = new ImageIcon("./res/background2.png").getImage();
        /**
         * Objects
         */
        rock = new ImageIcon("./res/rock.png").getImage();
        pokaball = new ImageIcon("./res/pokaball.png").getImage();
        /**
         * Login panel background
         */
        login = new ImageIcon("./res/login2.jpg").getImage();
    }

    /**
     * Returns pokemons resources images.
     * @return an ArrayList of all image resources.
     */
    public ArrayList<Image> getPokemonResources(){
        ArrayList<Image> pokemons = new ArrayList<>();
        pokemons.add(pikachu);
        pokemons.add(bulbasaur);
        pokemons.add(eevee);
        pokemons.add(jigglypuff);
        pokemons.add(meowth);
        pokemons.add(squirtle);
        return pokemons;
    }

    /**
     * Returns agents resources images.
     * @return an ArrayList of all image resources.
     */
    public ArrayList<Image> getAgentResources(){
        ArrayList<Image> agents = new ArrayList<>();
        agents.add(ash);
        return agents;
    }

    /**
     * Returns backgrounds resources images.
     * @return an ArrayList of all image resources.
     */
    public ArrayList<Image> getBackgroundsResources(){
        ArrayList<Image> backgrounds = new ArrayList<>();
        //backgrounds.add(background1);
        backgrounds.add(background2);
        return backgrounds;
    }

    /**
     * Returns nodes resources images.
     * @return an ArrayList of all image resources.
     */
    public ArrayList<Image> getNodesResources(){
        ArrayList<Image> nodes = new ArrayList<>();
        nodes.add(rock);
        return nodes;
    }

    /**
     * Returns pokaball image.
     * @return an image of pokaball.
     */
    public Image getPokaball(){
        return pokaball;
    }

    /**
     * Returns login image.
     * @return the background for the login panel.
     */
    public Image getLoginBackground(){
        return login;
    }
}
