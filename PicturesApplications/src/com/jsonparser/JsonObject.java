package com.jsonparser;

import java.util.ArrayList;
import java.util.List;

/**
 * @author sad
 */
public class JsonObject extends JsonElement {

    private final List<FieldValuePair> elements = new ArrayList<>();

    public boolean has(String key) {
        for (FieldValuePair pair : elements) {
            if (key.equals(pair.getName())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String getAsString() {
        StringBuilder sb = new StringBuilder();
        sb.append('{');
        boolean first = true;
        for (FieldValuePair pair : elements) {
            if (!first) {
                sb.append(", ");
            }
            sb.append(pair.toString());
            first = false;
        }
        return sb.append('}').toString();
    }

    public JsonElement getElementByName(String name) {
        for (FieldValuePair p : elements) {
            if (p.getName().equals(name)) {
                return p.getValue();
            }
        }
        return null;
    }

    public List<FieldValuePair> getElements() {
        return elements;
    }

    @Override
    public int getAsInt() {
        throw new UnsupportedOperationException("Cannot convert JsonObject to int");
    }

    @Override
    public long getAsLong() {
        throw new UnsupportedOperationException("Cannot convert JsonObject to long");
    }

    @Override
    public JsonObject getAsObject() {
        return this;
    }

    @Override
    public JsonArray getAsArray() {
        throw new UnsupportedOperationException("Cannot convert JsonObject to JsonArray");
    }

    @Override
    public boolean isObject() {
        return true;
    }

    @Override
    public boolean getAsBoolean() {
        throw new UnsupportedOperationException("Cannot convert JsonObject to boolean");
    }

    @Override
    public float getAsFloat() {
        throw new UnsupportedOperationException("Cannot convert JsonObject to float");
    }
}
