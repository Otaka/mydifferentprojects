package com.gooddies.events;

import java.util.ArrayList;
import java.util.List;
import javax.swing.JComponent;

/**
 * @author Dmitry
 */
public abstract class ValueChangedEvent<T> {

    private static List<ValueChangedBefore> beforeChangingEvents = new ArrayList<ValueChangedBefore>();

    protected abstract void valueChanged(T value);

    public void fire(T value, JComponent source) {
        beforeEvent(value, source);
        valueChanged(value);
    }

    public void fire(T value, JComponent source, boolean fireBeforeEvent) {
        if (fireBeforeEvent) {
            beforeEvent(value, source);
        }
        valueChanged(value);
    }

    public static void addBeforeEventListener(ValueChangedBefore event) {
        beforeChangingEvents.add(event);
    }

    private void beforeEvent(T value, JComponent source) {
        for (ValueChangedBefore event : beforeChangingEvents) {
            event.fire(source, value);
        }
    }

    public interface ValueChangedBefore {

        public void fire(JComponent component, Object value);
    }
}
