package com.sqlprocessor.buffers;

import com.sqlprocessor.compiler.exception.CannotCompileClassException;
import java.lang.reflect.Field;
import java.util.List;
import org.apache.commons.lang.StringUtils;

/**
 * @author sad
 */
public class SqlBuffer<T> {

    private String name;
    private Class<T> dataClass;
    private List<T> data;
    private List<String> fields;

    public SqlBuffer(String name, Class<T> dataClass, List<String> fields) {
        this.name = name;
        this.dataClass = dataClass;
        this.fields = fields;
    }

    public List<String> getFields() {
        return fields;
    }

    public String getName() {
        return name;
    }

    public Class<T> getDataClass() {
        return dataClass;
    }

    public List<T> getData() {
        return data;
    }

    public void setData(List<T> data) {
        this.data = data;
    }

    public Field getField(String fieldName) {
        Class clazz = dataClass;
        while (clazz != null) {
            try {
                Field field = clazz.getDeclaredField(fieldName);
                field.setAccessible(true);
                return field;
            } catch (Exception ex) {
            }

            clazz = clazz.getSuperclass();
        }

        throw new IllegalArgumentException("Class " + dataClass.getSimpleName() + " does not have field [" + fieldName + "]");
    }

    public String getGetterMethod(String fieldName) {
        String capitalizedFieldName = StringUtils.capitalize(fieldName);
        try {
            return dataClass.getMethod("get"+capitalizedFieldName, new Class[0]).getName();
        } catch (NoSuchMethodException ex) {
            ex.printStackTrace();
        } catch (SecurityException ex) {
            ex.printStackTrace();
        }
        try {
            return dataClass.getMethod("is"+capitalizedFieldName, new Class[0]).getName();
        } catch (NoSuchMethodException ex) {
            ex.printStackTrace();
        } catch (SecurityException ex) {
            ex.printStackTrace();
        }

        throw new CannotCompileClassException("Class ["+dataClass.getSimpleName()+"] does not have getter for field ["+fieldName+"]. Possible names (get"+capitalizedFieldName+" or for boolean fields is"+capitalizedFieldName+")");
    }

    @Override
    public String toString() {
        return "" + name + ". Count of records = " + (data == null ? "null" : data.size());
    }

}
