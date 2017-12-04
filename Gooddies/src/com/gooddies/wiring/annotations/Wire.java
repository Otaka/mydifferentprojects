package com.gooddies.wiring.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author sad
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Wire {
    public final String defaultName="WIRE_CLASS_NAME_DEFAULT";
    public String value() default defaultName;
}
