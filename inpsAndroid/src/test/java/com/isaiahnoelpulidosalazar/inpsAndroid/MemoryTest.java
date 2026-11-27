package com.isaiahnoelpulidosalazar.inpsAndroid;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

public class MemoryTest {

    @Test
    public void testBasicOperations() {
        Memory<String> mem = new Memory<>();
        assertTrue(mem.isEmpty());
        assertEquals(0, mem.count());
        assertFalse(mem.remove("missing"));

        mem.add("alpha");
        mem.add(null);
        mem.add("beta");
        assertFalse(mem.isEmpty());
        assertEquals(3, mem.count());
        assertTrue(mem.contains("alpha"));
        assertTrue(mem.contains(null));
        assertFalse(mem.contains("gamma"));

        assertEquals("beta", mem.get(2));
        assertFalse(mem.remove("missing"));
        assertEquals(3, mem.count());

        assertTrue(mem.remove("alpha"));
        assertEquals(2, mem.count());
        assertEquals(null, mem.removeAt(0));
        assertEquals(1, mem.count());
        assertEquals("beta", mem.get(0));

        List<String> iterated = new ArrayList<>();
        for (String item : mem) {
            iterated.add(item);
        }
        assertEquals(Arrays.asList("beta"), iterated);

        List<String> view = mem.toList();
        assertThrows(UnsupportedOperationException.class, () -> view.add("x"));

        mem.clear();
        assertTrue(mem.isEmpty());
        assertEquals(0, mem.count());
    }

    @Test
    public void testOutOfBounds() {
        Memory<Integer> mem = new Memory<>();
        assertThrows(IndexOutOfBoundsException.class, () -> mem.get(0));
        assertThrows(IndexOutOfBoundsException.class, () -> mem.removeAt(0));
    }
}
