package com.gooddies.reflection.fieldprocessing.fieldextractors.jdbcExctractor;

import com.gooddies.reflection.fieldprocessing.ProcessedField;


/**
 * @author sad
 */
public class ProcessedJdbcField extends  ProcessedField {

    private String databaseField;

    public String getDatabaseField() {
        return databaseField;
    }

    public void setDatabaseField(String databaseField) {
        this.databaseField = databaseField;
    }
    
}
