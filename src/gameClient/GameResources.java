package gameClient;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class GameResources {
    Image pikachu, bulbasaur, eevee, jigglypuff,meowth, squirtle;
    Image ash;
    Image background1, background2, login;
    Image rock, pokaball;
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
         * Login panel
         */
        login = new ImageIcon("./res/login2.jpg").getImage();
    }

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

    public ArrayList<Image> getAgentResources(){
        ArrayList<Image> agents = new ArrayList<>();
        agents.add(ash);
        return agents;
    }

    public ArrayList<Image> getBackgroundsResources(){
        ArrayList<Image> backgrounds = new ArrayList<>();
        //backgrounds.add(background1);
        backgrounds.add(background2);
        return backgrounds;
    }

    public ArrayList<Image> getNodesResources(){
        ArrayList<Image> nodes = new ArrayList<>();
        nodes.add(rock);
        return nodes;
    }

    public Image getPokaball(){
        return pokaball;
    }

    public Image getLoginBackground(){
        return login;
    }
}
