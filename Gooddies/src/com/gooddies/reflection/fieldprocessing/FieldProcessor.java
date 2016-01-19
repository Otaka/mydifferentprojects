package com.gooddies.reflection.fieldprocessing;

import com.gooddies.reflection.fieldprocessing.exception.FieldExctractionStopException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author sad
 */
public class FieldProcessor {

    private List<ProcessedField> fields;
    private Map<String, ProcessedField> fieldNameToFieldProcessor = new HashMap<String, ProcessedField>();

    public FieldProcessor(Class clazz, FieldExtractor extractor) {
        fields = getFields(clazz, extractor);
    }

    public List<ProcessedField> getProcessedFields() {
        return fields;
    }

    public ProcessedField getProcessedField(String name) {
        return fieldNameToFieldProcessor.get(name);
    }

    protected List<ProcessedField> getFields(Class clazz, FieldExtractor extractor) {
        ArrayList<ProcessedField> list = new ArrayList<ProcessedField>();
        Class tc = clazz;
        try {
            while (tc != null) {
                for (Field field : tc.getDeclaredFields()) {
                    ProcessedField processed = extractor.extractField(field, clazz);
                    if (processed != null) {
                        list.add(processed);
                        fieldNameToFieldProcessor.put(field.getName(), processed);
                    }
                }

                tc = tc.getSuperclass();
            }
        } catch (FieldExctractionStopException ex) {
        }

        return list;
    }
}
