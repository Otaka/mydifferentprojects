package com.settings.editor.builder;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * @author sad
 */
public class PropertyHolder {

    private final Field field;
    private final Method setter;
    private final Method getter;
    private final Object object;
    private final Annotation annotation;
    private final String category;
    private final String name;

    public PropertyHolder(Field field, Method setter, Method getter, Object object, Annotation annotation, String category, String name) {
        this.annotation = annotation;
        this.field = field;
        this.setter = setter;
        this.getter = getter;
        this.object = object;
        this.category = category;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String getCategory() {
        return category;
    }

    public Annotation getAnnotation() {
        return annotation;
    }

    public Field getField() {
        return field;
    }

    public Method getSetter() {
        return setter;
    }

    public Method getGetter() {
        return getter;
    }

    public Object getObject() {
        return object;
    }

    public void setValue(Object obj) {
        if (setter != null) {
            if (!setter.isAccessible()) {
                setter.setAccessible(true);
            }
            try {
                setter.invoke(object, obj);
                return;
            } catch (Exception ex) {
                ex.printStackTrace();
                throw new RuntimeException(ex);
            }
        }

        if (!field.isAccessible()) {
            field.setAccessible(true);
        }
        try {
            field.set(object, obj);
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException();
        }
    }

    public int getValueInt() {
        Object value = getValue();
        if (value == null) {
            return 0;
        }
        if (value instanceof Integer) {
            return (Integer) value;
        }
        throw new RuntimeException("Field " + field.getName() + " in class " + field.getDeclaringClass() + " is not integer, but " + field.getType().getName());
    }

    public double getValueDouble() {
        Object value = getValue();
        if (value == null) {
            return 0.0;
        }
        if (value instanceof Double) {
            return (Double) value;
        }
        if (value instanceof Float) {
            return (Float) value;
        }
        if (value instanceof Integer) {
            return (double) (Integer) value;
        }
        throw new RuntimeException("Field " + field.getName() + " in class " + field.getDeclaringClass() + " is not integer or double or float, but " + field.getType().getName());
    }

    public String getValueString() {
        Object value = getValue();
        if (value == null) {
            return "";
        }
        if (value instanceof String) {
            return (String) value;
        }

        return value.toString();
    }

    public boolean getValueBoolean() {
        Object value = getValue();
        if (value == null) {
            return false;
        }
        if (value instanceof Boolean) {
            return (Boolean) value;
        }

        throw new RuntimeException("Field " + field.getName() + " in class " + field.getDeclaringClass() + " is not boolean, but " + field.getType().getName());
    }

    public Object getValue() {
        if (getter != null) {
            if (!getter.isAccessible()) {
                getter.setAccessible(true);
            }
            try {
                return getter.invoke(object);
            } catch (Exception ex) {
                ex.printStackTrace();
                throw new RuntimeException(ex);
            }
        }

        try {
            if (!field.isAccessible()) {
                field.setAccessible(true);
            }
            return field.get(object);
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException(ex);
        }
    }
}
