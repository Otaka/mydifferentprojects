package com.jogl.engine.math;

import com.jogamp.opengl.math.Quaternion;
import java.util.Arrays;

/**
 * @author Dmitry
 */
public class Vector4 {

    private final float[] buffer;
    private static ThreadLocal<float[]> tempBuffer = new ThreadLocal<>();

    public Vector4() {
        buffer = new float[4];
    }
    
    public Vector4(float x, float y, float z, float w) {
        buffer = new float[4];
        buffer[0] = x;
        buffer[1] = y;
        buffer[2] = z;
        buffer[3] = w;
    }

    public Vector4(float x, float y, float z) {
        buffer = new float[4];
        buffer[0] = x;
        buffer[1] = y;
        buffer[2] = z;
        buffer[3] = 1;
    }


    public float[] getTempBufer() {
        float[] tb = tempBuffer.get();
        if (tb == null) {
            tb = new float[4];
            tempBuffer.set(tb);
        }

        return tb;
    }

    public Vector4(float[] initial, boolean useBuffer) {
        if (useBuffer) {
            buffer = initial;
        } else {
            buffer = new float[4];
            buffer[0] = initial[0];
            buffer[1] = initial[1];
            buffer[2] = initial[2];
            buffer[3] = initial[3];
        }
    }

    public void rotateByQuaternion(Quaternion q) {
        float[] tb = getTempBufer();
        q.rotateVector(tb, 0, buffer, 0);
        buffer[0] = tb[0];
        buffer[1] = tb[1];
        buffer[2] = tb[2];
        buffer[3] = tb[3];

    }

    public void sum(Vector4 vector) {
        buffer[0] += vector.buffer[0];
        buffer[1] += vector.buffer[1];
        buffer[2] += vector.buffer[2];
        buffer[3] += vector.buffer[3];
    }
    
    public void sum(float x, float y, float z, float w) {
        buffer[0] += x;
        buffer[1] += y;
        buffer[2] += z;
        buffer[3] += w;
    }

    public void mul(Vector4 vector) {
        buffer[0] *= vector.buffer[0];
        buffer[1] *= vector.buffer[1];
        buffer[2] *= vector.buffer[2];
        buffer[3] *= vector.buffer[3];
    }

    public void div(Vector4 vector) {
        buffer[0] /= vector.buffer[0];
        buffer[1] /= vector.buffer[1];
        buffer[2] /= vector.buffer[2];
        buffer[3] /= vector.buffer[3];
    }

    public void set(float x, float y, float z) {
        buffer[0] = x;
        buffer[1] = y;
        buffer[2] = z;
    }

    public void set(float x, float y, float z, float w) {
        buffer[0] = x;
        buffer[1] = y;
        buffer[2] = z;
        buffer[3] = w;
    }

    public void set(Vector4 v) {
        buffer[0] = v.buffer[0];
        buffer[1] = v.buffer[1];
        buffer[2] = v.buffer[2];
        buffer[3] = v.buffer[3];
    }

    
    public float[] getBuffer() {
        return buffer;
    }

    public void loadFrom(float[] values) {
        buffer[0] = values[0];
        buffer[1] = values[1];
        buffer[2] = values[2];
        buffer[3] = values[3];
    }

    /**
     * load as quaternion
     */
    public void loadFrom(float x, float y, float z, float w) {
        buffer[0] = x;
        buffer[1] = y;
        buffer[2] = z;
        buffer[3] = w;
    }

    public void loadFrom(float x, float y, float z) {
        buffer[0] = x;
        buffer[1] = y;
        buffer[2] = z;
        buffer[3] = 1;
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

    public void setW(float value) {
        buffer[3] = value;
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

    public float getW() {
        return buffer[3];
    }

    @Override
    public String toString() {
        return Arrays.toString(buffer);
    }

}
