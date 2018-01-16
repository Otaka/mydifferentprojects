package com.settings.editor.components.annotations;

import java.lang.annotation.ElementType;
;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;



@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface PropertyColor {

    public String category() default "def";

    public String name() default "defaultName";

    public String width() default "24px";

    public String height() default "24px";
}
