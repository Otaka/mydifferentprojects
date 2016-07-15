package com.settings.editor.components;

import com.gooddies.events.ValueChangedEvent;
import com.gooddies.texteditors.DefaultTextField;
import com.settings.editor.builder.PropertyHolder;
import com.settings.editor.components.annotations.PropertyText;
import com.settings.editor.components.utils.ContextMouseListener;
import java.awt.Component;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.plaf.ComponentUI;
import javax.swing.text.BadLocationException;

/**
 * @author sad
 */
public class PropertyTextBuilder extends AbstractComponentPropertyBuilder {

    @Override
    public JComponent createComponent(final PropertyHolder property, JPanel container) {
        JLabel label = new JLabel(property.getName());
        PropertyText annotation = (PropertyText) property.getAnnotation();

        if (annotation.multiline()) {
            return createMultiLineTextField(annotation, label, container, property);
        } else {
            return createSingleLineTextField(annotation, label, container, property);
        }
    }

    private JComponent createSingleLineTextField(PropertyText annotation, JLabel label, JPanel container, final PropertyHolder property) {
        DefaultTextField textField = new DefaultTextField();
        textField.addMouseListener(new ContextMouseListener());
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

        return textField;
    }

    private JComponent createMultiLineTextField(PropertyText annotation, JLabel label, JPanel container, final PropertyHolder property) {
        JTextPane textField = new JTextPane() {
            @Override
            public boolean getScrollableTracksViewportWidth() {
                Component parent = getParent();
                ComponentUI ui = getUI();

                return parent != null ? (ui.getPreferredSize(this).width <= parent
                        .getSize().width) : true;
            }
        };
        textField.setText(property.getValueString());
        textField.addMouseListener(new ContextMouseListener());
        if (annotation.length() != Integer.MAX_VALUE) {
            throw new RuntimeException("Multiline properties don't work with length attribute Property " + property.getField().getDeclaringClass().getName());
        }

        textField.getDocument().addDocumentListener(new DocumentListener() {

            @Override
            public void insertUpdate(DocumentEvent e) {
                try {
                    property.setValue(e.getDocument().getText(0, e.getDocument().getLength()));
                } catch (BadLocationException ex) {
                    ex.printStackTrace();
                }
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                try {
                    property.setValue(e.getDocument().getText(0, e.getDocument().getLength()));
                } catch (BadLocationException ex) {
                    ex.printStackTrace();
                }
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                try {
                    property.setValue(e.getDocument().getText(0, e.getDocument().getLength()));
                } catch (BadLocationException ex) {
                    ex.printStackTrace();
                }
            }
        });

        container.add(label, "wrap");
        JScrollPane scrollPane = new JScrollPane(textField);

        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        container.add(scrollPane, "width " + annotation.width() + ", height " + annotation.height() + ", wrap");

        return scrollPane;
    }

}
