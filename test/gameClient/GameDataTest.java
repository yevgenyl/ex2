package gameClient;

import Server.Game_Server_Ex2;
import api.GeoLocation;
import api.game_service;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GameDataTest {

    @Test
    void testParsingJson(){
        /*
         * Checks:
         * Test agents size should be 0 (agents not placed).
         * Test pokemons size should be 1 (1 pokemon in scenario 0).
         */
        game_service game = Game_Server_Ex2.getServer(0);
        GameData data = new GameData(game);
        assertNull(data.getAgents());
        assertNotNull(data.getPokemons());
        assertEquals(data.getPokemons().size(),1);
    }

    @Test
    void testEqualsPokemon(){
        game_service game = Game_Server_Ex2.getServer(0);
        GameData data = new GameData(game);
        /*
         * Test equals method of pokemon.
         */
        GamePokemon pokemon = new GamePokemon(5.0,-1,new GeoLocation(35.197656770719604,32.10191878639921,0.0));
        pokemon.setEdge(data.getPokemons().get(0).getEdge());
        assertEquals(data.getPokemons().get(0),pokemon);
    }

    @Test
    void testShortestDistancePokemon(){
        game_service game = Game_Server_Ex2.getServer(0);
        GameData data = new GameData(game);
        game.addAgent(9);
        game.startGame();
        data.getManager().notifyObserver();
        /*
         * Test shortestDistancePokemon
         */
        GamePokemon pokemon = new GamePokemon(5.0,-1,new GeoLocation(35.197656770719604,32.10191878639921,0.0));
        pokemon.setEdge(data.getPokemons().get(0).getEdge());
        GameAgent agent = data.getAgents().get(0);
        GamePokemon shortest = data.shortestDistancePokemon(data.getAgents().get(0),data.getPokemons());
        assertEquals(shortest,pokemon);
        game.stopGame();
    }

    @Test
    void testMostValuedPokemon(){
        game_service game = Game_Server_Ex2.getServer(0);
        GameData data = new GameData(game);
        game.addAgent(9);
        game.startGame();
        data.getManager().notifyObserver();
        /*
         * Test mostValuedPokemon.
         */
        GamePokemon pokemon = new GamePokemon(5.0,-1,new GeoLocation(35.197656770719604,32.10191878639921,0.0));
        pokemon.setEdge(data.getPokemons().get(0).getEdge());
        GamePokemon mostValued = data.mostValuedPokemon(1);
        assertEquals(mostValued,pokemon);
        game.stopGame();
    }

}