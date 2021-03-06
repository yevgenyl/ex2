package api;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import api.util.AlgoHelper;
import api.util.GraphJsonDeserializer;
import api.util.Pair;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.*;

/**
 * This class represents a Directed (positive) Weighted Graph Theory Algorithms including:
 * 0. clone(); (copy)
 * 1. init(graph);
 * 2. isConnected(); // strongly (all ordered pais connected)
 * 3. double shortestPathDist(int src, int dest);
 * 4. List<node_data> shortestPath(int src, int dest);
 * 5. Save(file); // JSON file
 * 6. Load(file); // JSON file
 */

public class DWGraph_Algo implements dw_graph_algorithms{

    private directed_weighted_graph g; // the graph on which this set of algorithms operates on.
    private AlgoHelper<node_data> algoHelper; // This object is used to manage temporal data (color, weight etc..).

    /**
     * Default constructor
     */
    public DWGraph_Algo(){
        g = new DWGraph_DS();
    }

    /**
     * Init the graph on which this set of algorithms operates on.
     * @param g - the graph to be initialized.
     */
    @Override
    public void init(directed_weighted_graph g) {
        this.g = g;
    }

    /**
     * Return the underlying graph of which this class works.
     * @return - a pointer to the graph on which this set of algorithms operates on.
     */
    @Override
    public directed_weighted_graph getGraph() {
        return g;
    }

    /**
     * Compute a deep copy of this weighted graph.
     * @return - a copy of this graph.
     */
    @Override
    public directed_weighted_graph copy() {
        return new DWGraph_DS(g);
    }

    /**
     * Returns true if and only if (iff) there is a valid path from each node to each
     * other node. NOTE: assume directional graph (all n*(n-1) ordered pairs).
     * @return - true/false depends on if the graph is strongly connected.
     */
    @Override
    public boolean isConnected() {
        if(g.nodeSize()==0)
            return true;
        return sccTarjan().size() == 1;
    }

    /**
     * returns the length of the shortest path between src to dest
     * Note: if no such path --> returns -1
     * @param src - start node
     * @param dest - end (target) node
     * @return - the shortest distance from source to destination.
     */
    @Override
    public double shortestPathDist(int src, int dest) {
        node_data n1 = g.getNode(src), n2 = g.getNode(dest);
        if(n1 != null && n2 != null) { // If both src and dest exist.
            if(n1 == n2) // If it's the same node return zero distance.
                return 0;
            Pair<HashMap<Integer,Integer>,Double> parentMapAnWeight = dijkstra(g.getNode(src), g.getNode(dest)); // Perform dijkstra algorithm.
            if(parentMapAnWeight == null){ // Means there is no such path.
                return -1;
            }else {
                return parentMapAnWeight.getSecond(); // Perform dijkstra.
            }
        }else {
            return -1; // If one or both of the nodes are null, it means there is no path between these nodes.
        }
    }

    /**
     * returns the the shortest path between src to dest - as an ordered List of nodes:
     * src--> n1-->n2-->...dest
     * see: https://en.wikipedia.org/wiki/Shortest_path_problem
     * Note if no such path --> returns null;
     * @param src - start node
     * @param dest - end (target) node
     * @return a list representing the shortest path from src to dest (inclusive).
     */
    @Override
    public List<node_data> shortestPath(int src, int dest) {
        node_data n1 = g.getNode(src), n2 = g.getNode(dest);
        LinkedList<node_data> list = new LinkedList<>(); // Empty list which will contain the shortest path.
        if(n1 != null && n2 != null) { // If both src and dest exist.
            if (n1 == n2) { // If it's the same node then it will be the only node in the returned list.
                list.add(n1); // add src nodes to the list.
                return list;
            }
            HashMap<Integer, Integer> parentMap;
            Pair<HashMap<Integer,Integer>,Double> parentMapAnWeight = dijkstra(g.getNode(src), g.getNode(dest));
            if(parentMapAnWeight == null){ // Means there is no such path.
                return null;
            }else {
                parentMap = dijkstra(g.getNode(src), g.getNode(dest)).getFirst(); // Perform dijkstra.
            }
            node_data parent = n2;
            while (parent.getKey() != src) { // get all parents list from dest to src.
                list.addFirst(parent); // Add this nodes in reverse order to the list.
                parent = g.getNode(parentMap.get(parent.getKey())); // Update the next parent.
            }
            list.addFirst(g.getNode(src)); // Finally add the src node to the beginning of list.
            return list; // Return the list containing the path.
        }else {
            return null;
        }
    }

    /**
     * Saves this weighted (directed) graph to the given
     * file name - in JSON format
     * @param file - the file name (may include a relative path).
     * @return true - iff the file was successfully saved
     */
    @Override
    public boolean save(String file) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create(); // Create new GsonBuilder.
        String json = gson.toJson(g); // Make json representation of the graph.
        try {
            PrintWriter pw = new PrintWriter(new File(file)); // Create new PrintWriter object.
            pw.write(json); // Write json string to json file.
            pw.close(); // Close output stream.
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false; // In case of file not found, return false (means operation failed).
        }
        return true;
    }

    /**
     * This method load a graph to this graph algorithm.
     * if the file was successfully loaded - the underlying graph
     * of this class will be changed (to the loaded one), in case the
     * graph was not loaded the original graph should remain "as is".
     * @param file - file name of JSON file
     * @return true - iff the graph was successfully loaded.
     */
    @Override
    public boolean load(String file) {
        try {
            GsonBuilder builder = new GsonBuilder();
            builder.registerTypeAdapter(DWGraph_DS.class, new GraphJsonDeserializer()); // Register graph deserializer to the json builder.
            Gson gson = builder.create();
            FileReader reader = new FileReader(file);
            DWGraph_DS graph = gson.fromJson(reader,DWGraph_DS.class); // Read json object by using the specified deserialize (GraphJsonDeserializer).
            this.init(graph); // Init the graph on which this set of algorithms operates on, with the graph from json object.
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false; // In case of file not found, return false (means operation failed).
        }
        return false;
    }

    /**
     * Tarjan's - Strongly Connected Components (SCC) algorithm implementation.
     * This algorithm is implemented according to the pseudo code in the next video:
     * https://www.youtube.com/watch?v=wUgWX0nc4NY
     * @return a list of lists representing the strongly connected components.
     */
    private List<List<Integer>> sccTarjan(){
        algoHelper = new AlgoHelper<>(); // Create AlgoHelper object for storing coloring data (Unvisited/visited nodes).
        List<List<Integer>> components = new ArrayList<>();
        int time = 0;
        int[] lowlink = new int[g.nodeSize()]; // Low link values for each node id;
        int[] ids = new int[g.nodeSize()]; // Stores the id's of each node (not the same id as node's key value).
        boolean[] onStack = new boolean[g.nodeSize()]; // Whether or not the node is on the stack.
        Stack<Integer> stack = new Stack<>(); //
        for(node_data n : g.getV()){
            if(algoHelper.getColor(n).equals("WHITE")) // If node is not already visited.
                dfs(n,components,time,lowlink,ids,onStack,stack);
        }
        return components;
    }

    /**
     * DFS algorithm implementation used by Tarjan's algorithm.
     * @param n - s starting node.
     * @param components - the list of strongly connected components which need to be updated.
     * @param time - to give each node an id.
     * @param lowlink - the low link values according to the algorithm (The smallest node ids reachable from current node).
     * @param ids - a unique id list for each node (to track if whether or not a node has been visited and to track the id's of each node).
     * @param onStack - tracks if whether or not nodes are on the stack.
     * @param stack - the stack containing the nodes.
     */
    private void dfs(node_data n, List<List<Integer>> components, int time, int[] lowlink, int[] ids, boolean[] onStack, Stack<Integer> stack ){
        int at = n.getKey(); // Denote the id of the node that we are currently at.
        stack.push(at); // Push current node to the stack.
        onStack[at] = true; // Mark the current node as being on the stack.
        lowlink[at] = ids[at] = time++; // Give an id and a current low link value to the node.
        algoHelper.storeTemporalColor(n,"GRAY"); // Mark current node as 'visited'.
        for(edge_data e : g.getE(at)){ // Visit all the neighbors of the current node.
            int to = e.getDest(); // Represents the id of the node that we are going to.
            if(algoHelper.getColor(g.getNode(to)).equals("WHITE")) // If the node we are going to is unvisited.
                dfs(g.getNode(to),components,time,lowlink,ids,onStack,stack); // Then visit it.
            if(onStack[to]) // If the node that we are just came from is on the stack (after recursive call).
                lowlink[at] = Math.min(lowlink[at],lowlink[to]); // Set the min low link value for the node that we are currently at.
        }
        // After visiting al the neighbors of the current node.
        if(ids[at] == lowlink[at]){ // Check if we are on the beginning of a strongly connected component (by checking if the current id is equal to the current low link value).
            Stack<Integer> component = new Stack<>(); // Build a stack which will hold the component.
            while (true){
                int x = stack.pop(); // Pop of all the nodes inside the strongly connected component.
                component.add(x); // Add this nodes to the strongly connected component list.
                onStack[x] = false; // Mark node as not longer being on the stack.
                lowlink[x] = ids[at]; // Make sure all nodes which are part of the strongly connected component have the same id.
                if(x == at) // Once which reach the start of the strongly connected component, break the loop.
                    break;
            }
            components.add(component); // Finally add the strongly connected component to the list of strongly connected components.
        }
    }

    /**
     * Dijkstra shortest path algorithm implementation using minimum priority queue data structure.
     * This algorithm works on non negative directed weighted graphs.
     * This algorithm implemented according to the pseudo code int the following video.
     * https://www.coursera.org/lecture/advanced-data-structures/core-dijkstras-algorithm-2ctyF
     * @param src - source node.
     * @param dest - destination node.
     * @return A Pair of HashMap of the parent's path and the Double value of the shortest distance between src to dest.
     */
    private Pair<HashMap<Integer,Integer>,Double> dijkstra(node_data src, node_data dest){
        HashMap<Integer,Integer> parentMap = new HashMap<>(); // Parents hierarchy.
        AlgoHelper<node_data> helper = new AlgoHelper<>(); // This class is used to manage temporal data (color, weight etc..).
        PriorityQueue<node_data> pq = new PriorityQueue<>(16, new CompareByWeight(helper)); // Create a minimum priority queue with comparator (by minimum weight).
        for(node_data n : g.getV()){
            helper.storeTemporalWeight(n,Double.MAX_VALUE); // Initialise all weight to infinity.
            helper.storeTemporalColor(n,"WHITE"); // Mark all as not visited.
        }
        helper.storeTemporalWeight(src,0.0); // Set src node weight to 0 (the weight of a node to himself is 0).
        pq.add(src); // Add src node to the priority queue.
        while (!pq.isEmpty()){ // While the is nodes to visit.
            node_data current = pq.poll(); // remove the node with the minimal weight. O(Log(n)) operation.
            if(helper.getColor(current).equals("WHITE")) { // If the node is not visited.
                helper.storeTemporalColor(current, "GRAY"); // Mark the node as visited.
            }else {
                continue;
            }
            if(current.getKey() == dest.getKey()) { // If with found the destination node. no need to continue searching.
                Pair<HashMap<Integer,Integer>,Double> pair = new Pair<>();
                pair.setFirst(parentMap);
                pair.setSecond(helper.getWeight(dest));
                return pair;
                //return parentMap; // At this point we have all the information we need. Complete function by return operation.
            }
            for(edge_data neighbor : g.getE(current.getKey())){ // For all neighbors of current node.
                if(helper.getColor(g.getNode(neighbor.getDest())).equals("WHITE")) { // If neighbor is not already. visited
                    double pathDist = helper.getWeight(current) + g.getEdge(current.getKey(), neighbor.getDest()).getWeight(); // Calculate the distance between current node to it's neighbor.
                    if (Double.compare(pathDist, helper.getWeight(g.getNode(neighbor.getDest()))) == -1) { // If the distance is less than the current neighbor distance, then need to update it.
                        helper.storeTemporalWeight(g.getNode(neighbor.getDest()),pathDist); // Update neighbors distance to the new smallest distance.
                        parentMap.put(neighbor.getDest(),current.getKey()); // Set current node as parent of this neighbor.
                        pq.add(g.getNode(neighbor.getDest())); // Add the neighbor to the priority queue for further checks.
                    }
                }
            }
        }
        return null; // If we get here, it means there is no such path.
    }

    /**
     * This class implements the Comparator interface.
     * It is used mainly by the dijkstra algorithm to compare
     * nodes by weight.
     */
    private class CompareByWeight implements Comparator<node_data> {

        AlgoHelper<node_data> helper;

        public CompareByWeight(AlgoHelper<node_data> helper){
            this.helper = helper;
        }

        /**
         * Override of compare method (part of the Comparator interface).
         * The comparison result depends on the weight which is stored inside AlgoHelper object.
         * @param o1 - first node
         * @param o2 - second node
         * @return - -1,0,1 depending on if o1 is less, equal or greater than o2.
         */
        @Override
        public int compare(node_data o1, node_data o2) {
            return Double.compare(helper.getWeight(o1),helper.getWeight(o2));
        }
    }
}
