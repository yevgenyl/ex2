package gameClient.util;
import api.*;
import com.google.gson.*;
import dataStructures.DWGraph_DS;
import dataStructures.GeoLocation;
import dataStructures.NodeData;
import java.lang.reflect.Type;

/**
 * This class is responsible for parsing the Json object representing the graph on which the game will operate on (at the specific scenario).
 */

public class GameGraphJsonDeserializer implements JsonDeserializer<directed_weighted_graph> {
    @Override
    public directed_weighted_graph deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        directed_weighted_graph graph = new DWGraph_DS();
        JsonArray nodesArray = jsonObject.get("Nodes").getAsJsonArray();
        for(JsonElement nodeObject : nodesArray){
            node_data node = new NodeData();
            JsonObject object = nodeObject.getAsJsonObject();
            String[] coordinates = object.get("pos").getAsString().split(",");
            node.setLocation(new GeoLocation(Double.parseDouble(coordinates[0]),Double.parseDouble(coordinates[1]),Double.parseDouble(coordinates[2])));
            graph.addNode(node);
        }
        JsonArray edgesArray = jsonObject.get("Edges").getAsJsonArray();
        for(JsonElement edgeObject : edgesArray){
            JsonObject edgeJsonObject = edgeObject.getAsJsonObject();
            int src = edgeJsonObject.get("src").getAsInt();
            int dest = edgeJsonObject.get("dest").getAsInt();
            double weight = edgeJsonObject.get("w").getAsDouble();
            graph.connect(src,dest,weight);
        }
        return graph;
    }
}
