package com.drawables;

import com.jsonparser.JsonObject;
import java.awt.Color;
import java.awt.Graphics;

/**
 * @author Dmitry
 */
public class RectDrawable extends Drawable {
    private Color color = Color.WHITE;
    private float x = 0;
    private float y = 0;
    private float x2 = 1;
    private float y2 = 1;

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public float getX2() {
        return x2;
    }

    public void setX2(float x2) {
        this.x2 = x2;
    }

    public float getY2() {
        return y2;
    }

    public void setY2(float y2) {
        this.y2 = y2;
    }

    private Color parseColor(String color) {
        color = color.toLowerCase().trim();
        try {
            return Color.decode(color);
        } catch (NumberFormatException ex) {
            switch (color) {
                case "red":
                    return Color.RED;
                case "black":
                    return Color.BLACK;
                case "blue":
                    return Color.BLUE;
                case "cyan":
                    return Color.CYAN;
                case "dark_gray":
                    return Color.DARK_GRAY;
                case "gray":
                case "grey":
                    return Color.GRAY;
                case "green":
                    return Color.GREEN;
                case "magenta":
                    return Color.MAGENTA;
                case "orange":
                    return Color.ORANGE;
                case "pink":
                    return Color.PINK;
                case "white":
                    return Color.WHITE;
                case "yellow":
                    return Color.YELLOW;
            }
        }

        throw new IllegalArgumentException("Cannot parse string '" + color + "' as color");
    }

    private void parsePosition(String positionString) {
        String[] values = positionString.split("\\s+");
        if (values.length != 4) {
            throw new IllegalArgumentException("Position of the 'color' should contain 4 numbers separated by space. I.E \"0 0 1 1\"");
        }
        try {
            x = Float.parseFloat(values[0]);
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("String '" + values[0] + "' should be valid float value between 0 and 1");
        }
        try {
            y = Float.parseFloat(values[1]);
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("String '" + values[1] + "' should be valid float value between 0 and 1");
        }
        try {
            x2 = Float.parseFloat(values[2]);
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("String '" + values[2] + "' should be valid float value between 0 and 1");
        }
        try {
            y2 = Float.parseFloat(values[3]);
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("String '" + values[3] + "' should be valid float value between 0 and 1");
        }
    }

    @Override
    public Drawable parse(JsonObject element) {
        super.parse(element);
        if (element.has("color")) {
            color = parseColor(element.getElementByName("color").getAsString());
        }

        if (element.has("position")) {
            parsePosition(element.getElementByName("position").getAsString());
        }
        return this;
    }

    @Override
    public void paint(Graphics gr, int containerWidth, int containerHeight) {
        super.paint(gr, containerWidth, containerHeight);
        gr.setColor(color);
        int xPos = (int) (x * containerWidth);
        int yPos = (int) (y * containerHeight);
        int x2Pos = (int) (x2 * containerWidth);
        int y2Pos = (int) (y2 * containerHeight);
        gr.fillRect(xPos, yPos, x2Pos - xPos, y2Pos - yPos);
    }

}
