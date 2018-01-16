package com.sqlprocessor.utils;

import java.util.HashMap;

/**
 * @author sad
 */
public class FluentHashMap extends HashMap {

    public FluentHashMap putObject(String key, Object value) {
        put(key, value);
        return this;
    }
}
