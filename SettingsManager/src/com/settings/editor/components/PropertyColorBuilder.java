package com.settings.editor.components;

import com.settings.editor.builder.PropertyHolder;
import com.settings.editor.components.annotations.PropertyColor;
import java.awt.Color;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.*;

/**
 * @author sad
 */
public class PropertyColorBuilder extends AbstractComponentPropertyBuilder {

    @Override
    public JComponent createComponent(final PropertyHolder property, final JPanel container) {
        JLabel label = new JLabel(property.getName());
        PropertyColor annotation = (PropertyColor) property.getAnnotation();
        final JPanel colorPanel = new JPanel();
        colorPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        colorPanel.setOpaque(true);
        Color color = (Color) property.getValue();
        colorPanel.setBackground(color == null ? Color.WHITE : color);
        container.add(label, "wrap");
        container.add(colorPanel, "width " + annotation.width() + ", height " + annotation.height() + ", wrap");
        colorPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                Color newColor = JColorChooser.showDialog(container, "Choose color", colorPanel.getBackground());
                if (newColor != null && newColor != colorPanel.getBackground()) {
                    colorPanel.setBackground(newColor);
                    colorPanel.repaint();
                    property.setValue(newColor);
                }
            }
        });
        return null;
    }

}
