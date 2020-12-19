package gameClient.util;
import com.google.gson.*;
import gameClient.GameAgent;
import dataStructures.GeoLocation;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * This class is responsible for parsing the Json object representing the agents list from the server.
 */

public class AgentJsonDeserializer implements JsonDeserializer<List<GameAgent>> {
    @Override
    public List<GameAgent> deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        List<GameAgent> list = new ArrayList<>();
        JsonArray agents = jsonObject.get("Agents").getAsJsonArray();
        for(JsonElement agent : agents){
            JsonObject object = agent.getAsJsonObject().get("Agent").getAsJsonObject();
            int id = object.get("id").getAsInt();
            double value = object.get("value").getAsDouble();
            int src = object.get("src").getAsInt();
            int dest = object.get("dest").getAsInt();
            double speed = object.get("speed").getAsDouble();
            String[] cordiantes = object.get("pos").getAsString().split(",");
            GameAgent gameAgent = new GameAgent(id,value,src,dest,speed,new GeoLocation(Double.parseDouble(cordiantes[0]),Double.parseDouble(cordiantes[1]),Double.parseDouble(cordiantes[2])));
            list.add(gameAgent);
        }
        return list;
    }
}
