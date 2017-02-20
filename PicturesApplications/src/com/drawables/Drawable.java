package com.drawables;

import com.jsonparser.JsonObject;
import java.awt.Graphics;

/**
 * @author Dmitry
 */
public abstract class Drawable {
    public Drawable parse(JsonObject element) {
        return this;
    }

    public void paint(Graphics gr, int containerWidth, int containerHeight) {

    }
}
