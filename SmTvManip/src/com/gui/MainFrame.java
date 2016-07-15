package com.gui;

import com.gooddies.events.ValueChangedEvent;
import com.gooddies.swing.hList;
import com.swingson.SwingsonGuiBuilder;
import java.awt.HeadlessException;
import javax.swing.JEditorPane;
import javax.swing.JFrame;

/**
 * @author sad
 */
public class MainFrame extends JFrame {

    private final hList<Object> categoriesList = new hList<>();
    private final JEditorPane editor = new JEditorPane();
    
    public MainFrame() throws HeadlessException {
        setTitle("Editor");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(500, 500);
        SwingsonGuiBuilder.createGuiFromJsonInPackage(this);
        categoriesList.setValueChangedEvent(new ValueChangedEvent<Object>() {

            @Override
            protected void valueChanged(Object value) {
                valueSelectedEvent(value);
            }
        });
    }

    public void valueSelectedEvent(Object value){
        
    }
}
