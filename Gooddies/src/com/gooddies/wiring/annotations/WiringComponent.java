package com.gooddies.wiring.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author sad
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface WiringComponent {
    public static final String defaultName="";
    public String name() default defaultName;
    
    /**
     * Is this bean singleton, and Wiring should create only one instance
     */
    public boolean singleton()default false;
    /**
     * If there are two beans with same names, Wiring should throw an exception.<br/>
     * But if some of the beans marked as <b>default</b>, it means, that this bean can be overriden by the other bean with the same name that are not default<br/>
     */
    public boolean isDefault()default false;
    
    public boolean lazy()default false;
}
