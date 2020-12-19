package dataStructures.util;
import com.google.gson.*;
import dataStructures.DWGraph_DS;
import api.*;
import dataStructures.GeoLocation;
import dataStructures.NodeData;
import java.lang.reflect.Type;
import java.util.Map;

/**
 * This class is responsible for parsing Json objects which represents an undirected positive weighted graphs.
 */

public class GraphJsonDeserializer implements JsonDeserializer<DWGraph_DS> {

    /**
     * Class JsonDeserializerPermission -
     * A special class created to make a unique signature of the GraphJsonDeserializer class.
     * The JsonDeserializerPermission class will ensure that only this class (GraphJsonDeserializer)
     * will be able to do some sensitive modifications.
     * For example: Set key for specific NodeData.
     * Reference to external sources which leaded to the idea of using this trick:
     * https://stackoverflow.com/questions/182278/is-there-a-way-to-simulate-the-c-friend-concept-in-java
     * This is very similar to C++ 'friend' concept.
     */
    public static final class JsonDeserializerPermission {private JsonDeserializerPermission(){}}
    private static final JsonDeserializerPermission permission = new JsonDeserializerPermission();

    @Override
    public DWGraph_DS deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonObject jsonObject = jsonElement.getAsJsonObject(); // Get the main jason object (the whole graph information).
        DWGraph_DS graph = new DWGraph_DS(); // Create new empty graph to be used for construction.
        JsonObject graphVertices = jsonObject.get("V").getAsJsonObject(); // Get the collection of vertices from the main json object.
        for(Map.Entry<String,JsonElement> vertices : graphVertices.entrySet()){ // For each vertex.
            int key = Integer.parseInt(vertices.getKey()); // The HashMap's key used to store the current node.
            JsonObject vertex = vertices.getValue().getAsJsonObject(); // For each vertex, ge internal values as json object.
            node_data node = new NodeData(); // Create new node object to be used for construction.
            node.setWeight(vertex.get("weight").getAsDouble());
            node.setTag(vertex.get("tag").getAsInt());
            node.setInfo(vertex.get("info").getAsString());
            if(vertex.has("location")){
                JsonObject jsonLocation = vertex.get("location").getAsJsonObject();
                GeoLocation location = new GeoLocation(jsonLocation.get("x").getAsDouble(),jsonLocation.get("y").getAsDouble(),jsonLocation.get("z").getAsDouble());
                node.setLocation(location);
            }
            graph.addNodeWithKey(permission,node,key);
        }
        JsonObject graphEdges = jsonObject.get("E").getAsJsonObject();
        for(Map.Entry<String,JsonElement> edges : graphEdges.entrySet()){ // For each outer key of the HashMap
            for(Map.Entry<String,JsonElement> edge : edges.getValue().getAsJsonObject().entrySet()){ // For each inner key of the HashMap
                JsonObject edgeObject = edge.getValue().getAsJsonObject();
                int src = edgeObject.get("src").getAsInt();
                int dest = edgeObject.get("dest").getAsInt();
                double weight = edgeObject.get("weight").getAsDouble();
                graph.connect(src,dest,weight);
            }
        }
        return graph;
    }
}
