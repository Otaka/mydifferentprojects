package com.jogl.engine.material;

import com.jogamp.opengl.GL3;
import com.jogl.Utils;
import com.jogl.engine.SceneManager;
import java.io.IOException;

/**
 * @author Dmitry
 */
public class ShaderMaterial extends Material {
    private final int shaderProgram;

    public ShaderMaterial(SceneManager sceneManager, String vertexShader, String fragmentShader) throws IOException {
        shaderProgram = Utils.loadShader(sceneManager.getGl(), vertexShader, fragmentShader);
    }

    @Override
    public int applyMaterial(GL3 gl) {
        gl.glUseProgram(shaderProgram);
        return shaderProgram;
    }

    @Override
    public void deApplyMaterial(GL3 gl) {
        
    }
}
