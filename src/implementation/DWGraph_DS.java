package implementation;
import api.*;
import implementation.util.GraphJsonDeserializer;

import java.util.*;

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

    private int edgeSize, nodeSize, MC;

    /**
     * Directed weighted graph G(V,E) representation using HashMap data structure.
     */
    private HashMap<Integer,node_data> V; // The collection of vertices.
    private HashMap<Integer,HashMap<Integer,edge_data>> E; // The collection of edges.

    /**
     *
     */
    private HashMap<Integer,HashMap<Integer,edge_data>> Reverse_E;

    /**
     * Default constructor
     */
    public DWGraph_DS(){
        this.currentKey = -1;
        this.V = new HashMap<>();
        this.E = new HashMap<>();
        this.Reverse_E = new HashMap<>();
        edgeSize = nodeSize = MC = 0;
    }

    /**
     * Copy constructor
     * @param graph
     */
    public DWGraph_DS(directed_weighted_graph graph){
        if(graph != null) { // null graphs are not accepted.
            currentKey = -1;
            V = new HashMap<>(); // Initialize vertices HashMap.
            E = new HashMap<>(); // Initialize edges HashMap.
            Reverse_E = new HashMap<>();
            nodeSize = edgeSize = MC = 0; // Initialize counters to zero.
            for (node_data v : graph.getV()) { // For each node from old graph.
                int key1 = v.getKey();
                addNode(new NodeData(v)); // Create exactly the same node in new the graph.
                for (edge_data e : graph.getE(key1)) { // For each neighbor e of v copy the key.
                    int key2 = e.getDest();
                    addNode(new NodeData(graph.getNode(key2))); // Create exactly the same node in new the graph.
                    connect(key1, key2, e.getWeight()); // If key1 and key2 were connected in the old graph, then connect them in the new graph.
                }
            }
        }
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
        if(n!=null && (getNode(n.getKey()) == null)) {
            NodeData nP = (NodeData) n; // A pointer to node_data (n)
            V.put(++currentKey, n); // Insert node_data (n) to the HashMap.
            nP.setKey(graph_permission, currentKey); // Set key for specified node with permission (Only this graph class).
            ++nodeSize;
            ++MC;
        }
    }

    /**
     * Add node with specific key.
     * Note: This method can be used with permission only.
     * Only GraphJsonDeserializer owns the permission.
     * @param permission The permission to use this method.
     * @param n The node to add to this graph.
     * @param key The specified key to add.
     */
    public void addNodeWithKey(GraphJsonDeserializer.JsonDeserializerPermission permission, node_data n, int key){
        if(n!=null && (getNode(n.getKey()) == null)) {
            NodeData nP = (NodeData) n; // A pointer to node_data (n)
            V.put(key, n); // Insert node_data (n) to the HashMap.
            nP.setKey(graph_permission, key); // Set key for specified node with permission (Only this graph class).
            ++nodeSize;
            ++MC;
            if(currentKey < key){
                currentKey = key;
            }else {
                currentKey++;
            }
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
                if(E.get(src) == null){
                    E.put(src,new HashMap<>());
                }
                if(Reverse_E.get(dest) == null){
                    Reverse_E.put(dest,new HashMap<>());
                }
                E.get(src).put(dest,edge);
                Reverse_E.get(dest).put(src,edge); // Put the reverse edge dest -> src information.
                edgeSize++;
                MC++;
            }
        }
    }

    @Override
    public Collection<node_data> getV() {
        return V.values();
    }

    @Override
    public Collection<edge_data> getE(int node_id) {
        if(E.get(node_id) != null) {
            return E.get(node_id).values();
        }else {
            Collection<edge_data> emptyCollection = new LinkedHashSet<>();
            return emptyCollection;
        }
    }

    public Collection<edge_data> getEReverse(int node_id){
        if(Reverse_E.get(node_id) != null) {
            return Reverse_E.get(node_id).values();
        }else {
            Collection<edge_data> emptyCollection = new LinkedHashSet<>();
            return emptyCollection;
        }
    }

    @Override
    public node_data removeNode(int key) {
        node_data toRemove = getNode(key);
        if(toRemove != null){ // If there is node to remove.
            Collection<edge_data> reverseEdges = getEReverse(key); // Get the reverse collection of edges.
            int removedEdges = reverseEdges.size() + getE(key).size(); // number of edges to remove = reverse edges + regular edges.
            Iterator<edge_data> it = reverseEdges.iterator(); // Get iterator to the collection of reverse edges.
            while (it.hasNext()){ // For each edge belongs to the specified node (key).
                edge_data edge = it.next();
                it.remove();
                E.get(edge.getSrc()).remove(edge.getDest());
                if(E.get(edge.getDest()) != null) {
                    E.get(edge.getDest()).remove(edge.getSrc());
                }
                if(getE(edge.getSrc()).size() == 0){ // If after removing edges there is no edges left from this source, then need to remove source.
                    E.remove(edge.getSrc());
                }
                Reverse_E.remove(edge.getSrc());
            }
            Reverse_E.remove(key);
            E.remove(key); // Finally remove the specified node from edges HashMap.
            V.remove(key); // Finally remove the specified node from vertices HashMap.
            nodeSize--; // Update the node size.
            edgeSize -= removedEdges; // Update edge size.
            MC++; // Update mode count.
        }
        return toRemove; // Return a pointer to the removed object.
    }

    @Override
    public edge_data removeEdge(int src, int dest) {
            HashMap<Integer,edge_data> internalMap;
            edge_data toRemove = null;
            if((internalMap = E.get(src)) != null) {
                if((toRemove = internalMap.get(dest)) != null) {
                    if (src == dest) // If node1 and node2 are the same. no need to do anything.
                        return null;
                    Reverse_E.get(dest).remove(src);
                    E.get(src).remove(dest); // Remove the edge data between node1 to node2.
                    edgeSize--; // Update edge size.
                    MC++; // Update mode count.
                }
            }
            return toRemove;
    }

    @Override
    public int nodeSize() {
        return this.nodeSize;
    }

    @Override
    public int edgeSize() {
        return this.edgeSize;
    }

    @Override
    public int getMC() {
        return this.MC;
    }

    @Override
    public String toString() {
        String s = "\n";
        for(node_data n : getV()){
            s += "("+n.getKey()+") -> " + getE(n.getKey()).toString() + "\n";
        }
        return s;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DWGraph_DS that = (DWGraph_DS) o;
        return currentKey == that.currentKey &&
                edgeSize == that.edgeSize &&
                nodeSize == that.nodeSize &&
                Objects.equals(V, that.V) &&
                Objects.equals(E, that.E);
    }

    @Override
    public int hashCode() {
        return Objects.hash(currentKey, edgeSize, nodeSize, V, E);
    }
}
