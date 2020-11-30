package implementation;
import api.*;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashSet;

public class DWGraph_DS implements directed_weighted_graph{

    /**
     * Class GraphPermission -
     * A special class created to make a unique signature of the DWGraph_DS class.
     * The GraphPermission class will ensure that only this class (DWGraph_DS)
     * will be able to do some sensitive modifications.
     * For example: Set key for specific NodeData.
     * Reference to external sources which leaded to the idea of using this trick:
     * https://stackoverflow.com/questions/182278/is-there-a-way-to-simulate-the-c-friend-concept-in-java
     * This is very similar to C++ 'friend' concept.
     */
    public static final class GraphPermission {private GraphPermission(){}}
    private static final GraphPermission graph_permission = new GraphPermission();

    private int currentKey;

    int edgeSize, nodeSize, MC;

    /**
     * Directed weighted graph G(V,E) representation using HashMap data structure.
     */
    private HashMap<Integer,node_data> V; // The collection of vertices.
    private HashMap<Integer,HashMap<Integer,edge_data>> E; // The collection of edges.

    /**
     * Default constructor
     */
    public DWGraph_DS(){
        this.currentKey = -1;
        this.V = new HashMap<>();
        this.E = new HashMap<>();
    }

    /**
     * Copy constructor
     * @param graph
     */
    public DWGraph_DS(directed_weighted_graph graph){

    }

    /**
     * Returns the node with the specified key.
     * @param key - the node_id
     * @return the node with the specified key, null if none.
     */
    @Override
    public node_data getNode(int key) {
        return V.get(key);
    }

    @Override
    public edge_data getEdge(int src, int dest) {
        HashMap<Integer,edge_data> internalMap;
        edge_data edge;
        if((internalMap = E.get(src)) != null){ // If src has edges at all.
            if((edge = internalMap.get(dest)) != null){ // If there is an edge between src to dest.
                return edge; // Finally return the edge_data between src to dest.
            }
        }
        return null;
    }

    @Override
    public void addNode(node_data n) {
        if(n!=null) {
            NodeData nP = (NodeData) n; // A pointer to node_data (n)
            V.put(++currentKey, n); // Insert node_data (n) to the HashMap.
            E.put(currentKey,new HashMap<>()); // Add node to the edges map.
            nP.setKey(graph_permission, currentKey); // Set key for specified node with permission (Only this graph class).
            ++nodeSize;
            ++MC;
        }
    }

    @Override
    public void connect(int src, int dest, double w) {
        if(w < 0.0 || (src == dest)) // Negative weights are illegal. Also we don't need to update if node1 == node2.
            return;
        if(getEdge(src,dest) == null){
            node_data n1, n2;
            if((n1 = V.get(src)) != null && (n2 = V.get(dest)) != null){
                edge_data edge = new EdgeData(src,dest,w);
                E.get(src).put(dest,edge);
            }
        }
        //TODO - 30 Nov 2020 - need to Check with boaz about setWeight()
        /*
        if(w < 0.0 || (src == dest)) // Negative weights are illegal. Also no need to update if node1 == node2.
            return;
        if (hasEdge(node1,node2)){ // If the edge is already exist, then only need to update weight.
            E.get(node1).put(node2,w);
            MC++;
        }else { // If the edge is not already exist.
            node_info n1,n2;
            if ((n1 = V.get(node1)) != null && (n2 = V.get(node2)) != null) { // If both vertices exist.
                E.get(node1).put(node2, w); // Put node2 as a neighbor of node1.
                E.get(node2).put(node1, w); // Put node1 as a neighbor of node2.
                eSize++; // Count edge size (+1).
                MC++; // Count modification (+1).
            }
        }
         */
    }

    @Override
    public Collection<node_data> getV() {
        return V.values();
    }

    @Override
    public Collection<edge_data> getE(int node_id) {
        if(getNode(node_id) != null) {
            return E.get(node_id).values();
        }else {
            Collection<edge_data> emptyCollection = new LinkedHashSet<>();
            return emptyCollection;
        }
    }

    @Override
    public node_data removeNode(int key) {
        return null;
    }

    @Override
    public edge_data removeEdge(int src, int dest) {
        return null;
    }

    @Override
    public int nodeSize() {
        return 0;
    }

    @Override
    public int edgeSize() {
        return 0;
    }

    @Override
    public int getMC() {
        return 0;
    }
}
