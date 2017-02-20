package com.gui.instruments;

import com.gui.Node;
import com.gui.RenderPane;

/**
 * @author Dmitry
 */
public class ConnectInstrument extends AbstrInstrument {
    private final RenderPane renderPane;

    public ConnectInstrument(RenderPane renderPane) {
        this.renderPane = renderPane;
    }

    @Override
    public void mouseDown(int x, int y, int button) {
        if (renderPane.getSelectedNode() == null) {
            Node selectedNode = renderPane.getNodeByXY(x, y);
            renderPane.setSelectedNode(selectedNode);
        } else {
            Node selectedNode = renderPane.getNodeByXY(x, y);
            if (selectedNode != null) {
                if (renderPane.getSelectedNode().isConnected(selectedNode)) {
                    renderPane.getSelectedNode().removeConnection(selectedNode);
                } else {
                    if (selectedNode != renderPane.getSelectedNode()) {
                        renderPane.getSelectedNode().addConnectToNode(selectedNode);
                    }
                }
            } else {
                renderPane.setSelectedNode(null);
            }
        }
        renderPane.repaint();
    }
}
