package com.gooddies.wiring;

import com.gooddies.exceptions.WiringException;
import com.gooddies.utils.FileUtils;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author sad
 */
public class JsonPrimitiveComponentInitializer {

    public void process(Wiring wiring, File file) {
        String jsonString = FileUtils.readToString(file, "UTF-8");
        JsonParser parser = new JsonParser();
        JsonElement object = parser.parse(jsonString);

        JsonObject settings = object.getAsJsonObject();
        settings=settings.getAsJsonObject("config");
        if(settings==null || settings.isJsonNull()){
            throw new WiringException("Json configfile should contain one main entry 'config'");
        }
        
        for (Entry<String, JsonElement> entry : settings.entrySet()) {
            Object value;
            if (entry.getValue().isJsonPrimitive()) {
                value = processPrimitive((JsonPrimitive) entry.getValue());
            } else if (entry.getValue().isJsonArray()) {
                value = processArray((JsonArray) entry.getValue(), file, entry.getKey());
            } else {
                value = processMap((JsonObject) entry.getValue(), file, entry.getKey());
            }

            wiring.addPrimitiveComponent(entry.getKey(), value);
        }
    }

    private Object processArray(JsonArray array, File file, String propName) {
        int size = array.size();
        List<Object>list = new CopyOnWriteArrayList<Object>();
        for (int i = 0; i < size; i++) {
            if (!array.get(i).isJsonPrimitive()) {
                throw new WiringException("Json config file array entry should contain only primitive types. File=" + file.getAbsolutePath() + ". Property=" + propName);
            }

            list.add(processPrimitive((JsonPrimitive) array.get(i)));
        }

        return list;
    }

    private Object processMap(JsonObject obj, File file, String propName) {
        Map<String, Object> map = new HashMap<String, Object>();
        for (Entry<String, JsonElement> entry : obj.entrySet()) {
            if (!entry.getValue().isJsonPrimitive()) {
                throw new WiringException("Json config file object entry should contain only primitive types. File=" + file.getAbsolutePath() + ". Property=" + propName);
            }

            map.put(entry.getKey(), processPrimitive((JsonPrimitive) entry.getValue()));
        }

        return map;
    }

    private Object processPrimitive(JsonPrimitive primitive) {
        if (primitive.isBoolean()) {
            return primitive.getAsBoolean();
        }

        if (primitive.isNumber()) {
            return primitive.getAsDouble();
        }

        return primitive.getAsString();
    }
}
