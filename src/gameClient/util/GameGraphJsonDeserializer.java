package gameClient.util;
import api.*;

import com.google.gson.*;
import gameClient.GamePokemon;
import implementation.DWGraph_DS;
import implementation.GeoLocation;
import implementation.NodeData;

import java.lang.reflect.Type;
import java.util.List;

public class GameGraphJsonDeserializer implements JsonDeserializer<directed_weighted_graph> {
    @Override
    public directed_weighted_graph deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        directed_weighted_graph graph = new DWGraph_DS();
        JsonArray nodesArray = jsonObject.get("Nodes").getAsJsonArray();
        for(JsonElement nodeObject : nodesArray){
            node_data node = new NodeData();
            JsonObject object = nodeObject.getAsJsonObject();
            String[] cordiantes = object.get("pos").getAsString().split(",");
            node.setLocation(new GeoLocation(Double.parseDouble(cordiantes[0]),Double.parseDouble(cordiantes[1]),Double.parseDouble(cordiantes[2])));
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
