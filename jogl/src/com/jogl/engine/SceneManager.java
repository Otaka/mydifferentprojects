package com.jogl.engine;

import com.jogamp.common.nio.Buffers;
import static com.jogamp.opengl.GL.*;
import com.jogamp.opengl.GL3;
import com.jogl.engine.exceptions.JoglException;
import com.jogl.engine.math.Matrix;
import com.jogl.engine.node.Camera;
import com.jogl.engine.node.Node;
import com.jogl.engine.texture.TextureManager;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Dmitry
 */
public class SceneManager {
    private final GL3 gl;
    private final List<Node> nodes;
    private Camera activeCamera;
    private final TextureManager textureManager;
    private final IntBuffer triangleVertexArrayID = Buffers.newDirectIntBuffer(1);

    public SceneManager(GL3 gl) {
        this.gl = gl;
        createVao();
        nodes = new ArrayList<>();
        textureManager = new TextureManager(this);
        gl.glEnable(GL_DEPTH_TEST);
        gl.glDepthFunc(GL_LESS);
        gl.glEnable(GL_CULL_FACE);
    }

    private void createVao() {
        gl.glGenVertexArrays(1, triangleVertexArrayID);
        gl.glBindVertexArray(triangleVertexArrayID.get(0));
    }

    public TextureManager getTextureManager() {
        return textureManager;
    }

    public GL3 getGl() {
        return gl;
    }

    public Camera getActiveCamera() {
        return activeCamera;
    }

    public void setActiveCamera(Camera activeCamera) {
        this.activeCamera = activeCamera;
    }

    public List<Node> getNodes() {
        return nodes;
    }

    private Matrix getPvMatrix() {
        if (activeCamera == null) {
            throw new JoglException("No active camera");
        }
        return activeCamera.getPVMatrix();
    }

    public void clear() {
        gl.glClearColor(0.2f, 0.2f, 0.2f, 0.0f);
        gl.glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
    }

    public void render() {
        Matrix pvMatrix = getPvMatrix();
        for (Node node : nodes) {
            node.renderNode(gl, pvMatrix, null, false);
        }
    }

    public Node getObjectByName(String name) {
        return searchByName(name, nodes);
    }

    private Node searchByName(String name, List<Node> nodes) {
        for (Node n : nodes) {
            if (name.equals(n.getName())) {
                return n;
            }
            Node result = searchByName(name, n.getChildren());
            if (result != null) {
                return result;
            }
        }

        return null;
    }
}
