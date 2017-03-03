package com.simplepl.utils;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;

/**
 * @author sad
 */
public class AstDeserializer implements JsonDeserializer<AstValue>{

    @Override
    public AstValue deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        if(json.isJsonObject()){
            JsonObject jsonObject=json.getAsJsonObject();
            if(jsonObject.has("strValue")){
                return context.deserialize(json, StringValue.class);
            }else{
                return context.deserialize(json, AstMatcher.class);
            }
        }else if(json.isJsonPrimitive()){
            return new StringValue(json.getAsString());
        }
        throw new IllegalArgumentException("Cannot deserialize json "+json.toString());
    }

}
