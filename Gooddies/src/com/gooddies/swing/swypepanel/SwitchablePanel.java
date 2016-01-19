package com.gooddies.swing.swypepanel;

import javax.swing.JPanel;

/**
 * @author sad
 */
public class SwitchablePanel extends JPanel {

    private String title;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        firePropertyChange("panelTitle", this.title, title);
        this.title = title;
    }

    public void onShow(boolean isBack) {

    }
}
