package com.settings.editor.components.annotations;

import com.settings.editor.annotations.PropertyNumberType;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface PropertyNumberDouble {

    public String category() default "def";

    public String name() default "defaultName";

    public double min() default -1;

    public double max() default -1;

    public PropertyNumberType type() default PropertyNumberType.TEXTBOX;

    public String width() default "100%";

    public String height() default "24px";
}
