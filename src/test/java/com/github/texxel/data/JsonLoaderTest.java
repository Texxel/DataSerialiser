package com.github.texxel.data;

import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.github.texxel.data.DataLoader;
import com.github.texxel.data.JsonLoader;
import com.github.texxel.data.PData;
import org.junit.Test;

import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class JsonLoaderTest {

    @Test
    public void testRead () throws Exception {
        String data =
                "{\n" +
                "   \"long\": 10,\n" +
                "   \"double\": 1.3,\n" +
                "   \"string\": \"hia\",\n" +
                "   \"bool\": false,\n" +
                "   \"null\": null,\n" +
                "   \"something\": {\n" +
                "       \"sub1\": true,\n" +
                "       \"sub2\": {\n" +
                "           \"hello\": \"yup\",\n" +
                "           \"empty\": {}\n" +
                "       }\n" +
                "   }\n" +
                "}";
        DataLoader loader = new JsonLoader();
        PData pData = loader.read(new ByteArrayInputStream(data.getBytes()));

        assertEquals(10, pData.getLong("long"));
        assertEquals(1.3, pData.getDouble("double"), 0.01);
        assertEquals("hia", pData.getString("string"));
        assertEquals(false, pData.getBoolean("bool"));
        assertEquals(PData.Type.NULL, pData.getType("null"));

        PData something = pData.getSection("something");
        assertEquals(true, something.getBoolean("sub1"));

        PData sub2 = something.getSection("sub2");
        assertEquals("yup", sub2.getString("hello"));

        PData empty = sub2.getSection("empty");
        assertArrayEquals(new String[0], empty.keys().toArray());
    }

    @Test
    public void testWriteData() throws IOException {
        PData data = new PData()
                .set("long", 10)
                .set("double", 1.2)
                .set("bool", true)
                .set("string", "bye")
                .setNull("null");
        PData sub1 = data.createSection("sub1")
                .set("one", 1);
        sub1.createSection("sub2");

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        new JsonLoader().write(out, data);
        String output = new String(out.toByteArray());

        JsonValue json = new JsonReader().parse(output);
        assertEquals(10, json.getLong("long"));
        assertEquals(1.2, json.getDouble("double"), 0.01);
        assertEquals(true, json.getBoolean("bool"));
        assertEquals("bye", json.getString("string"));
        assertTrue(json.get("null").isNull());

        JsonValue jsub1 = json.get("sub1");
        assertEquals(1, jsub1.getLong("one"));

        assertFalse(jsub1.get("sub2").iterator().hasNext());

    }

}