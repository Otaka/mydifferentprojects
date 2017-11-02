package com.gooddies.wiring;

import com.gooddies.wiring.annotations.Wire;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * @author sad
 */
public class ClassInfo {

    private Method[] postMethods;
    private Field[] wiredFields;
    private Wire[] annotations;
    private Constructor constructor;

    public ClassInfo(Method[] postMethods, Field[] wiredFields, Wire[] annotations, Constructor constructor) {
        this.postMethods = postMethods;
        this.wiredFields = wiredFields;
        this.annotations = annotations;
        this.constructor = constructor;
    }

    public Constructor getConstructor() {
        return constructor;
    }

    public Method[] getPostMethods() {
        return postMethods;
    }

    public Field[] getWiredFields() {
        return wiredFields;
    }

    public Wire[] getAnnotations() {
        return annotations;
    }
}
