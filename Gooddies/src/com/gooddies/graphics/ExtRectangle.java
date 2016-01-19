package com.gooddies.graphics;

import java.awt.Rectangle;

public class ExtRectangle extends Rectangle {

    public ExtRectangle() {
        super(0, 0, 0, 0);
    }

    public ExtRectangle(int x, int y, int w, int h) {
        super(x, y, w, h);
    }

    public ExtRectangle(Rectangle r) {
        super(r.x, r.y, (int) r.getWidth(), (int) r.getHeight());
    }

    public void fitProportional(Rectangle rect) {
        Rectangle r = new Rectangle(rect);
        double w = getWidth();
        double h = getHeight();
        double cw = r.getWidth();
        double ch = r.getHeight();

        double xyaspect = w / h;
        if (w > h) {
            w = cw;
            h = cw / xyaspect;
            if (h > ch) {
                h = ch;
                w = ch * xyaspect;
            }
        } else {
            h = ch;
            w = ch * xyaspect;
            if (w > cw) {
                w = cw;
                h = cw / xyaspect;
            }
        }
        setRect(getX(), getY(), w, h);
    }
}
