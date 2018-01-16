package com.settings.editor.builder;

/**
 * @author sad
 */
public class CategoryProperties {
    private boolean scroll=false;

    public CategoryProperties setScroll(boolean scroll) {
        this.scroll = scroll;
        return this;
    }

    public boolean isScroll() {
        return scroll;
    }
    
}
