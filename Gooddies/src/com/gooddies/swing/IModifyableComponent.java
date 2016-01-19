package com.gooddies.swing;

import com.gooddies.events.ValueChangedEvent;

public interface IModifyableComponent {
    public void clearModified();
    public void setModified(boolean modified);
    public boolean isModified();
}
