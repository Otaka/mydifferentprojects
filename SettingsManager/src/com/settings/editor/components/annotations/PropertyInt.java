package com.settings.editor.components.annotations;

import com.settings.editor.annotations.PropertyNumberType;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface PropertyInt {

    public String category() default "def";

    public String name() default "defaultName";

    public int min() default Integer.MIN_VALUE;

    public int max() default Integer.MAX_VALUE;

    public PropertyNumberType type() default PropertyNumberType.TEXTBOX;

    public String width() default "100px";

    public String height() default "24px";
}
