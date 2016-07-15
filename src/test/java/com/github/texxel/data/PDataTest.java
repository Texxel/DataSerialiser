package com.github.texxel.data;

import com.github.texxel.data.PData;
import com.github.texxel.data.exceptions.MissingDataException;
import com.github.texxel.data.exceptions.WrongTypeException;
import org.junit.Test;

import java.util.Collection;
import java.util.HashSet;

import static com.github.texxel.AssertHelpers.*;

public class PDataTest {

    @Test
    public void testLong() {
        PData data = new PData();
        data.set("one", 1).set("two", 2);

        assertEquals(1, data.getLong("one"));
        assertEquals(2, data.getLong("two"));
    }

    @Test
    public void testDouble() {
        PData data = new PData();
        data.set( "half", 0.5 ).set( "pi", 3.14 );

        assertEquals( 0.5,  data.getDouble( "half" ), 0.01 );
        assertEquals( 3.14, data.getDouble( "pi" ), 0.01 );
    }

    @Test
    public void testString() {
        PData data = new PData();
        data.set("blue", "blau").set("red", "rot");

        assertEquals("blau", data.getString("blue"));
        assertEquals("rot", data.getString("red"));
    }

    @Test
    public void testBoolean() {
        PData data = new PData();
        data.set("on", true).set("off", false);

        assertEquals(true, data.getBoolean("on"));
        assertEquals(false, data.getBoolean("off"));
    }

    @Test
    public void testKeyList() {
        PData data = new PData();
        data.set("one", "fish")
                .set("two", 6)
                .set("three", 3.4);

        final Collection<String> keys = data.keys();

        assertSetEquals(
                new HashSet<String>() {{
                    add("one");
                    add("two");
                    add("three");
                }},
                new HashSet<String>() {{
                    addAll(keys);
                }});
    }

    @Test
    public void testCreateSections() {
        PData data = new PData();
        PData child = data.createSection("fish");
        child.set("tuna", "big");

        assertFalse(data.contains("tuna"));
        assertEquals("big", child.getString("tuna"));
        assertEquals(child, data.getSection("fish"));
    }

    @Test
    public void testItemsAreDeleted() {
        PData data = new PData();
        data.set("tuna", "big");
        data.set("whale", "massive");

        data.delete("tuna");

        assertFalse(data.contains("tuna"));
        assertEquals("massive", data.getString("whale"));
    }

    @Test
    public void testItemType() {
        PData data = new PData();
        data.set("long", 4);
        data.set("string", "bing");
        data.set("double", 5.3);
        data.set("bool", false);
        data.createSection("data");
        data.setNull("empty");

        assertEquals(PData.Type.DOUBLE, data.getType("double"));
        assertEquals(PData.Type.LONG, data.getType("long"));
        assertEquals(PData.Type.STRING, data.getType("string"));
        assertEquals(PData.Type.BOOLEAN, data.getType("bool"));
        assertEquals(PData.Type.DATA, data.getType("data"));
        assertEquals(PData.Type.NULL, data.getType("empty"));
        assertNull(data.getType("nothing"));
    }

    @Test
    public void testContains() {
        PData data = new PData();
        data.set("One", 1);
        data.set("Two", "nope");
        data.setNull("Three");

        assertTrue(data.contains("One"));
        assertTrue(data.contains("Two"));
        assertTrue(data.contains("Three"));

        assertFalse(data.contains("Four"));
    }

    @Test
    public void testClone() {
        PData orig = new PData();
        orig.set("One", 1);
        orig.set("Two", "twa");
        orig.createSection("Three")
                .set("Four", "poor");

        PData clone = orig.copy();

        assertEquals(orig.toString(), clone.toString());
    }

    @Test
    public void testLongsCanBeDoubles() {
        PData data = new PData();
        data.set("one", 2);

        double val = data.getDouble("one");
        assertEquals(val, 2, 0.001);
    }

    @Test(expected = MissingDataException.class)
    public void testMissingData() {
        PData data = new PData()
            .set("here", true);

        data.getBoolean("there");
    }

    @Test(expected = WrongTypeException.class)
    public void testWrongData() {
        PData data = new PData()
                .set("int", 10);

        data.getString("int");
    }
}