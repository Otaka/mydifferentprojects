package com.jsonparser;

/**
 * @author sad
 */
public class FieldValuePair {

    private final String name;
    private final JsonElement value;

    public FieldValuePair(String name, JsonElement value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public JsonElement getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "'" + name + "':" + value;
    }

}
