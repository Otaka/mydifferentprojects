package com.gui.instruments;

import com.gui.Node;
import com.gui.RenderPane;
import java.awt.Point;

/**
 * @author Dmitry
 */
public class MoveInstrument extends AbstrInstrument {
    private final RenderPane renderPane;
    private boolean mouseDown = false;
    private Point dMovePoint;

    public MoveInstrument(RenderPane renderPane) {
        this.renderPane = renderPane;
    }

    @Override
    public void mouseMove(int x, int y) {
        if (mouseDown) {
            int tx = (int) (x - dMovePoint.getX());
            int ty = (int) (y - dMovePoint.getY());
            if (tx < 0) {
                tx = 0;
            }
            if (ty < 0) {
                ty = 0;
            }
            renderPane.getSelectedNode().setX(tx);
            renderPane.getSelectedNode().setY(ty);
            renderPane.repaint();
        }
    }

    @Override
    public void mouseDown(int x, int y, int button) {
        Node selectedNode = renderPane.getNodeByXY(x, y);
        if (selectedNode != null) {
            dMovePoint = new Point(x - selectedNode.getX(), y - selectedNode.getY());
            mouseDown = true;
        }
        renderPane.setSelectedNode(selectedNode);
        renderPane.repaint();
    }

    @Override
    public void mouseUp(int x, int y, int button) {
        mouseDown = false;
        dMovePoint = null;
        renderPane.repaint();
    }

}
