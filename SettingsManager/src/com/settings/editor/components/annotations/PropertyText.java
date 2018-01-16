package com.settings.editor.components.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface PropertyText {

    public String category() default "def";

    public String name() default "defaultName";

    public boolean multiline() default false;

    public String width() default "100px";

    public String height() default "24px";
    public int length() default Integer.MAX_VALUE;
}
