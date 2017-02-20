package com.gui.instruments;

import com.gui.Node;
import com.gui.RenderPane;
import com.n.DetectionN;

public class ActivationInstrument extends AbstrInstrument {
    private final RenderPane renderPane;

    public ActivationInstrument(RenderPane renderPane) {
        this.renderPane = renderPane;
    }

    @Override
    public void mouseDown(int x, int y, int button) {
        Node node = renderPane.getNodeByXY(x, y);
        if (node != null && node instanceof DetectionN) {
            DetectionN dn = (DetectionN) node;
            dn.setActive(!dn.isActive());
            renderPane.repaint();
        }
    }
}
