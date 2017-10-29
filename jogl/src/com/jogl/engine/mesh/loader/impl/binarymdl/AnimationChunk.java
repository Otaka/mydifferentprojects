package com.jogl.engine.mesh.loader.impl.binarymdl;

import com.jogamp.opengl.math.Quaternion;

/**
 * @author Dmitry
 */
public class AnimationChunk {
    private float time;
    private float[] position;
    private Quaternion rotation;

    public AnimationChunk() {
    }

    public float getTime() {
        return time;
    }

    public void setTime(float time) {
        this.time = time;
    }

    public float[] getPosition() {
        return position;
    }

    public void setPosition(float[] position) {
        this.position = position;
    }

    public Quaternion getRotation() {
        return rotation;
    }

    public void setRotation(Quaternion rotation) {
        this.rotation = rotation;
    }

}
