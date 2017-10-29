package com.jogl.engine.node.geometry;

import static com.jogamp.opengl.GL.*;
import com.jogamp.opengl.GL3;
import static com.jogl.Utils.setBufferData;
import com.jogl.engine.SceneManager;
import com.jogl.engine.exceptions.JoglException;
import com.jogl.engine.math.Matrix;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

/**
 * @author Dmitry
 */
public class NotIndexedGeometry extends Geometry {
    private IntBuffer meshVerticesBufferId;
    private IntBuffer uvBuffer;
    private int vertexCount;
    private int shaderMvpUniform = -1;

    public void init(SceneManager sceneManager, float[] vertices, float[] uv) {
        if (vertices.length % 3 != 0) {
            throw new JoglException("Vertices count is not divided on 3. Current vertices length=" + vertices.length);
        }

        meshVerticesBufferId = IntBuffer.allocate(1);
        sceneManager.getGl().glGenBuffers(1, meshVerticesBufferId);
        setBufferData(sceneManager.getGl(), meshVerticesBufferId, FloatBuffer.wrap(vertices));

        if (uv == null) {
            uv = generateUvsCoordinates(vertices.length / 3);
        }

        uvBuffer = IntBuffer.allocate(1);
        sceneManager.getGl().glGenBuffers(1, uvBuffer);
        setBufferData(sceneManager.getGl(), uvBuffer, FloatBuffer.wrap(uv));
        vertexCount = vertices.length;
    }

    private float[] generateUvsCoordinates(int count) {
        int verticesCount = count * 2;
        float[] array = new float[verticesCount];
        for (int i = 0; i < verticesCount; i++) {
            array[i] = 0f;
        }
        return array;
    }

    @Override
    public void materialChanged() {
        super.materialChanged();
        shaderMvpUniform = -1;
    }

    @Override
    public void renderGeometry(GL3 gl, Matrix pvMatrix, Matrix modelm, Matrix pvmMatrix, int programId) {

        if (shaderMvpUniform == -1) {
            shaderMvpUniform = gl.glGetUniformLocation(programId, "MVP");
        }
        gl.glUniformMatrix4fv(shaderMvpUniform, 1, false, pvmMatrix.getFloatBuffer());
        gl.glEnableVertexAttribArray(0);

        gl.glBindBuffer(GL_ARRAY_BUFFER, meshVerticesBufferId.get(0));
        gl.glVertexAttribPointer(0,
                3,
                GL_FLOAT,
                false,
                0, 0l);

        gl.glEnableVertexAttribArray(1);
        gl.glBindBuffer(GL_ARRAY_BUFFER, uvBuffer.get(0));
        gl.glVertexAttribPointer(1,
                2, GL_FLOAT, false, 0, 0);

        gl.glDrawArrays(GL_TRIANGLES, 0, vertexCount);
        gl.glDisableVertexAttribArray(0);
        if (uvBuffer != null) {
            gl.glDisableVertexAttribArray(1);
        }
    }
}
