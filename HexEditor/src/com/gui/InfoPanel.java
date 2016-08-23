package com.gui;

import com.gooddies.events.Lookup;
import com.gooddies.swing.hList;
import com.swingson.SwingsonGui;
import com.swingson.SwingsonGuiBuilder;
import java.util.List;
import javax.swing.*;

/**
 * @author Dmitry
 */
public class InfoPanel extends JPanel {
    private final hList dataList = new hList();

    public InfoPanel() {
        final SwingsonGui gui = SwingsonGuiBuilder.createGuiFromJsonInPackage(this);
        Lookup.getDefault().addChangeEvent(InfoMessage.class, new Lookup.LookupEventChangedValue<InfoMessage>() {
            @Override
            public void change(InfoMessage newValue) {
                JLabel label = (JLabel) gui.getDefinedComponent(newValue.getId()).getComponent();
                label.setText(newValue.getInfoMessage());

                revalidate();
                validate();
                repaint();
            }
        });

        Lookup.getDefault().addChangeEvent(InfoMessageObject.class, new Lookup.LookupEventChangedValue<InfoMessageObject>() {
            @Override
            public void change(InfoMessageObject newValue) {
                dataList.clearItems();
                if (newValue.getMessage() != null) {
                    dataList.addItems((List) newValue.getMessage());
                }
            }
        });
    }

}
