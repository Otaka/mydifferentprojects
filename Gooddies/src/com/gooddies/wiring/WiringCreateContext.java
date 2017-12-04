package com.gooddies.wiring;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author sad
 */
public class WiringCreateContext {

    private List<Class> creationComponentStack;
    private Map<Class, Object> classToObject = new HashMap<Class, Object>();

    public WiringCreateContext() {
        creationComponentStack = new ArrayList<Class>();
    }

    public void add(Class clazz, Object object) {
        classToObject.put(clazz, object);
    }

    public Object getObject(Class clazz) {
        return classToObject.get(clazz);
    }

    public boolean add(Class clazz) {
        if (creationComponentStack.contains(clazz)) {
            return false;
        }

        creationComponentStack.add(clazz);
        return true;
    }

    public List<Class> getCreationComponentStack() {
        return creationComponentStack;
    }
}
