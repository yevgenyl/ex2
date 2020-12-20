package gameClient.util;
import com.google.gson.*;
import gameClient.GamePokemon;
import api.GeoLocation;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * This class is responsible for parsing the Json object representing the pokemons list from the server.
 */

public class PokemonJsonDeserializer implements JsonDeserializer<List<GamePokemon>> {
    @Override
    public List<GamePokemon> deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        List<GamePokemon> list = new ArrayList<>();
        JsonArray pokemons = jsonObject.get("Pokemons").getAsJsonArray();
        for(JsonElement pokemon : pokemons){
            JsonObject object = pokemon.getAsJsonObject().get("Pokemon").getAsJsonObject();
            String[] _cordiantes = object.get("pos").getAsString().split(",");
            double _value = object.get("value").getAsDouble();
            int _type = object.get("type").getAsInt();
            GamePokemon gamePokemon = new GamePokemon(_value,_type,new GeoLocation(Double.parseDouble(_cordiantes[0]),Double.parseDouble(_cordiantes[1]),Double.parseDouble(_cordiantes[2])));
            list.add(gamePokemon);
        }
        return list;
    }
}
