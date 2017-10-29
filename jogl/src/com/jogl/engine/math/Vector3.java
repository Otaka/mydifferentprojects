package com.jogl.engine.math;

import java.util.Arrays;

/**
 * @author Dmitry
 */
public class Vector3 {

    private final float[] buffer;

    public Vector3() {
        buffer = new float[3];
    }

    public Vector3(float[] initial, boolean useBuffer) {
        if (useBuffer) {
            buffer = initial;
        } else {
            buffer = new float[4];
            buffer[0] = initial[0];
            buffer[1] = initial[1];
            buffer[2] = initial[2];
        }
    }

    public Vector3(float x, float y, float z) {
        buffer = new float[4];
        buffer[0] = x;
        buffer[1] = y;
        buffer[2] = z;
    }

    public float[] getBuffer() {
        return buffer;
    }

    public void loadFrom(float[] values) {
        buffer[0] = values[0];
        buffer[1] = values[1];
        buffer[2] = values[2];
    }

    public void loadFrom(float x, float y, float z) {
        buffer[0] = x;
        buffer[1] = y;
        buffer[2] = z;
    }

    public void setX(float value) {
        buffer[0] = value;
    }

    public void setY(float value) {
        buffer[1] = value;
    }

    public void setZ(float value) {
        buffer[2] = value;
    }

    public float getX() {
        return buffer[0];
    }

    public float getY() {
        return buffer[1];
    }

    public float getZ() {
        return buffer[2];
    }

    @Override
    public String toString() {
        return Arrays.toString(buffer);
    }

}
