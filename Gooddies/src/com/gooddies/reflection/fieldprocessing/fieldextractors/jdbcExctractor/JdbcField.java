package com.gooddies.reflection.fieldprocessing.fieldextractors.jdbcExctractor;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @author sad
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface JdbcField {

    public String field();
}
