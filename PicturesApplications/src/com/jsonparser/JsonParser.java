package com.jsonparser;

import java.io.*;

/**
 * @author sad
 */
public class JsonParser {

    private JsonParserStream stream;

    private String readFile(InputStreamReader reader) throws IOException {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader br = new BufferedReader(reader)) {
            boolean first = true;
            for (String line; (line = br.readLine()) != null;) {
                if (!first) {
                    sb.append("\n");
                }

                sb.append(line);
                first = false;
            }
        }
        return sb.toString();
    }

    public JsonElement parse(InputStreamReader reader) {
        try {
            return parse(readFile(reader));
        } catch (IOException ex) {
            throw new RuntimeException("Cannot read input stream");
        }
    }

    public JsonElement parse(String str) {
        stream = new JsonParserStream(str.toCharArray());
        JsonObject object = parseJsonObject();
        return object;
    }

    public JsonObject parseJsonObject() {
        stream.skipBlank();
        char c = stream.getChar();
        if (c != '{') {
            throw new RuntimeException("JsonObject should start with '{' symbol [" + stream.determinePosition(stream.getCurrentPosition() - 1) + "]");
        }

        JsonObject obj = new JsonObject();
        while (true) {
            stream.skipBlank();
            c = stream.getCharNotMove();
            if (c == '}') {
                stream.getChar();
                break;
            } else if (c == '"' || c == '\'') {
                FieldValuePair element = readEntry();
                obj.getElements().add(element);
                stream.skipBlank();
                char comma = stream.getCharNotMove();
                if (comma == ',') {
                    stream.getChar();
                }
            } else {
                throw new RuntimeException("Object should contain only 'name':'value' pairs [" + stream.determinePosition(stream.getCurrentPosition()) + "]");
            }
        }

        return obj;
    }

    public JsonArray parseJsonArray() {
        stream.skipBlank();
        char c = stream.getChar();
        if (c != '[') {
            throw new RuntimeException("JsonArray should start with '[' symbol [" + stream.determinePosition(stream.getCurrentPosition() - 1) + "]");
        }

        JsonArray obj = new JsonArray();
        while (true) {
            stream.skipBlank();
            c = stream.getCharNotMove();
            if (c == ']') {
                stream.getChar();
                break;
            } else {
                JsonElement element = readValuePart();
                obj.getElements().add(element);
                stream.skipBlank();
                char comma = stream.getCharNotMove();
                if (comma == ',') {
                    stream.getChar();
                }
            }
        }

        return obj;
    }

    public JsonElement readValuePart() {
        JsonElement element = null;
        stream.skipBlank();
        char c = stream.getCharNotMove();
        if (c == '{') {
            element = parseJsonObject();
        } else if (c == '[') {
            element = parseJsonArray();
        } else if (c == '"' || c == '\'') {
            String str = stream.readString();
            element = new JsonString(str);
        } else if (Character.isDigit(c)) {
            int position = stream.getCurrentPosition();
            String numbString = stream.readDigit();
            try {
                float digit = Float.parseFloat(numbString);
                element = new JsonNumber(digit);
            } catch (NumberFormatException ex) {
                throw new RuntimeException("Cannot parse number '" + numbString + "' [" + stream.determinePosition(position) + "'");
            }
        } else if (c == 't' || c == 'T' || c == 'f' || c == 'F') {
            String word = stream.readWord();
            if (word.equalsIgnoreCase("true") || word.equalsIgnoreCase("false")) {
                element = new JsonBoolean(word.equalsIgnoreCase("true"));
            }
        }
        return element;
    }

    public FieldValuePair readEntry() {
        String fieldName = stream.readString();
        stream.skipBlank();
        char separator = stream.getChar();
        if (separator != ':') {
            throw new RuntimeException("You should separate field and value with ':' [" + stream.determinePosition(stream.getCurrentPosition() - 1) + "]");
        }

        JsonElement element = readValuePart();
        return new FieldValuePair(fieldName, element);
    }
}
