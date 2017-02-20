package com.drawables;

import java.awt.Graphics;
import java.util.List;

/**
 * @author Dmitry
 */
public class CollectionOfDrawables extends Drawable {
    List<Drawable> drawables;

    public CollectionOfDrawables(List<Drawable> drawables) {
        this.drawables = drawables;
    }

    @Override
    public void paint(Graphics gr, int containerWidth, int containerHeight) {
        for (Drawable dr : drawables) {
            dr.paint(gr, containerWidth, containerHeight);
        }
    }
}
