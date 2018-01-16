package com.settings.editor.builder;

import java.awt.Dimension;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JComponent;
import javax.swing.JDialog;

/**
 * @author sad
 */
public class SettingsDialogBuilder {

    private Object property;
    private Dimension preferredSize;
    private Dimension minSize;
    private Dimension maxSize;
    private String caption;
    private boolean resizable = false;
    private final Map<String, CategoryProperties> categoryProperties = new HashMap<>();

    public String getCaption() {
        return caption;
    }

    public boolean isResizable() {
        return resizable;
    }

    public void setCategoryProperties(CategoryProperties properties) {
        setCategoryProperties("def", properties);
    }

    public SettingsDialogBuilder setCategoryProperties(String category, CategoryProperties properties) {
        if (categoryProperties.containsKey(category)) {
            throw new IllegalArgumentException("Category \"" + category + "\" is already set");
        }
        categoryProperties.put(category, properties);
        return this;
    }

    public SettingsDialogBuilder setResizable(boolean resizable) {
        this.resizable = resizable;
        return this;
    }

    public SettingsDialogBuilder setCaption(String caption) {
        this.caption = caption;
        return this;
    }

    public SettingsDialogBuilder setProperty(Object property) {
        this.property = property;
        return this;
    }

    public SettingsDialogBuilder setMaxSize(Dimension maxSize) {
        this.maxSize = maxSize;
        return this;
    }

    public SettingsDialogBuilder setMinSize(Dimension minSize) {
        this.minSize = minSize;
        return this;
    }

    public SettingsDialogBuilder setPreferredSize(Dimension preferredSize) {
        this.preferredSize = preferredSize;
        return this;
    }

    public Object getProperty() {
        return property;
    }

    public Dimension getPreferredSize() {
        return preferredSize;
    }

    public Dimension getMinSize() {
        return minSize;
    }

    public Dimension getMaxSize() {
        return maxSize;
    }

    public JDialog show() {
        SettingsPanelBuilder panelBuilder = new SettingsPanelBuilder();
        panelBuilder.setCategoryProperties(categoryProperties);
        JComponent panel = panelBuilder.build(property);
        if (minSize != null) {
            panel.setMinimumSize(minSize);
        }

        if (maxSize != null) {
            panel.setMaximumSize(maxSize);
        }

        if (preferredSize != null) {
            panel.setPreferredSize(preferredSize);
        }

        JDialog dialog = new JDialog();
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.setTitle(caption == null ? "Settings" : caption);
        dialog.setResizable(resizable);
        dialog.getContentPane().add(panel);
        dialog.pack();
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
        return dialog;
    }
}
