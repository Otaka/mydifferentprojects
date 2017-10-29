package com.jogl.engine.node.geometry;

import static com.jogamp.opengl.GL.*;
import com.jogamp.opengl.GL3;
import com.jogl.engine.material.Material;
import com.jogl.engine.math.Matrix;
import com.jogl.engine.texture.Texture;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Dmitry
 */
public abstract class Geometry {
    private Material material;
    private final List<Texture> textures = new ArrayList<>();

    public Geometry() {
    }

    public void addTexture(Texture texture) {
        textures.add(texture);
    }

    public void addTexture(Texture texture, int index) {
        textures.add(index, texture);
    }

    public void removeTexture(Texture texture) {
        textures.remove(texture);
    }

    public void setMaterial(Material material) {
        this.material = material;
        materialChanged();
    }

    public Material getMaterial() {
        return material;
    }

    public void render(GL3 gl, Matrix pvMatrix, Matrix modelm, Matrix pvmMatrix) {
        int programId = material.applyMaterial(gl);
        gl.glEnable(GL_TEXTURE_2D);
        for (int i = 0; i < textures.size(); i++) {
            Texture texture = textures.get(i);
            gl.glActiveTexture(GL_TEXTURE0 + i);
            gl.glBindTexture(GL_TEXTURE_2D, texture.getTextureId());
            gl.glUniform1i(texture.getTextureId(), 0);
        }

        renderGeometry(gl, pvMatrix, modelm, pvmMatrix, programId);
        gl.glDisable(GL_TEXTURE_2D);
        if (material != null) {
            material.deApplyMaterial(gl);
        }
    }

    public void materialChanged() {

    }

    public abstract void renderGeometry(GL3 gl, Matrix pvMatrix, Matrix modelm, Matrix pvmMatrix, int programId);
}
