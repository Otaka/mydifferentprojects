package com.jogl.engine.node;

import com.jogl.engine.SceneManager;
import com.jogl.engine.math.Matrix;
import com.jogl.engine.math.Vector4;

/**
 * @author Dmitry
 */
public class Camera extends Node {

    private Matrix projection;
    private final Matrix pv = new Matrix();

    private final Vector4 cameraTarget = new Vector4(0, 0, 10f);//original values of the camera target and up, just to not re instantiate objects
    private final Vector4 cameraUp = new Vector4(0, 10f, 0, 0);

    private final Vector4 currentCameraTarget = new Vector4(0, 0, 10f);
    private final Vector4 currentCameraUp = new Vector4(0, 10f, 0);

    public Camera(SceneManager sceneManager, Node parent) {
        super(sceneManager, parent);
        if (parent != null) {
            throw new RuntimeException("Camera should not be attached to parent");
        }

        projectionCamera(45.0f, 4.0f / 3.0f, 0.1f, 100.0f);
    }

    public Camera(SceneManager sceneManager) {
        super(sceneManager);
        projectionCamera(45.0f, 4.0f / 3.0f, 0.1f, 1000.0f);
    }

    private void projectionCamera(float fov, float aspect, float near, float far) {
        projection = new Matrix();
        projection.makePerspectiveProjectionMatrix(fov, aspect, near, far);
    }

    public Matrix getPVMatrix() {
        boolean shouldRecalculate = false;
        Matrix cameraModelMatrix = null;
        if (getParent() == null) {
            if (dirty) {
                cameraModelMatrix = getTransformation();
                shouldRecalculate = true;
                dirty = false;
            }
        } else {
            throw new RuntimeException("Camera should not be attached to parent");
        }

        if (shouldRecalculate) {
            cameraModelMatrix.multVectorOnMatrix(cameraTarget, currentCameraTarget);
            cameraModelMatrix.multVectorOnMatrix(cameraUp, currentCameraUp);
            Matrix camModel = new Matrix();
            camModel.lookAt(getX(), getY(), getZ(), currentCameraTarget.getX(), currentCameraTarget.getY(), currentCameraTarget.getZ(), currentCameraUp.getX(), currentCameraUp.getY(), currentCameraUp.getZ());
            pv.loadIdentity();
            pv.multMatrix(projection);
            pv.multMatrix(camModel);
        }

        return pv;
    }

    /*public void setPositionAndLook(float x, float y, float z, float targetX, float targetY, float targetZ, float upX, float upY, float upZ) {
     transformation.lookAt(x, y, z, targetX, targetY, targetZ, upX, upY, upZ);
     dirty = true;
     }*/
}
