package com.kotorresearch.script.interpreter;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @author Dmitry
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface ScriptFunctionAnnotation {

    public int index();
}
