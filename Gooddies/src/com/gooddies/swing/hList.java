package com.gooddies.swing;

import com.gooddies.events.ValueChangedEvent;
import com.gooddies.utils.IteratorToIterable;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelListener;
import java.util.Iterator;
import java.util.List;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JToolTip;
import javax.swing.ListCellRenderer;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;

/**
 * @author sad
 */
public class hList<T> extends JScrollPane {

    private ValueChangedEvent<T> valueChanged;
    private final JList<T> list;
    private final DefaultListModel<T> model;

    public hList() {
        setPreferredSize(new Dimension(100, 100));
        list = new JList<T>() {

            @Override
            public JToolTip createToolTip() {
                return hList.this.createToolTip();
            }

            @Override
            public String getToolTipText(MouseEvent event) {
                return hList.this.getToolTipText(event);
            }

            @Override
            public String getToolTipText() {
                return hList.this.getToolTipText();
            }

        };
        list.setToolTipText(",jmn");
        setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        getViewport().add(list);
        model = new DefaultListModel<T>();
        list.setModel(model);
        list.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                fireChanged();
            }
        });

        list.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_DOWN || e.getKeyCode() == KeyEvent.VK_UP) {
                    fireChanged();
                }
            }
        });
    }
@SuppressWarnings(value = "unchecked")
    public Iterable<T> iterable() {
        Iterator iterator = new Iterator() {
            private int currentIndex = 0;

            @Override
            public boolean hasNext() {
                return currentIndex < model.size();
            }

            @Override
            public Object next() {
                return getItem(currentIndex++);
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };

        return IteratorToIterable.once(iterator);
    }

    @Override
    public synchronized void addMouseListener(MouseListener l) {
        if (list != null) {
            list.addMouseListener(l);
        }
    }

    @Override
    public synchronized void addMouseMotionListener(MouseMotionListener l) {
        if (list != null) {
            list.addMouseMotionListener(l);
        }
    }

    @Override
    public synchronized void addMouseWheelListener(MouseWheelListener l) {
        if (list != null) {
            list.addMouseWheelListener(l);
        }
    }

    public int locationToIndex(Point location) {
        return list.locationToIndex(location);
    }

    public void setCellRenderer(ListCellRenderer<? super T> cellRenderer) {
        list.setCellRenderer(cellRenderer);
    }

    public interface ItemTextExtractor<T> {

        public String getText(T text, int index);
    }

    @SuppressWarnings(value = "unchecked")
    public void setItemTextExtractor(final ItemTextExtractor<T> extractor) {
        list.setCellRenderer(new DefaultListCellRenderer() {

            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                T obj = (T) value;
                String text = extractor.getText(obj, index);
                Component component = super.getListCellRendererComponent(list, text, index, isSelected, cellHasFocus);
                return component;
            }

        });

    }

    public int getVisibleRowCount() {
        return list.getVisibleRowCount();
    }

    public Point indexToLocation(int index) {
        return list.indexToLocation(index);
    }

    @Override
    public synchronized void addKeyListener(KeyListener l) {
        list.addKeyListener(l);
    }

    public void removeObject(int index) {
        model.remove(index);
    }

    public void setValueChangedEvent(ValueChangedEvent<T> valueChanged) {
        this.valueChanged = valueChanged;
    }

    private void fireChanged() {
        if (valueChanged != null) {
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    T object = getSelectedItem();
                    if (object != null) {
                        valueChanged.fire(object, hList.this);
                    }
                }
            });
        }
    }

    public boolean isEmpty() {
        return model.getSize() != 0;
    }

    public int count() {
        return model.getSize();
    }

    public void clearItems() {
        model.clear();
    }

    @SafeVarargs
    public final void addItems(T... items) {
        for (T item : items) {
            model.addElement(item);
        }
    }

    public final void addItems(List<T> items) {
        for (T item : items) {
            model.addElement(item);
        }
    }

    public void addItem(T item, int index) {
        model.add(index, item);
    }

    public void addItem(T item) {
        model.addElement(item);
    }

    public int getItemCount() {
        return model.size();
    }

    public T getItem(int index) {
        return (T) model.get(index);
    }

    public int getSelectedIndex() {
        return list.getSelectedIndex();
    }

    public T getSelectedItem() {
        return (T) list.getSelectedValue();
    }

    public JList getJList() {
        return list;
    }
}
