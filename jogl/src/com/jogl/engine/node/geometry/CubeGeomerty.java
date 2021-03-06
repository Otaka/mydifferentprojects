package com.jogl.engine.node.geometry;

import com.jogl.engine.SceneManager;

/**
 * @author Dmitry
 */
public class CubeGeomerty extends NotIndexedGeometry {
    ;
    
    public CubeGeomerty(SceneManager sceneManager, float cubeSize) {
        float[] vertices = new float[]{
            -cubeSize, -cubeSize, -cubeSize,
            -cubeSize, -cubeSize, cubeSize,
            -cubeSize, cubeSize, cubeSize,
            cubeSize, cubeSize, -cubeSize,
            -cubeSize, -cubeSize, -cubeSize,
            -cubeSize, cubeSize, -cubeSize,
            cubeSize, -cubeSize, cubeSize,
            -cubeSize, -cubeSize, -cubeSize,
            cubeSize, -cubeSize, -cubeSize,
            cubeSize, cubeSize, -cubeSize,
            cubeSize, -cubeSize, -cubeSize,
            -cubeSize, -cubeSize, -cubeSize,
            -cubeSize, -cubeSize, -cubeSize,
            -cubeSize, cubeSize, cubeSize,
            -cubeSize, cubeSize, -cubeSize,
            cubeSize, -cubeSize, cubeSize,
            -cubeSize, -cubeSize, cubeSize,
            -cubeSize, -cubeSize, -cubeSize,
            -cubeSize, cubeSize, cubeSize,
            -cubeSize, -cubeSize, cubeSize,
            cubeSize, -cubeSize, cubeSize,
            cubeSize, cubeSize, cubeSize,
            cubeSize, -cubeSize, -cubeSize,
            cubeSize, cubeSize, -cubeSize,
            cubeSize, -cubeSize, -cubeSize,
            cubeSize, cubeSize, cubeSize,
            cubeSize, -cubeSize, cubeSize,
            cubeSize, cubeSize, cubeSize,
            cubeSize, cubeSize, -cubeSize,
            -cubeSize, cubeSize, -cubeSize,
            cubeSize, cubeSize, cubeSize,
            -cubeSize, cubeSize, -cubeSize,
            -cubeSize, cubeSize, cubeSize,
            cubeSize, cubeSize, cubeSize,
            -cubeSize, cubeSize, cubeSize,
            cubeSize, -cubeSize, cubeSize
        };
        float[] uv = new float[]{
            0.000059f, 1.0f - 0.000004f,
            0.000103f, 1.0f - 0.336048f,
            0.335973f, 1.0f - 0.335903f,
            1.000023f, 1.0f - 0.000013f,
            0.667979f, 1.0f - 0.335851f,
            0.999958f, 1.0f - 0.336064f,
            0.667979f, 1.0f - 0.335851f,
            0.336024f, 1.0f - 0.671877f,
            0.667969f, 1.0f - 0.671889f,
            1.000023f, 1.0f - 0.000013f,
            0.668104f, 1.0f - 0.000013f,
            0.667979f, 1.0f - 0.335851f,
            0.000059f, 1.0f - 0.000004f,
            0.335973f, 1.0f - 0.335903f,
            0.336098f, 1.0f - 0.000071f,
            0.667979f, 1.0f - 0.335851f,
            0.335973f, 1.0f - 0.335903f,
            0.336024f, 1.0f - 0.671877f,
            1.000004f, 1.0f - 0.671847f,
            0.999958f, 1.0f - 0.336064f,
            0.667979f, 1.0f - 0.335851f,
            0.668104f, 1.0f - 0.000013f,
            0.335973f, 1.0f - 0.335903f,
            0.667979f, 1.0f - 0.335851f,
            0.335973f, 1.0f - 0.335903f,
            0.668104f, 1.0f - 0.000013f,
            0.336098f, 1.0f - 0.000071f,
            0.000103f, 1.0f - 0.336048f,
            0.000004f, 1.0f - 0.671870f,
            0.336024f, 1.0f - 0.671877f,
            0.000103f, 1.0f - 0.336048f,
            0.336024f, 1.0f - 0.671877f,
            0.335973f, 1.0f - 0.335903f,
            0.667969f, 1.0f - 0.671889f,
            1.000004f, 1.0f - 0.671847f,
            0.667979f, 1.0f - 0.335851f
        };
        init(sceneManager, vertices, uv);
    }
}
