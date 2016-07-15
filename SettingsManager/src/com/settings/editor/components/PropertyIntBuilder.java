package com.settings.editor.components;

import com.gooddies.events.ValueChangedEvent;
import com.gooddies.texteditors.DefaultIntegerTextField;
import com.settings.editor.builder.PropertyHolder;
import com.settings.editor.components.annotations.PropertyInt;
import com.settings.editor.components.utils.ContextMouseListener;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * @author sad
 */
public class PropertyIntBuilder extends AbstractComponentPropertyBuilder {

    @Override
    public JComponent createComponent(final PropertyHolder property, JPanel container) {
        JLabel label = new JLabel(property.getName());
        DefaultIntegerTextField textField = new DefaultIntegerTextField();
        textField.addMouseListener(new ContextMouseListener());
        PropertyInt annotation = (PropertyInt) property.getAnnotation();

        textField.setValue(property.getValueInt());
        if (annotation.max() != Integer.MAX_VALUE) {
            textField.setMaxValue(annotation.max());
        }
        if (annotation.min() != Integer.MIN_VALUE) {
            textField.setMinValue(annotation.min());
        }
        container.add(label, "wrap");
        container.add(textField, "width " + annotation.width() + ", height " + annotation.height() + ", wrap");
        textField.setValueChanged(new ValueChangedEvent<Integer>() {
            @Override
            protected void valueChanged(Integer value) {
                property.setValue(value);
            }
        });
        return null;
    }

}
