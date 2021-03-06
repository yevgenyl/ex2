package api;
import api.*;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.Random;

class DWGraph_AlgoTest {

    @Test
     void testCopyGraph(){
        directed_weighted_graph g = graphCreator(5,5);
        System.out.println(g);
        dw_graph_algorithms ga = new DWGraph_Algo();
        ga.init(g);
        directed_weighted_graph g2 = ga.copy();
        System.out.println(g);
        System.out.println(g2);
        assertEquals(g,g2);
        double weight = g.getEdge(0,3).getWeight();
        g.removeEdge(0,3);
        assertFalse(g.equals(g2));
        g.connect(0,3,weight);
        assertTrue(g.equals(g2));
        g.removeEdge(0,3);
        g.connect(0,3,3);
        assertFalse(g.equals(g2));
        g.connect(0,3,weight);
        assertFalse(g.equals(g2));
        g.removeEdge(0,3);
        g.connect(0,3,weight);
        assertTrue(g.equals(g2));
    }


    @Test
    void testShortestPathDist(){
        directed_weighted_graph g = new DWGraph_DS();
        for(int i = 0; i <= 7; i++){
            g.addNode(new NodeData());
        }
        g.connect(1,2,20);
        g.connect(1,5,15);
        g.connect(2,3,20);
        g.connect(5,6,15);
        g.connect(3,4,20);
        g.connect(6,4,15);
        g.connect(1,7,2);
        g.connect(7,4,50);
        dw_graph_algorithms ga = new DWGraph_Algo();
        ga.init(g);
        assertTrue(Double.compare(45,ga.shortestPathDist(1,4)) == 0);
        assertTrue(Double.compare(40,ga.shortestPathDist(1,3)) == 0);
        assertEquals(0,ga.shortestPathDist(1,1));
        assertEquals(-1,ga.shortestPathDist(1,200));
        assertEquals(-1,ga.shortestPathDist(0,1));
    }

    @Test
    void testShortestPath(){
        directed_weighted_graph g = new DWGraph_DS();
        for(int i = 0; i <= 7; i++){
            g.addNode(new NodeData());
        }
        g.connect(1,2,20);
        g.connect(1,5,15);
        g.connect(2,3,20);
        g.connect(5,6,15);
        g.connect(3,4,20);
        g.connect(6,4,15);
        g.connect(1,7,2);
        g.connect(7,4,1);
        dw_graph_algorithms ga = new DWGraph_Algo();
        ga.init(g);
        LinkedList<node_data> expectedPath = new LinkedList<>(Arrays.asList(g.getNode(1),g.getNode(7),g.getNode(4)));
        assertNull(ga.shortestPath(1,10));
        assertEquals(expectedPath,ga.shortestPath(1,4));
        assertEquals(1,ga.shortestPath(1,1).size());
        g.addNode(new NodeData());
        assertNull(ga.shortestPath(1,8));
        assertNotNull(ga.shortestPath(1,7));
    }

    @Test
    void testIsconnected(){
        directed_weighted_graph g = new DWGraph_DS();
        g.addNode(new NodeData());
        g.addNode(new NodeData());
        g.addNode(new NodeData());
        g.addNode(new NodeData());
        g.connect(0,1,0.5);
        g.connect(1,0,0.5);
        g.connect(1,2,0.5);
        g.connect(2,3,0.5);
        g.connect(3,2,0.5);
        dw_graph_algorithms ga = new DWGraph_Algo();
        ga.init(g);
        assertFalse(ga.isConnected());
        directed_weighted_graph g2 = new DWGraph_DS();
        dw_graph_algorithms ga2 = new DWGraph_Algo();
        ga2.init(g2);
        assertTrue(ga2.isConnected());
        directed_weighted_graph g3 = new DWGraph_DS();
        dw_graph_algorithms ga3 = new DWGraph_Algo();
        ga3.init(g3);
        assertTrue(ga3.isConnected());
    }

    @Test
    void testSaveLoad(){
        directed_weighted_graph g = graphCreator(5,10);
        dw_graph_algorithms ga = new DWGraph_Algo();
        ga.init(g);
        ga.save("graph.json");

        dw_graph_algorithms ga2 = new DWGraph_Algo();
        ga2.load("graph.json");

        assertEquals(ga.getGraph(),ga2.getGraph());
    }

    /////////////////////////// Private methods ///////////////////////////

    /**
     * Private function for creating random graph with seed.
     * @param v - number of vertices.
     * @param e - number of edges.
     * @return
     */
    private directed_weighted_graph graphCreator(int v, int e){
        directed_weighted_graph g = new DWGraph_DS();
        Random r = new Random(1);
        for(int i = 0; i < v; i++)
            g.addNode(new NodeData());
        for(int i = 0; i < e; i++) {
            int a = r.nextInt(v);
            int b = r.nextInt(v);
            g.connect(a,b,r.nextDouble());
        }
        return g;
    }
}