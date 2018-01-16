package com.settings.editor.components;

import com.settings.editor.builder.PropertyHolder;
import java.lang.annotation.Annotation;
import javax.swing.JComponent;
import javax.swing.JPanel;

/**
 * @author sad
 */
public abstract class AbstractComponentPropertyBuilder {
    public abstract JComponent createComponent(PropertyHolder property,JPanel container);
}
