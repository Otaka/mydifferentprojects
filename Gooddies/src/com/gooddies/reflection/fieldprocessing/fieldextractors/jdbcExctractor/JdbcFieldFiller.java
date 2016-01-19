package com.gooddies.reflection.fieldprocessing.fieldextractors.jdbcExctractor;

import com.gooddies.reflection.fieldprocessing.FieldExtractor;
import com.gooddies.reflection.fieldprocessing.FieldProcessor;
import com.gooddies.reflection.fieldprocessing.ProcessedField;
import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * @author sad
 */
@SuppressWarnings("unchecked")
public class JdbcFieldFiller<T> {

    private List<ProcessedJdbcField> processedFields;

    public JdbcFieldFiller(Class clazz) {
        FieldProcessor processor = new FieldProcessor(clazz, new FieldExtractor() {
            @Override
            public ProcessedField tryExtractField(Field f, Class clazz) throws SecurityException {
                if (f.getAnnotation(JdbcField.class) != null) {
                    JdbcField annotation = f.getAnnotation(JdbcField.class);
                    String fieldName = annotation.field();
                    ProcessedJdbcField field = new ProcessedJdbcField();
                    field.setDatabaseField(fieldName);
                    return field;
                }
                return null;
            }
        });

        processedFields = (List) processor.getProcessedFields();
    }

    public T fill(Object instance, ResultSet rs) throws SQLException {
        for (ProcessedJdbcField field : processedFields) {
            Object value = rs.getObject(field.getDatabaseField());
            field.setValue(instance, value);
        }
        return (T)instance;
    }
}
