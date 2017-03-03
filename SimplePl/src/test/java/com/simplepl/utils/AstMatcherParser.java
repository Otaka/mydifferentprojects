package com.simplepl.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * @author sad
 */
public class AstMatcherParser {

    public AstMatcher parse(String json) {
         GsonBuilder gsonBuilder=new GsonBuilder();
        gsonBuilder.registerTypeAdapter(AstValue.class, new AstDeserializer());
        gsonBuilder.create().fromJson(json, AstMatcher.class);
        return gsonBuilder.create().fromJson(json, AstMatcher.class);
    }
    
    public String toJson(AstMatcher matcher) {
        Gson gson = new Gson();
        return gson.toJson(matcher);
    }
}
