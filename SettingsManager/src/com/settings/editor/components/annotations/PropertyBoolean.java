package com.settings.editor.components.annotations;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface PropertyBoolean {

    public String category() default "def";

    public String name() default "defaultName";

    public String width() default "100px";

    public String height() default "24px";
}
