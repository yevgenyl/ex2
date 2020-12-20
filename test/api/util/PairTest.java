package api.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PairTest {
    @Test
    void testPairClass(){
        Pair<Integer,Integer> pair = new Pair<>();
        assertNull(pair.getFirst());
        assertNull(pair.getSecond());
        pair.setFirst(4);
        pair.setSecond(5);
        assertNotNull(pair.getFirst());
        assertEquals(4,pair.getFirst());
        assertEquals(5,pair.getSecond());

        Pair<Integer,Integer> pair2 = new Pair<>();
        pair2.setFirst(null);
        pair2.setSecond(null);

        assertNull(pair2.getFirst());
        assertNull(pair2.getSecond());
    }
}