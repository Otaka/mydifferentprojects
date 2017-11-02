package com.gooddies.swing;

import com.gooddies.persistence.Properties;

import javax.swing.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * @author Dmitry
 */
public class JpSplitPanel extends JSplitPane {

    public JpSplitPanel(String name) {
        this(name, 0.5f);
    }

    public JpSplitPanel(String name, float dividerLocation) {
        final String dividerPropName = name + ".divider";
        addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                if (evt.getPropertyName().equals(JSplitPane.LAST_DIVIDER_LOCATION_PROPERTY)) {
                    Properties.get().putInt(dividerPropName, getDividerLocation());
                }
            }
        });

        if (Properties.get().hasKey(dividerPropName)) {
            setDividerLocation(Properties.get().getInt(dividerPropName));
        } else {
            setDividerLocation(dividerLocation);
        }
    }
}
