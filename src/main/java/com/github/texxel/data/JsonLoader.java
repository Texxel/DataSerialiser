package com.github.texxel.data;

import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.JsonWriter;
import com.badlogic.gdx.utils.StreamUtils;
import com.github.texxel.data.exceptions.DataSerializationException;
import com.github.texxel.data.exceptions.InvalidDataException;

import java.io.*;

public class JsonLoader implements DataLoader {

    @Override
    public PData read (InputStream input) {
        try {
            JsonReader reader = new JsonReader();
            JsonValue json = reader.parse(input);
            PData pData = new PData();
            readInto(pData, json);
            input.close();
            return pData;
        } catch (Exception e) {
            throw new DataSerializationException("Couldn't load the data", e);
        } finally {
            StreamUtils.closeQuietly(input);
        }
    }

    private void readInto (PData pData, JsonValue json) {
        for (JsonValue value : json) {
            String name = value.name;
            if (value.isString()) {
                pData.set(name, value.asString());
            } else if (value.isLong()) {
                pData.set(name, value.asLong());
            } else if (value.isDouble()) {
                pData.set(name, value.asDouble());
            } else if (value.isBoolean()){
                pData.set(name, value.asBoolean());
            } else if (value.isObject()) {
                readInto(pData.createSection(name), value);
            } else if (value.isNull()) {
                pData.setNull(name);
            } else {
                throw new InvalidDataException("Unsupported type: " + value.type());
            }
        }
    }

    @Override
    public void write (OutputStream output, PData data) {
        OutputStreamWriter writer = null;
        try {
            JsonValue json = new JsonValue(JsonValue.ValueType.object);
            writeFrom(data, json);
            String writing = json.toJson(JsonWriter.OutputType.json);
            writer = new OutputStreamWriter(output);
            writer.write(writing);
        } catch (Exception e) {
            throw new DataSerializationException("Couldn't write the data", e);
        } finally {
            StreamUtils.closeQuietly(writer);
        }
    }

    private void writeFrom(PData data, JsonValue json) {
        JsonValue prev = null;
        for (String key : data.keys()) {
            JsonValue next;
            switch (data.getType(key)) {
                case DOUBLE:
                    next = new JsonValue(data.getDouble(key));
                    break;
                case LONG:
                    next = new JsonValue(data.getLong(key));
                    break;
                case STRING:
                    next = new JsonValue(data.getString(key));
                    break;
                case BOOLEAN:
                    next = new JsonValue(data.getBoolean(key));
                    break;
                case NULL:
                    next = new JsonValue(JsonValue.ValueType.nullValue);
                    break;
                case DATA:
                    next = new JsonValue(JsonValue.ValueType.object);
                    PData childPData = data.getSection(key);
                    writeFrom(childPData, next);
                    break;
                default:
                    throw new InvalidDataException("Unsupported type " + data.getType(key));
            }
            next.name = key;
            if (prev == null)
                json.child = prev = next;
            else {
                prev.next = next;
                next.prev = prev;
                prev = next;
            }
        }
    }
}
