package com.gooddies.swing.event.command;

/**
 * @author sad
 */
public abstract class BaseController<T> {

    private T view;

    public T getView() {
        return view;
    }

    public void setView(T view) {
        this.view = (T) view;
        SwingEventCommandBinder.processAnnotations(view, this);
    }
}
