package com.gui.instruments;

import com.gui.RenderPane;
import com.n.DetectionN;
import java.awt.Color;

public class NewNodeInstrument extends AbstrInstrument {
    private static int index = 1;
    private final RenderPane renderPane;

    public NewNodeInstrument(RenderPane renderPane) {
        this.renderPane = renderPane;
    }

    @Override
    public void mouseDown(int x, int y, int button) {
        DetectionN newNode = new DetectionN();
        newNode.setX(x);
        newNode.setY(y);
        newNode.setId((int) (Math.random() * 999999999999999999l));
        newNode.setColor(Color.BLACK);
        newNode.setTitle("Н " + index);
        index++;
        renderPane.getNodes().add(newNode);
        renderPane.repaint();
    }
}
