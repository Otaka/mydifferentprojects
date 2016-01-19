package com.gooddies.swing;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.border.BevelBorder;

@SuppressWarnings("unchecked")
public class HToggleButton extends JPanel {
    private String action;
    protected static List<WeakReference<HToggleButton>> listOfToggleButtons = new ArrayList<WeakReference<HToggleButton>>();
    private ActionListener onSelect;
    private ActionListener onDeselect;
    private boolean selected = false;
    private Color lastBackColor;
    private boolean execListeners = false;
    private BufferedImage icon;
    private int group;
    private boolean clickToDeselect = false;

    public void setClickToDeselect(boolean enable) {
        clickToDeselect = enable;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public int getToggleGroup() {
        return group;
    }

    public HToggleButton setToggleGroup(int group) {
        this.group = group;
        return this;
    }

    public HToggleButton setOnSelect(ActionListener onSelect) {
        this.onSelect = onSelect;
        return this;
    }

    public boolean isSelected() {
        return selected;
    }

    public HToggleButton setOnDeselect(ActionListener onDeselect) {
        this.onDeselect = onDeselect;
        return this;
    }

    public HToggleButton(BufferedImage icon) {
        this.icon = icon;
        setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
        listOfToggleButtons.add(new WeakReference(this));
        setPreferredSize(new Dimension(icon.getWidth() + 4, icon.getHeight() + 4));
        initListeners();
    }

    public void select() {
        selectWithoutAction();
        if (onSelect != null) {
            onSelect.actionPerformed(null);
        }
    }

    public void selectWithoutAction() {
        deselectAllInToggleGroup();
        execListeners = false;
        lastBackColor = getBackground();
        setBackground(Color.YELLOW);
        selected = true;
        execListeners = true;
        setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
    }

    public void deselectAllInToggleGroup() {
        int toggleGroup = getToggleGroup();
        for (WeakReference<HToggleButton> reference : listOfToggleButtons) {
            if (!reference.isEnqueued() && reference.get() != null) {
                HToggleButton button = reference.get();
                if (button != this) {
                    if (button.getToggleGroup() == toggleGroup) {
                        button.deselect();
                    }
                }
            }
        }
    }

    public void deselect() {
        if (isSelected()) {
            selected = false;
            setBackground(lastBackColor);
            setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
            if (onDeselect != null) {
                onDeselect.actionPerformed(null);
            }
        }
    }

    private void initListeners() {
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (!clickToDeselect) {
                    if (!isSelected()) {
                        select();
                    }
                }else{
                    if (!isSelected()) {
                        select();
                    }else{
                        deselect();
                    }
                }
            }
        });
    }

    @Override
    public void setBackground(Color bg) {
        if (execListeners) {
            lastBackColor = bg;
        }
        super.setBackground(bg);
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        int x = (int) (getWidth() / 2.0 - icon.getWidth(this) / 2.0);
        int y = (int) (getHeight() / 2.0 - icon.getHeight(this) / 2.0);
        if (isSelected()) {
            x += 1;
            y += 1;
        }
        g.drawImage(icon, x, y, this);
    }
}