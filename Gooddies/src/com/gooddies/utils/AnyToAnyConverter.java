package com.gooddies.utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author sad
 */
public class AnyToAnyConverter {

    private static Map<Long, AbstractConverterProcessor> processors = new HashMap<Long, AbstractConverterProcessor>();

    public static void addProcessor(Class fromClass, Class toClass, AbstractConverterProcessor processor) {
        if (fromClass == null || toClass == null) {
            throw new RuntimeException("fromClass or toClass should not be empty in AnyToAnyProcessor");
        }
        long hash = fromClass.hashCode() + toClass.hashCode();
        processors.put(hash, processor);
    }

    static {
        addProcessor(String.class, String.class, new AbstractConverterProcessor.StringToStringProcessor());
        addProcessor(String.class, Integer.class, new AbstractConverterProcessor.StringToIntegerProcessor());
        addProcessor(String.class, int.class, new AbstractConverterProcessor.StringToIntegerProcessor());
        addProcessor(String.class, Long.class, new AbstractConverterProcessor.StringToLongProcessor());
        addProcessor(String.class, long.class, new AbstractConverterProcessor.StringToLongProcessor());
        addProcessor(String.class, Float.class, new AbstractConverterProcessor.StringToFloatProcessor());
        addProcessor(String.class, float.class, new AbstractConverterProcessor.StringToFloatProcessor());
        addProcessor(String.class, Double.class, new AbstractConverterProcessor.StringToDoubleProcessor());
        addProcessor(String.class, double.class, new AbstractConverterProcessor.StringToDoubleProcessor());
        addProcessor(String.class, Boolean.class, new AbstractConverterProcessor.StringToBooleanProcessor());
        addProcessor(String.class, boolean.class, new AbstractConverterProcessor.StringToBooleanProcessor());
        addProcessor(Double.class, Integer.class, new AbstractConverterProcessor.DoubleToIntegerProcessor());
        addProcessor(Double.class, double.class, new AbstractConverterProcessor.DoubleToDoubleProcessor());
        addProcessor(double.class, Double.class, new AbstractConverterProcessor.DoubleToDoubleProcessor());
        addProcessor(Double.class, int.class, new AbstractConverterProcessor.DoubleToIntegerProcessor());
        addProcessor(double.class, int.class, new AbstractConverterProcessor.DoubleToIntegerProcessor());
        addProcessor(double.class, Integer.class, new AbstractConverterProcessor.DoubleToIntegerProcessor());
        addProcessor(Double.class, Float.class, new AbstractConverterProcessor.DoubleToFloatProcessor());
        addProcessor(Double.class, float.class, new AbstractConverterProcessor.DoubleToFloatProcessor());
        addProcessor(double.class, float.class, new AbstractConverterProcessor.DoubleToFloatProcessor());
        addProcessor(double.class, Float.class, new AbstractConverterProcessor.DoubleToFloatProcessor());
        addProcessor(double.class, String.class, new AbstractConverterProcessor.DoubleToStringProcessor());
        addProcessor(Double.class, String.class, new AbstractConverterProcessor.DoubleToStringProcessor());
    }

    public static Object convert(Class fromClass, Class toClass, Object value) {
        if (fromClass == null || toClass == null) {
            throw new RuntimeException("fromClass or toClass should not be empty in AnyToAnyProcessor");
        }
        long hash = fromClass.hashCode() + toClass.hashCode();
        AbstractConverterProcessor processor = processors.get(hash);
        if (processor == null) {
            throw new RuntimeException("Cannot convert from " + fromClass.getName() + " to " + toClass.getName() + " because there is no associated processor");
        }
        if (value == null) {
            return null;
        }
        return processor.convert(value);
    }
}
