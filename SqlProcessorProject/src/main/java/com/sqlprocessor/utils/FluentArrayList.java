package com.sqlprocessor.utils;

import java.util.ArrayList;
import java.util.Collection;

/**
 * @author sad
 */
public class FluentArrayList<T> extends ArrayList<T> {

    public FluentArrayList() {
    }

    public FluentArrayList(T... objects) {
        for (T obj : objects) {
            add(obj);
        }
    }

    public FluentArrayList(Collection<T> objects) {
        for (T obj : objects) {
            add(obj);
        }
    }

    public FluentArrayList putObject(T obj) {
        add(obj);
        return this;
    }
}
