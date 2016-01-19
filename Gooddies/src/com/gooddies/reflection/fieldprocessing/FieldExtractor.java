package com.gooddies.reflection.fieldprocessing;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * @author sad
 */
@SuppressWarnings("unchecked")
public abstract class FieldExtractor {

    private boolean searchForReader = true;
    private boolean searchForWriter = true;

    public abstract ProcessedField tryExtractField(Field f, Class clazz) throws SecurityException;

    public FieldExtractor(boolean searchForReader, boolean searchForWriter) {
        this.searchForReader = searchForReader;
        this.searchForWriter = searchForWriter;
    }

    public FieldExtractor() {
    }

    public FieldExtractor setSearchForReader(boolean searchForReader) {
        this.searchForReader = searchForReader;
        return this;
    }

    public FieldExtractor setSearchForWriter(boolean searchForWriter) {
        this.searchForWriter = searchForWriter;
        return this;
    }

    public ProcessedField extractField(Field f, Class clazz) {
        ProcessedField pf = tryExtractField(f, clazz);
        if (pf != null) {
            initField(f, clazz, pf);
        }
        return pf;
    }

    private String firstLetterToUpper(String name) {
        if (name != null && !name.isEmpty()) {
            return name.substring(0, 1).toUpperCase() + name.substring(1);
        }
        return "";
    }

    protected void initField(Field field, Class clazz, ProcessedField processed) {
        if (!field.isAccessible()) {
            field.setAccessible(true);
        }
        processed.setField(field);
        if (searchForReader) {
            String methodName = field.getName();
            String fullMethodName1 = "get" + firstLetterToUpper(methodName);
            String fullMethodName2 = "is" + firstLetterToUpper(methodName);
            Method readerMethod = extractMethod(clazz, fullMethodName1);
            if (readerMethod == null) {
                readerMethod = extractMethod(clazz, fullMethodName2);
            }

            if (readerMethod == null) {
                String message = String.format("Class %s does not have corresponding getter[%s() or %s()] for field %s", clazz.getCanonicalName(), fullMethodName1, fullMethodName2, field.getName());
                throw new RuntimeException(message);
            }
            if (!readerMethod.isAccessible()) {
                readerMethod.setAccessible(true);
            }
            processed.setReader(readerMethod);
        }
        if (searchForWriter) {
            String methodName = field.getName();
            String fullMethodName1 = "set" + firstLetterToUpper(methodName);
            Method writerMethod = extractMethod(clazz, fullMethodName1, field.getType());

            if (writerMethod == null) {
                return;
                //String fName = field.getType().getName();
                //String message = String.format("Class %s does not have corresponding setter[%s(%s)] for field %s", clazz.getCanonicalName(), fullMethodName1, fName, field.getName());
                //throw new RuntimeException(message);
            }
            if (!writerMethod.isAccessible()) {
                writerMethod.setAccessible(true);
            }
            processed.setWriter(writerMethod);
        }
    }

    public static Method extractMethod(Class clazz, String methodName, Class... params) throws SecurityException {
        Class tc = clazz;
        while (tc != null) {
            try {
                Method method = tc.getDeclaredMethod(methodName, params);
                return method;
            } catch (NoSuchMethodException ex) {
            }
            tc = tc.getSuperclass();
        }
        return null;
    }
}
