package com.settings.editor.components.annotations;

import java.lang.annotation.ElementType;
;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;



@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface PropertyFile {

    public String category() default "def";

    public String name() default "defaultName";

    public String width() default "100px";

    public String height() default "24px";

    /**
     Should be like:<br/> <b>JPG Images(JPG, JPEG)|jpg,jpeg ; BMP Images|bmp ; All Files|*
     */
    public String filter() default "All Files|*";
    public boolean selectDir()default false;
}
