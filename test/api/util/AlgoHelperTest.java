package api.util;

import api.NodeData;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AlgoHelperTest {
    @Test
    void testAlgoHelperClass(){
        AlgoHelper<NodeData> helper = new AlgoHelper<>();
        NodeData n = new NodeData();
        helper.storeTemporalColor(n,"WHITE");
        assertEquals("WHITE",helper.getColor(n));
        helper.storeTemporalWeight(n,14.0);
        assertTrue(Double.compare(14,helper.getWeight(n)) == 0);
    }
}