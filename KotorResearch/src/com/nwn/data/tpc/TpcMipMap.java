package com.nwn.data.tpc;

/**
 * @author Dmitry
 */
public class TpcMipMap {
    private int width;
    private int height;
    private int size;
    private byte[] data;

    public void setData(byte[] data) {
        this.data = data;
    }

    public byte[] getData() {
        return data;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public int getSize() {
        return size;
    }

    public int getWidth() {
        return width;
    }

}
