package com.jogl.engine.material;

import com.jogamp.opengl.GL3;

/**
 * @author Dmitry
 */
public abstract class Material {

    public abstract int applyMaterial(GL3 gl);

    public abstract void deApplyMaterial(GL3 gl);

}
