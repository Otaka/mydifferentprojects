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
public class IndexedGeometry extends Geometry {
    private IntBuffer meshVerticesBufferId;
    private IntBuffer modelIndexesId;
    private IntBuffer uvBufferId;
    private int indexCount;
    private int shaderMvpUniform = -1;

    public void init(SceneManager sceneManager, float[] vertices, float[] uv, int[] indexes) {
        if (vertices.length % 3 != 0) {
            throw new JoglException("Vertices count is not divided on 3. Current vertices length=" + vertices.length);
        }

        meshVerticesBufferId = IntBuffer.allocate(1);
        sceneManager.getGl().glGenBuffers(1, meshVerticesBufferId);
        setBufferData(sceneManager.getGl(), meshVerticesBufferId, FloatBuffer.wrap(vertices));

        modelIndexesId = IntBuffer.allocate(1);
        sceneManager.getGl().glGenBuffers(1, modelIndexesId);
        sceneManager.getGl().glBindBuffer(GL3.GL_ELEMENT_ARRAY_BUFFER, modelIndexesId.get(0));
        sceneManager.getGl().glBufferData(GL3.GL_ELEMENT_ARRAY_BUFFER, indexes.length * 4, IntBuffer.wrap(indexes), GL3.GL_STATIC_DRAW);

        if (uv == null) {
            uv = generateUvsCoordinates(vertices.length / 3);
        }

        uvBufferId = IntBuffer.allocate(1);
        sceneManager.getGl().glGenBuffers(1, uvBufferId);
        setBufferData(sceneManager.getGl(), uvBufferId, FloatBuffer.wrap(uv));

        indexCount = indexes.length;
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
        gl.glEnableVertexAttribArray(1);

        gl.glBindBuffer(GL_ARRAY_BUFFER, meshVerticesBufferId.get(0));
        gl.glVertexAttribPointer(0,
                3,
                GL_FLOAT,
                false,
                0, 0l);

        gl.glBindBuffer(GL_ARRAY_BUFFER, uvBufferId.get(0));
        gl.glVertexAttribPointer(1,
                2, GL_FLOAT, false, 0, 0);

        gl.glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, modelIndexesId.get(0));

        gl.glDrawElements(GL_TRIANGLES, indexCount, GL_UNSIGNED_INT, 0);
        gl.glDisableVertexAttribArray(0);
        gl.glDisableVertexAttribArray(1);
    }
}
