package com.gooddies.wiring;

/**
 * @author sad
 */
public class WiringComponentHolder {

    private final String name;
    private Object object;
    private final boolean singleton;
    private final Class clazz;
    private final boolean isDefault;
    private final boolean lazy;

    public WiringComponentHolder(String name, Class clazz, Object object, boolean singleton, boolean isDefault, boolean lazy) {
        this.name = name;
        this.object = object;
        this.singleton = singleton;
        this.clazz = clazz;
        this.isDefault = isDefault;
        this.lazy = lazy;
    }

    public void setObject(Object object) {
        this.object = object;
    }

    public boolean isLazy() {
        return lazy;
    }

    public boolean isDefault() {
        return isDefault;
    }

    public Class getClazz() {
        return clazz;
    }

    public boolean isSingleton() {
        return singleton;
    }

    public String getName() {
        return name;
    }

    public Object getObject() {
        return object;
    }
}
