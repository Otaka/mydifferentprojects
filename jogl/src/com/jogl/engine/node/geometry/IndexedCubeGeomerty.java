package com.jogl.engine.node.geometry;

import com.jogl.engine.SceneManager;

/**
 * @author Dmitry
 */
public class IndexedCubeGeomerty extends IndexedGeometry {

    public IndexedCubeGeomerty(SceneManager sceneManager, float c) {
        float[] vertices = new float[]{
            -c, c, -c,
            -c, c, c,
            c, c, c,
            c, c, -c,
            -c, -c, -c,
            -c, -c, c,
            c, -c, c,
            c, -c, -c
        };

        float[] uv = new float[]{
            0.000059f, 1.0f - 0.000004f,
            0.000103f, 1.0f - 0.336048f,
            0.335973f, 1.0f - 0.335903f,
            1.000023f, 1.0f - 0.000013f,
            0.667979f, 1.0f - 0.335851f,
            0.999958f, 1.0f - 0.336064f,
            0.667979f, 1.0f - 0.335851f,
            0.336024f, 1.0f - 0.671877f,};

        int[] indexes = dec(new int[]{
            1, 2, 4,
            2, 3, 4,
            8, 4, 3,
            3, 7, 8,
            5, 2, 1,
            5, 6, 2,
            1, 4, 8,
            8, 5, 1,
            7, 6, 5,
            8, 7, 5,
            6, 3, 2,
            3, 6, 7
        });

        init(sceneManager, vertices, uv, indexes);
    }

    private int[] dec(int[] array) {
        for (int i = 0; i < array.length; i++) {
            array[i]--;
        }
        return array;
    }
}
