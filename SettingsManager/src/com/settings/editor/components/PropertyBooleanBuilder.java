package com.settings.editor.components;

import com.gooddies.events.ValueChangedEvent;
import com.gooddies.swing.hCheckBox;
import com.settings.editor.builder.PropertyHolder;
import com.settings.editor.components.annotations.PropertyBoolean;
import javax.swing.JComponent;
import javax.swing.JPanel;

/**
 * @author sad
 */
public class PropertyBooleanBuilder extends AbstractComponentPropertyBuilder {

    @Override
    public JComponent createComponent(final PropertyHolder property, JPanel container) {
        hCheckBox booleanField = new hCheckBox(property.getName(), property.getValueBoolean());
        PropertyBoolean annotation = (PropertyBoolean) property.getAnnotation();

        booleanField.setValue(property.getValueBoolean());

        container.add(booleanField, "width " + annotation.width() + ", height " + annotation.height() + ", wrap");
        booleanField.setValueChangedEvent(new ValueChangedEvent<Boolean>() {
            @Override
            protected void valueChanged(Boolean value) {
                property.setValue(value);
            }
        });
        return null;
    }
}
