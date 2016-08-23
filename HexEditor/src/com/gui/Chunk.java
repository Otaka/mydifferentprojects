package com.gui;

import java.awt.Color;

/**
 * @author Dmitry
 */
public class Chunk {
    private final int offset;
    private String label;
    private String unpackPattern;
    private final int size;
    private final int type;
    private final int labelIndex;
    private Color border;
    private int borderoffset = 0;

    public Chunk(int offset, String label, int size, int type, int labelIndex) {
        this.offset = offset;
        this.label = label;
        this.size = size;
        this.type = type;
        this.labelIndex = labelIndex;
    }

    public Color getOverlappedBorderColor() {
        return border;
    }

    public void setOverlappedBorderColor(Color border) {
        this.border = border;
    }

    public int getBorderOffset() {
        return borderoffset;
    }

    public void setBorderOffset(int borderoffset) {
        this.borderoffset = borderoffset;
    }

    public void setUnpackPattern(String unpackPattern) {
        this.unpackPattern = unpackPattern;
    }

    public String getUnpackPattern() {
        return unpackPattern;
    }

    public int getLabelIndex() {
        return labelIndex;
    }

    public void setStringLabel(String label) {
        this.label = label;
    }

    public int getOffset() {
        return offset;
    }

    public String getLabel() {
        return label;
    }

    public int getSize() {
        return size;
    }

    public int getType() {
        return type;
    }

    @Override
    public String toString() {
        return "Offset:" + offset + " Size:" + size + " Label:\"" + label + "\"";
    }

}
