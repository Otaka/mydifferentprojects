package com.settings.editor.components;

import com.gooddies.events.ValueChangedEvent;
import com.gooddies.texteditors.DefaultTextField;
import com.settings.editor.builder.PropertyHolder;
import com.settings.editor.components.annotations.PropertyText;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * @author sad
 */
public class PropertyTextBuilder extends AbstractComponentPropertyBuilder {

    @Override
    public JComponent createComponent(final PropertyHolder property, JPanel container) {
        JLabel label = new JLabel(property.getName());
        DefaultTextField textField = new DefaultTextField();
        PropertyText annotation = (PropertyText) property.getAnnotation();

        textField.setTextAndScrollToStart(property.getValueString());
        if (annotation.length() != Integer.MAX_VALUE) {
            textField.setMaxTextLength(annotation.length());
        }
        container.add(label, "wrap");
        container.add(textField, "width " + annotation.width() + ", height " + annotation.height() + ", wrap");
        textField.setValueChangedEvent(new ValueChangedEvent<String>() {
            @Override
            protected void valueChanged(String value) {
                property.setValue(value);
            }
        });
        return null;
    }

}
