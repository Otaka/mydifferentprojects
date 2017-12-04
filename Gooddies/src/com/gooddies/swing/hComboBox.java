package com.gooddies.swing;

import com.gooddies.events.ValueChangedEvent;
import com.gooddies.utils.IteratorToIterable;
import java.awt.Rectangle;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JComboBox;
import javax.swing.SwingUtilities;
import javax.swing.event.ListDataEvent;
import javax.swing.plaf.ComboBoxUI;
import javax.swing.plaf.basic.BasicComboPopup;
import javax.swing.plaf.basic.ComboPopup;

/**
 * @author sad
 */
@SuppressWarnings("unchecked")
public class hComboBox<T> extends JComboBox<T> implements Iterable<T> {

    private ValueChangedEvent<T> valueChanged;
    private int sizeOfPopup = -1;

    public <T> hComboBox() {
        init();
    }

    @SafeVarargs
    public hComboBox(T... items) {
        super(items);
        init();
    }

    private void init() {
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER || e.getKeyCode() == KeyEvent.VK_SPACE) {
                    if (isPopupVisible()) {
                        fireChanged();
                    }
                }
            }
        });
    }

    private void fireChanged() {
        if (valueChanged != null) {
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    T object = getSelectedItem();
                    if (object != null) {
                        valueChanged.fire(object, hComboBox.this);
                    }
                }
            });
        }
    }

    @Override
    public T getSelectedItem() {
        return (T) super.getSelectedItem();
    }

    public void setValueChangedEvent(ValueChangedEvent<T> valueChanged) {
        this.valueChanged = valueChanged;
    }

    public void setPopupWidth(int size) {
        sizeOfPopup = size;
        updateUI();
    }

    public void clearItems() {
        removeAllItems();
    }

    @Override
    public void setUI(ComboBoxUI ui) {
        if (ui != null) {
            // Let's try our own customized UI.
            Class c = ui.getClass();
            getClass();
            final String myClass = "com.gooddies.swing.hComboBox$My" + c.getSimpleName();

            try {
                Class clazz = Class.forName(myClass);
                ComboBoxUI myUI = (ComboBoxUI) clazz.getDeclaredConstructors()[0].newInstance(hComboBox.this);
                super.setUI(myUI);
                return;
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(hComboBox.class.getName()).log(Level.SEVERE, null, ex);
            } catch (Exception ex) {
                Logger.getLogger(hComboBox.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        // Either null, or we fail to use our own customized UI.
        // Fall back to default.
        super.setUI(ui);

    }

    private boolean isSelectingItem() {
        try {
            Field field = getClass().getSuperclass().getDeclaredField("selectingItem");
            field.setAccessible(true);
            return (boolean) field.get(this);
        } catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    private void setSelectingItem(boolean value) {
        try {
            Field field = getClass().getSuperclass().getDeclaredField("selectingItem");
            field.setAccessible(true);
            field.set(this, value);
        } catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void contentsChanged(ListDataEvent e) {
        Object oldSelection = selectedItemReminder;
        Object newSelection = dataModel.getSelectedItem();
        selectedItemChanged();
        if (!isSelectingItem()) {
            fireActionEvent();
        }
    }

    @Override
    public void setSelectedItem(Object anObject) {
        Object oldSelection = selectedItemReminder;
        Object objectToSelect = anObject;
        if (oldSelection == null || true) {

            if (anObject != null && !isEditable()) {
                // For non editable combo boxes, an invalid selection
                // will be rejected.
                boolean found = false;
                for (int i = 0; i < dataModel.getSize(); i++) {
                    T element = dataModel.getElementAt(i);
                    if (anObject.equals(element)) {
                        found = true;
                        objectToSelect = element;
                        break;
                    }
                }
                if (!found) {
                    return;
                }
            }

            // Must toggle the state of this flag since this method
            // call may result in ListDataEvents being fired.
            setSelectingItem(true);
            dataModel.setSelectedItem(objectToSelect);
            setSelectingItem(false);

            selectedItemChanged();
        }
        fireActionEvent();
    }

    @Override
    public Iterator<T> iterator() {
        return new Itr();
    }

    // This is a non-portable method to make combo box horizontal scroll bar.
    // Whenever there is a new look-n-feel, we need to manually provide the ComboBoxUI.
    // Any idea on how to make this portable?
    //
    protected class MyWindowsComboBoxUI extends com.sun.java.swing.plaf.windows.WindowsComboBoxUI {

        public MyWindowsComboBoxUI() {
        }

        @Override
        protected ComboPopup createPopup() {
            return new MyComboPopup(comboBox);
        }
    }

    protected class MyMotifComboBoxUI extends com.sun.java.swing.plaf.motif.MotifComboBoxUI {

        public MyMotifComboBoxUI() {
        }

        @Override
        protected ComboPopup createPopup() {
            return new MyComboPopup(comboBox);
        }
    }

    protected class MyMetalComboBoxUI extends javax.swing.plaf.metal.MetalComboBoxUI {

        public MyMetalComboBoxUI() {
        }

        @Override
        protected ComboPopup createPopup() {
            return new MyComboPopup(comboBox);
        }
    }

    private class MyComboPopup extends BasicComboPopup {

        public MyComboPopup(JComboBox combo) {
            super(combo);
            getList().addMouseListener(new MouseAdapter() {
                @Override
                public void mouseReleased(MouseEvent e) {
                    fireChanged();
                }
            });

        }

        @Override
        public Rectangle computePopupBounds(int px, int py, int pw, int ph) {

            int w = pw;
            if (sizeOfPopup != -1) {
                w = sizeOfPopup;
            }
            return super.computePopupBounds(px, py, w, ph);
        }
    }

    private class Itr implements Iterator<T> {

        int cursor;       // index of next element to return
        int lastRet = -1; // index of last element returned; -1 if no such

        @Override
        public boolean hasNext() {
            return cursor != getItemCount();
        }

        @SuppressWarnings("unchecked")
        @Override
        public T next() {
            int i = cursor;
            if (i >= getItemCount()) {
                throw new NoSuchElementException();
            }
            cursor = i + 1;
            return (T) getItemAt(lastRet = i);
        }

        @Override
        public void remove() {
            throw new IllegalStateException();
        }
    }

    public Iterable<T> iterable() {
        return IteratorToIterable.once(new Itr());
    }

    public List<T> getItemsAsList() {
        List<T> list = new ArrayList<>();
        for (int i = 0; i < getItemCount(); i++) {
            list.add(getItemAt(i));
        }
        return list;
    }
}
