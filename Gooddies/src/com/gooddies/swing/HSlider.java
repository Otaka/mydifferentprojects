package com.gooddies.swing;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JPanel;

/**
 * @author sad
 */
public class HSlider extends JPanel {

    public abstract static class ChangeEvent {

        public void changeValue(int value) {
        }

        public void changeValue(HSlider.SelectedMark mark, int value) {
        }
    }

    public enum SelectedMark {

        LeftBoundary, RightBoundary, Value, None
    };
    private int minimum = 0;
    private int maximum = 100;
    private int current = 50;
    private int minBoundary = 10;
    private int maxBoundary = 90;
    private final Color stripColor = Color.LIGHT_GRAY;
    private final Color inBoundColor = Color.WHITE;
    private final Color boundMark = Color.BLACK;
    private final Color currentValueMark = Color.RED;
    private double cachedProportion;
    private boolean boundaryMovingAllowed = true;
    private HSlider.SelectedMark selectedMark = HSlider.SelectedMark.None;
    private HSlider.ChangeEvent event = null;

    public void setOnChangeEvent(ChangeEvent event) {
        this.event = event;
    }

    public HSlider() {
        setOpaque(false);
        setPreferredSize(new Dimension(200, 20));
        addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                if (isEnabled()) {
                    mouseMove(e.getX(), e.getY());
                }
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                if (isEnabled()) {
                    if (e.getButton() == 0) {
                        mouseDrag(e.getX(), e.getY());
                    }
                }

            }
        });
    }

    public int getMinimum() {
        return minimum;
    }

    public void setMinimum(int minimum) {
        this.minimum = minimum;
        recalculateCachedProportion();
        repaint();
    }

    public int getMaximum() {
        return maximum;
    }

    public void setMaximum(int maximum) {
        this.maximum = maximum;
        recalculateCachedProportion();
        repaint();
    }

    public int getCurrent() {
        return current;
    }

    public void setCurrent(int current) {
        if(current<=maxBoundary){
        this.current = current;
        }
        repaint();
    }

    public int getMinBoundary() {
        return minBoundary;
    }

    public void setMinBoundary(int minBoundary) {
        this.minBoundary = minBoundary;
        repaint();
    }

    public int getMaxBoundary() {
        return maxBoundary;
    }

    public void setMaxBoundary(int maxBoundary) {
        this.maxBoundary = maxBoundary;
        repaint();
    }

    public boolean isBoundaryMovingAllowed() {
        return boundaryMovingAllowed;
    }

    public void setBoundaryMovingAllowed(boolean boundaryMovingAllowed) {
        this.boundaryMovingAllowed = boundaryMovingAllowed;
    }

    @Override
    public void setBounds(int x, int y, int width, int height) {
        super.setBounds(x, y, width, height);
        recalculateCachedProportion();
    }

    private void recalculateCachedProportion() {
        float range = maximum - minimum;
        float pixWidth = getWidth();
        cachedProportion = pixWidth / range;
    }

    private int valueToPixel(int value) {
        float val = value - minimum;
        return (int) (val * cachedProportion);
    }

    private int pixelToValue(int pixel) {
        double v = pixel / cachedProportion;
        return (int) v + minimum;
    }

    private void mouseDrag(int x, int y) {
        if (selectedMark == HSlider.SelectedMark.None) {
            return;
        }
        int value = pixelToValue(x);
        if (value < minimum) {
            value = minimum;
        }
        if (value > maximum) {
            value = maximum;
        }
        if (selectedMark == HSlider.SelectedMark.Value) {
            if (value < minBoundary) {
                value = minBoundary;
            }
            if (value > maxBoundary) {
                value = maxBoundary;
            }
        }

        if (selectedMark == HSlider.SelectedMark.LeftBoundary) {
            if (value > current) {
                value = current;
            }
        }

        if (selectedMark == HSlider.SelectedMark.RightBoundary) {
            if (value < current) {
                value = current;
            }
        }
        if (selectedMark == HSlider.SelectedMark.LeftBoundary) {
            setMinBoundary(value);
            if (event != null) {
                event.changeValue(HSlider.SelectedMark.LeftBoundary, value);
            }
        } else if (selectedMark == HSlider.SelectedMark.RightBoundary) {
            setMaxBoundary(value);
            if (event != null) {
                event.changeValue(HSlider.SelectedMark.RightBoundary, value);
            }
        } else if (selectedMark == HSlider.SelectedMark.Value) {
            setCurrent(value);
            if (event != null) {
                event.changeValue(HSlider.SelectedMark.Value, value);
                event.changeValue(value);
            }
        }
    }

    private void mouseMove(int x, int y) {
        if (mouseHitTestMark(current, x, y)) {
            selectedMark = HSlider.SelectedMark.Value;
        } else if (mouseHitTestMark(minBoundary, x, y)) {
            selectedMark = HSlider.SelectedMark.LeftBoundary;
        } else if (mouseHitTestMark(maxBoundary, x, y)) {
            selectedMark = HSlider.SelectedMark.RightBoundary;
        } else {
            selectedMark = HSlider.SelectedMark.None;
        }

        if (!boundaryMovingAllowed) {
            if (selectedMark == HSlider.SelectedMark.LeftBoundary || selectedMark == HSlider.SelectedMark.RightBoundary) {
                selectedMark = HSlider.SelectedMark.None;
            }
        }

        if (selectedMark == HSlider.SelectedMark.None) {
            setCursor(Cursor.getDefaultCursor());
        } else {
            setCursor(Cursor.getPredefinedCursor(Cursor.W_RESIZE_CURSOR));
        }
    }

    @Override
    public void paint(Graphics g) {
        boolean enabled = isEnabled();
        super.paint(g);
        g.setColor(enabled ? stripColor : Color.LIGHT_GRAY);
        g.fillRect(0, 2, getWidth(), 4);
        int boundStart = valueToPixel(minBoundary);
        int boundEnd = valueToPixel(maxBoundary);
        g.setColor(enabled ? inBoundColor : Color.GRAY);
        g.fillRect(boundStart, 2, boundEnd - boundStart, 4);
        if (boundaryMovingAllowed) {
            g.setColor(enabled ? boundMark : Color.LIGHT_GRAY);
            drawBoundMark(g, minBoundary);
            drawBoundMark(g, maxBoundary);
        }

        g.setColor(enabled ? currentValueMark : Color.LIGHT_GRAY);
        drawBoundMark(g, current);
    }

    private boolean mouseHitTestMark(int val, int x, int y) {
        int pixel = valueToPixel(val);
        int x1 = pixel - 2;
        int y1 = 0;
        int width = 4;
        int height = 7;
        return x >= x1 && y >= y1 && x <= x1 + width && y <= y1 + height;
    }

    private void drawBoundMark(Graphics g, int val) {
        int pixel = valueToPixel(val);
        g.drawRect(pixel - 1, 1, 2, 5);
        g.drawRect(pixel - 1, 0, 2, 7);
    }
}
