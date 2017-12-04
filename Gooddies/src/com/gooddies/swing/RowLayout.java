package com.gooddies.swing;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.LayoutManager;
import java.awt.LayoutManager2;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Dmitry
 */
public class RowLayout implements LayoutManager, LayoutManager2 {
    public static enum RowHAlignment {
        LEFT, CENTER, RIGHT
    };
    private List<List<Component>> rows = new ArrayList<List<Component>>();
    private int startXGap = 5;
    private int startYGap = 5;
    private int betweenXGap = 5;
    private int betweenYGap = 5;
    private int endXGap = 5;
    private int endYGap = 5;
    private List<Component> currentRow;
    private RowHAlignment rowHAlignment = RowHAlignment.LEFT;

    public RowLayout setStartXGap(int startXGap) {
        this.startXGap = startXGap;
        return this;
    }

    public RowLayout setStartYGap(int startYGap) {
        this.startYGap = startYGap;
        return this;
    }

    public RowLayout setBetweenXGap(int betweenXGap) {
        this.betweenXGap = betweenXGap;
        return this;
    }

    public RowLayout setBetweenYGap(int betweenYGap) {
        this.betweenYGap = betweenYGap;
        return this;
    }

    public RowLayout setEndXGap(int endXGap) {
        this.endXGap = endXGap;
        return this;
    }

    public RowLayout setEndYGap(int endYGap) {
        this.endYGap = endYGap;
        return this;
    }

    protected static class newRow {
    }
    public static newRow NewRow = new newRow();

    public RowLayout() {
        newRow();
    }

    private void newRow() {
        currentRow = new ArrayList<Component>();
        rows.add(currentRow);
    }

    public RowLayout setRowHAlignment(RowHAlignment rowHAlignment) {
        this.rowHAlignment = rowHAlignment;
        return this;
    }

    @Override
    public void addLayoutComponent(String name, Component comp) {
        addComponent(comp);
    }

    private void addComponent(Component comp) {
        currentRow.add(comp);
    }

    @Override
    public void addLayoutComponent(Component comp, Object constraints) {
        currentRow.add(comp);
        if (constraints instanceof newRow) {
            newRow();
        }
    }

    @Override
    public void removeLayoutComponent(Component comp) {
        for (int j = 0; j < rows.size(); j++) {
            List<Component> row = rows.get(j);
            for (int i = 0; i < row.size(); i++) {
                Component c = row.get(i);
                if (c == comp) {
                    row.remove(c);
                    if (row.isEmpty()) {
                        rows.remove(j);
                    }
                    return;
                }
            }
        }
    }

    public Dimension computeSize(Container parent) {
        double maxX = 0;
        double maxY = 0;

        Dimension tDim = new Dimension();

        for (int j = 0; j < rows.size(); j++) {

            if (j != 0) {
                maxY += betweenYGap;
            }
            List<Component> row = rows.get(j);
            calculateRow(row, tDim);
            double rowWidth = tDim.getWidth();
            double rowMaxHeight = tDim.getHeight();

            if (rowWidth > maxX) {
                maxX = rowWidth;
            }


            maxY += rowMaxHeight;
        }

        int width = (int) (maxX + startXGap + endXGap);
        int height = (int) (maxY + startYGap + endYGap);
        return new Dimension(width, height);
    }

    private void calculateRow(List<Component> row, Dimension dim) {
        double rowHeight = 0;
        double rowWidth = 0;
        for (int i = 0; i < row.size(); i++) {
            if (i != 0) {
                rowWidth += betweenXGap;
            }
            Component c = row.get(i);
            if (!c.isVisible()) {
                continue;
            }

            Dimension size = c.getPreferredSize();
            if (size.getHeight() > rowHeight) {
                rowHeight = size.getHeight();
            }


            rowWidth += size.getWidth();
        }

        dim.setSize(rowWidth, rowHeight);
    }

    @Override
    public void layoutContainer(Container parent) {
        double currentX = startXGap;
        double currentY = startYGap;

        Dimension tDim = new Dimension();
        for (int j = 0; j < rows.size(); j++) {
            if (j != 0) {
                currentY += betweenYGap;
            }
            List<Component> row = rows.get(j);
            calculateRow(row, tDim);
            layoutRow((int) currentX, (int) currentY, tDim, row, parent.getSize());


            currentY += tDim.getHeight();
        }
    }

    private void layoutRow(int startOffsetX, int startOffsetY, Dimension rowSize, List<Component> row, Dimension parentSize) {
        int x = startOffsetX;
        if (rowHAlignment == RowHAlignment.CENTER) {
            x = (int) (parentSize.getWidth() / 2 - rowSize.getWidth() / 2);
        } else if (rowHAlignment == RowHAlignment.RIGHT) {
            x = (int) (parentSize.getWidth() - rowSize.getWidth() - endXGap);
        }


        boolean first = true;
        for (int i = 0; i < row.size(); i++) {
            Component c = row.get(i);
            if (!c.isVisible()) {
                continue;
            }
            if (first == false) {
                x += betweenXGap;
            }

            Dimension size = c.getPreferredSize();
            int width = size.width;
            int height = size.height;
            int y = startOffsetY + (rowSize.height / 2 - height / 2);
            c.setBounds(x, y, width, height);
            x += width;
            first = false;
        }
    }

    @Override
    public Dimension maximumLayoutSize(Container parent) {
        return computeSize(parent);
    }

    @Override
    public Dimension preferredLayoutSize(Container parent) {
        return computeSize(parent);
    }

    @Override
    public Dimension minimumLayoutSize(Container parent) {
        return computeSize(parent);
    }

    @Override
    public float getLayoutAlignmentX(Container target) {
        return 0;
    }

    @Override
    public float getLayoutAlignmentY(Container target) {
        return 0;
    }

    @Override
    public void invalidateLayout(Container target) {
    }
}
