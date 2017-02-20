package com.n;

import com.gui.Node;
import com.settings.editor.components.annotations.PropertyBoolean;
import gnu.trove.TIntHashSet;
import java.awt.Color;
import java.awt.Graphics;

public class DetectionN extends Node {

    @PropertyBoolean(name = "Активность")
    private boolean active = false;
    private final TIntHashSet connectedIds = new TIntHashSet();
    private int id;

    public TIntHashSet getConnectedIds() {
        return connectedIds;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    @Override
    public void paint(Graphics g, boolean isSelected) {
        super.paint(g, isSelected);
        if (isActive()) {
            Color oldColor = g.getColor();
            g.setXORMode(Color.BLUE);
            g.setColor(Color.WHITE);
            g.fillOval(getX() - getRadius() / 2, getY() - getRadius() / 2, getRadius(), getRadius());
            g.setPaintMode();
            g.setColor(oldColor);
        }
    }

}
