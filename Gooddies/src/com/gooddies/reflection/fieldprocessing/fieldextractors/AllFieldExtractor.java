package com.gooddies.reflection.fieldprocessing.fieldextractors;

import com.gooddies.reflection.fieldprocessing.FieldExtractor;
import com.gooddies.reflection.fieldprocessing.ProcessedField;
import com.gooddies.reflection.fieldprocessing.exception.FieldExctractionStopException;
import java.lang.reflect.Field;

/**
 * Extract all fields except Object fields
 */
public class AllFieldExtractor extends FieldExtractor {

    public AllFieldExtractor(boolean searchForReader, boolean searchForWriter) {
        super(searchForReader, searchForWriter);
    }

    public AllFieldExtractor() {
    }

    @Override
    public ProcessedField tryExtractField(Field f, Class clazz) throws SecurityException {
        if (clazz == Object.class) {
            throw new FieldExctractionStopException();
        }
        return new ProcessedField();
    }
}
