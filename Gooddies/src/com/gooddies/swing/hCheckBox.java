package com.gooddies.swing;

import com.gooddies.events.ValueChangedEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JCheckBox;
import javax.swing.SwingUtilities;

/**
 * @author sad
 */
public class hCheckBox extends JCheckBox {

    private ValueChangedEvent<Boolean> valueChangedEvent;

    public hCheckBox() {
    }
    
    public hCheckBox(String text) {
        super(text);
    }

    public hCheckBox(String text, Icon icon) {
        super(text, icon);
    }

    public hCheckBox(String text, Icon icon, boolean selected) {
        super(text, icon, selected);
    }

    public hCheckBox(Action a) {
        super(a);
    }

    public hCheckBox(Icon icon) {
        super(icon);
    }

    public hCheckBox(Icon icon, boolean selected) {
        super(icon, selected);
    }

    public hCheckBox(String text, boolean selected) {
        super(text, selected);
    }

    private void fireValueChanged(){
        if(valueChangedEvent!=null){
            SwingUtilities.invokeLater(new Runnable() {

                @Override
                public void run() {
                    valueChangedEvent.fire(isSelected(), hCheckBox.this);
                }
            });
        }
    }

    @Override
    protected void init(String text, Icon icon) {
        super.init(text, icon);
        addKeyListener(new KeyAdapter() {

            @Override
            public void keyReleased(KeyEvent e) {
                if(e.getKeyCode()==KeyEvent.VK_SPACE){
                    fireValueChanged();
                }
            }
        });
        
        addMouseListener(new MouseAdapter() {
            private boolean state=false;
            @Override
            public void mousePressed(MouseEvent e) {
                state=isSelected();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                SwingUtilities.invokeLater(new Runnable() {

                    @Override
                    public void run() {
                        if(isSelected()!=state){
                            fireValueChanged();
                        }
                    }
                });
            }
        });
    }
    
    
    
    public void setValueChangedEvent(ValueChangedEvent<Boolean> valueChangedEvent) {
        this.valueChangedEvent=valueChangedEvent;
    }
    
    public void setValue(boolean value){
        setSelected(value);
    }
    
    public boolean getValue(){
        return isSelected();
    }
}
