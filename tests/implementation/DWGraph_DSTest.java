package implementation;
import api.*;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class DWGraph_DSTest {
    static long start,end; // Runtime variables.

    @BeforeAll
    static void beforeAll(){
        System.out.println("--- Starting test for WGraph_DS class ---");
        start = new Date().getTime();
    }

    /**
     * Test simple empty graph
     */
    @Test
    void testEmptyGraph(){
        directed_weighted_graph g = new DWGraph_DS();
        assertEquals(0,g.nodeSize());
        assertEquals(0,g.edgeSize());
        assertEquals(0,g.getMC());
    }

    /**
     * Test addNode method by creating one node graph.
     */
    @Test
    void testOneNodeGraph(){
        directed_weighted_graph g = new DWGraph_DS();
        node_data n = new NodeData();
        g.addNode(n);
        assertEquals(1,g.nodeSize());
        assertEquals(0,g.edgeSize());
        assertEquals(1,g.getMC());
        assertEquals(0,n.getKey());
    }

    /**
     * Test connect method.
     */
    @Test
    void testConnectTwoNodes(){
        directed_weighted_graph g = new DWGraph_DS();
        node_data n1 = new NodeData();
        g.addNode(n1);
        node_data n2 = new NodeData();
        g.addNode(n2);
        g.connect(0,1,0.8);
        assertEquals(2,g.nodeSize());
        assertEquals(1,g.edgeSize());
        assertEquals(3,g.getMC());
        assertEquals(0,n1.getKey());
        assertEquals(1,n2.getKey());
    }

    /**
     * Test remove method
     */
    @Test
    void testRemoveNode(){
        directed_weighted_graph g = new DWGraph_DS();
        node_data n1 = new NodeData();
        g.addNode(n1);
        node_data n2 = new NodeData();
        g.addNode(n2);
        g.connect(0,1,0.8);
    }

    /**
     * Runtime test for graph creation.
     * Creates random graph with 100,000 vertices and edge number <= 1,000,000.
     * This test should run no more than 10 seconds.
     */
    @Test
    void testRuntime(){
        long startTime = new Date().getTime();
        int v = 100000, e = v*10;
        directed_weighted_graph g = graphCreator(v,e);
        long endTime = new Date().getTime();
        double dt = (endTime-startTime)/1000.0;
        assertTrue(dt < 10);
    }

    /**
     * Test graph's copy constructor
     */
    @Test
    void testCopyConstructor(){
        directed_weighted_graph g1 = new DWGraph_DS();
        g1.addNode(new NodeData());
        g1.addNode(new NodeData());
        g1.addNode(new NodeData());
        g1.addNode(new NodeData());
        g1.connect(2,3,12);
        g1.connect(2,1,0.9);
        //System.out.println(g1);
        assertEquals(4,g1.nodeSize());
        assertEquals(2,g1.edgeSize());
        directed_weighted_graph g2 = new DWGraph_DS(g1);
        //System.out.println(g2);
        assertEquals(4,g2.nodeSize());
        assertEquals(2,g2.edgeSize());
        g1.removeNode(2);
        //System.out.println(g1);
        assertEquals(3,g1.nodeSize());
        assertEquals(4,g2.nodeSize());
        assertEquals(0,g1.edgeSize());
        assertEquals(2,g2.edgeSize());
        //System.out.println(g2);
    }

    /**
     * Test getEdge() graph method.
     */
    @Test
    void testGetEdge(){
        directed_weighted_graph g1 = new DWGraph_DS();
        g1.addNode(new NodeData());
        g1.addNode(new NodeData());
        g1.addNode(new NodeData());
        g1.addNode(new NodeData());
        g1.connect(2,3,12);
        g1.connect(2,1,0.9);
        edge_data edge = g1.getEdge(2,1);
        assertEquals(1,edge.getDest());
        assertEquals(2,edge.getSrc());
        assertEquals(0,Double.compare(0.9,edge.getWeight()));
        edge_data edge2 = g1.getEdge(0,1);
        assertNull(edge2);
    }

    /**
     * Test adding negative nodes.
     * Test adding nodes which already exist.
     */
    @Test
    void testAddIllegalNode(){
        directed_weighted_graph g = new DWGraph_DS();
        assertDoesNotThrow(new Executable() {
            @Override
            public void execute() throws Throwable {
                g.addNode(null);
            }
        });
    }

    /**
     * Test the graph's connect method for illegal values.
     *
     */
    @Test
    void testConnectIllegalValues(){
        directed_weighted_graph g = new DWGraph_DS();
        assertDoesNotThrow(new Executable() {
            @Override
            public void execute() throws Throwable {
                g.connect(-1,-1,14);
            }
        });
        assertNull(g.getEdge(-1,-1));
    }

    /**
     * Test graph's getV methods.
     */
    @Test
    void testGetV(){
        directed_weighted_graph g = new DWGraph_DS();
        g.addNode(new NodeData());
        g.addNode(new NodeData());
        g.addNode(new NodeData());
        g.addNode(new NodeData());
        g.addNode(new NodeData());
        assertEquals(5,g.getV().size());
    }

    /**
     * Test graph's removeEdge() method.
     */
    @Test
    void testRemoveEdge(){
        directed_weighted_graph g = new DWGraph_DS();
        g.addNode(new NodeData());
        g.addNode(new NodeData());
        g.addNode(new NodeData());
        g.addNode(new NodeData());
        g.addNode(new NodeData());
        g.connect(4,1,22);
        assertEquals(5,g.nodeSize());
        assertEquals(1,g.edgeSize());
        g.removeEdge(4,1);
        g.removeEdge(4,1);
        assertEquals(5,g.nodeSize());
        assertEquals(0,g.edgeSize());
    }

    /**
     * Test graph's nodeSize() method.
     */
    @Test
    void testNodeSize(){
        directed_weighted_graph g = graphCreator(30,0);
        assertEquals(30,g.nodeSize());
        g.connect(0,1,30);
        assertEquals(30,g.nodeSize());
        g.removeNode(1);
        assertEquals(29,g.nodeSize());
    }

    /**
     * Test graph's edgeSize() method.
     */
    @Test
    void testEdgeSize(){
        directed_weighted_graph g1 = graphCreator(8,0);
        g1.connect(1,2,10);
        g1.connect(2,1,10);
        g1.connect(3,1,10);
        g1.connect(4,1,10);
        g1.connect(5,1,10);
        g1.connect(6,0,10);
        g1.connect(6,7,10);
        assertEquals(7,g1.edgeSize());
        g1.removeNode(1);
        assertEquals(2,g1.edgeSize());
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

    @AfterAll
    static void afterAll(){
        System.out.println("--- End of WGraph_DS class test ---");
        end = new Date().getTime();
        double dt = (end-start)/1000.0;
        System.out.println("--- Finished in "+dt+" seconds ---");
    }
}