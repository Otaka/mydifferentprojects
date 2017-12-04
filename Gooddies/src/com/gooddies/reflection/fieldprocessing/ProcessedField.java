package com.gooddies.reflection.fieldprocessing;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author sad
 */
public class ProcessedField {

    protected Method reader;
    protected Method writer;
    protected Field field;

    public ProcessedField() {
    }

    public Method getReader() {
        return reader;
    }

    public Field getField() {
        return field;
    }

    public void setField(Field field) {
        this.field = field;
    }

    public void setReader(Method reader) {
        this.reader = reader;
    }

    public Method getWriter() {
        return writer;
    }

    public void setWriter(Method writer) {
        this.writer = writer;
    }

    public Object getValue(Object instance) {
        try {
            if (reader != null) {
                try {
                    return reader.invoke(instance);
                } catch (Exception ex) {
                    Logger.getLogger(ProcessedField.class.getName()).log(Level.SEVERE, null, ex);
                }
                return null;
            }
            return field.get(instance);
        } catch (Exception ex) {
            Logger.getLogger(ProcessedField.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public void setValue(Object instance, Object value) {
        try {
            if (writer != null) {
                try {
                    writer.invoke(instance, value);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
            field.set(instance, value);
        } catch (IllegalArgumentException ex) {
            String message = String.format("Cannot assign value of type %s to the field %s of the class %s",
                    value.getClass().toString(), field.getName(), field.getDeclaringClass().getCanonicalName());
            throw new RuntimeException(message);
        } catch (IllegalAccessException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public String toString() {
        return "Field = " + field.getName();
    }
}
