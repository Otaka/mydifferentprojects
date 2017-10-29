package com.jogl.engine.node;

import com.jogamp.opengl.GL3;
import com.jogamp.opengl.math.FloatUtil;
import com.jogl.engine.SceneManager;
import com.jogl.engine.material.Material;
import com.jogl.engine.math.Matrix;
import com.jogl.engine.node.geometry.Geometry;
import java.util.*;

/**
 * @author Dmitry
 */
public class Node {
    protected final SceneManager sceneManager;
    protected String name;
    protected Node parent;
    protected final Matrix transformation;
    protected List<Geometry> geometryList;
    protected List<Node> children;
    protected boolean dirty;
    protected boolean visible;
    protected float pX,pY,pZ;
    protected float rX, rY, rZ;
    protected float sX = 1, sY = 1, sZ = 1;

    //matrix that tries to store world transformation or parent1*parent2*...*parentN*current matrices
    private Matrix transformationCache;
    private Matrix pvmMatrix;


    public Node(SceneManager sceneManager, Node parent) {
        this(sceneManager);
        this.parent = parent;
    }

    public Node(SceneManager sceneManager) {
        this.sceneManager = sceneManager;
        transformation = new Matrix();
        transformationCache = new Matrix();
        pvmMatrix = new Matrix();
        geometryList = new ArrayList<>();
        children = new ArrayList<>();
        dirty = true;
        visible = true;
    }

    public SceneManager getSceneManager() {
        return sceneManager;
    }

    public Matrix getTransformation() {
        return transformation;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isDirty() {
        return dirty;
    }

    public void setDirty(boolean dirty) {
        this.dirty = dirty;
    }

    public void setParent(Node parent) {
        this.parent = parent;
    }

    public Node getParent() {
        return parent;
    }

    public List<Node> getChildren() {
        return children;
    }

    public List<Geometry> getGeometryList() {
        return geometryList;
    }

    public void setMaterial(Material material) {
        for (Geometry g : geometryList) {
            g.setMaterial(material);
        }
    }

    public void setMaterial(int index, Material material) {
        geometryList.get(index).setMaterial(material);
    }

    public void moveX(float x) {
        transformation.translate(x, 0, 0);
        pX+=x;
        dirty = true;
    }

    public void moveY(float y) {
        transformation.translate(0, y, 0);
        pY+=y;
        dirty = true;
    }

    public void moveZ(float z) {
        transformation.translate(0, 0, z);
        pZ += z;
        dirty = true;
    }

    public void rotateX(float x) {
        transformation.rotate(x, 1, 0, 0);
        this.rX += x;
        dirty = true;
    }

    public void rotateY(float y) {
        transformation.rotate(y, 0, 1, 0);
        this.rY += y;
        dirty = true;
    }

    public void rotateZ(float z) {
        transformation.rotate(z, 0, 0, 1);
        this.rZ += z;
        dirty = true;
    }

    public void scaleX(float x) {
        transformation.scale(x, 1, 1);
        this.sX *= x;
        dirty = true;
    }

    public void scaleY(float y) {
        transformation.scale(1, y, 1);
        this.sY *= y;
        dirty = true;
    }

    public void scaleZ(float z) {
        transformation.scale(1, 1, z);
        this.sZ *= z;
        dirty = true;
    }

    public void move(float x, float y, float z) {
        transformation.translate(x, y, z);
        pX += x;
        pY += y;
        pZ += z;
        dirty = true;
    }

    public float getX() {
        return pX;
    }

    public float getY() {
        return pY;
    }

    public float getZ() {
        return pZ;
    }

    public float getRotationX() {
        return rX;
    }

    public float getRotationY() {
        return rY;
    }

    public float getRotationZ() {
        return rZ;
    }

    public float getScaleX() {
        return sX;
    }

    public float getScaleY() {
        return sY;
    }

    public float getScaleZ() {
        return sZ;
    }

    @Deprecated
    public void setPositionInternal(float x, float y, float z) {
        transformation.translate(x, y, z);
        pX = x;
        pY = y;
        pZ = z;
        dirty = true;
    }

    public void setPosition(float x, float y, float z) {
        move(x-pX, y-pY, z-pZ);
        dirty = true;
    }

    /**
     * Quaternion orientation
     */
    public void setRotationFromQuaternion(float qX, float qY, float qZ, float qW) {
        double sqw = qW * qW;
        double sqx = qX * qX;
        double sqy = qY * qY;
        double sqz = qZ * qZ;
        float resultX;
        float resultY;
        float resultZ;
        // If quaternion is normalised the unit is one, otherwise it is the correction factor
        double unit = sqx + sqy + sqz + sqw;
        double test = qX * qY + qZ * qW;

        if (test > 0.499f * unit) {
            // Singularity at north pole
            resultY = 2f * (float) Math.atan2(qX, qW);
            resultX = (float) (Math.PI * 0.5f);
            resultZ = 0f;
            setRotation(resultX, resultY, resultZ);
        } else if (test < -0.499f * unit) {
            // Singularity at south pole
            resultY = -2f * (float) Math.atan2(qX, qW);
            resultX = (float) (-Math.PI * 0.5f);
            resultZ = 0f;
            setRotation(resultX, resultY, resultZ);
        }

        resultY = (float) Math.atan2(2 * qY * qW - 2 * qX * qZ, sqx - sqy - sqz + sqw);
        resultX = (float) Math.asin(2 * test / unit);
        resultZ = (float) Math.atan2(2 * qX * qW - 2 * qY * qZ, -sqx + sqy - sqz + sqw);
        setRotation(resultX, resultY, resultZ);
    }

    public void setRotation(float x, float y, float z) {
        transformation.loadIdentity();
        transformation.scale(sX, sY, sZ);
        transformation.rotate(x, 1, 0, 0);
        transformation.rotate(y, 0, 1, 0);
        transformation.rotate(z, 0, 0, 1);
        //  transformation.translate(this.x, this.y, this.z);
        this.rX = x;
        this.rY = y;
        this.rZ = z;
        dirty = true;
    }

    public void setRotationInternal(float x, float y, float z) {
        transformation.rotate(x, 1, 0, 0);
        transformation.rotate(y, 0, 1, 0);
        transformation.rotate(z, 0, 0, 1);
        this.rX = x;
        this.rY = y;
        this.rZ = z;
        dirty = true;
    }

    public void setRotationAxisAngle(float x, float y, float z, float angle) {
        transformation.rotate(angle, x, y, z);
        this.rX = x;
        this.rY = y;
        this.rZ = z;
        dirty = true;
    }

    /*public void setScale(float x, float y, float z) {
     transformation.loadIdentity();
     transformation.scale(x, y, z);
     transformation.rotate(rX, 1, 0, 0);
     transformation.rotate(rY, 0, 1, 0);
     transformation.rotate(rZ, 0, 0, 1);
     transformation.translate(this.x, this.y, this.z);
     this.sX = x;
     this.sY = y;
     this.sZ = z;
     dirty = true;
     }*/

    /*public void setX(float x) {
     setPosition(x, y, z);
     }

     public void setY(float y) {
     setPosition(x, y, z);
     }

     public void setZ(float z) {
     setPosition(x, y, z);
     }
     */
    public void setRotationX(float x) {
        setRotation(x, rY, rZ);
    }

    public void setRotationY(float y) {
        setRotation(rX, y, rZ);
    }

    public void setRotationZ(float z) {
        setRotation(rX, rY, z);
    }

    /* public void setScaleX(float x) {
     setScale(x, sY, sZ);
     }

     public void setScaleY(float y) {
     setScale(sX, y, sZ);
     }

     public void setScaleZ(float z) {
     setScale(sX, sY, z);
     }*/
    public void renderNode(GL3 gl, Matrix pvMatrix, Matrix parentMatrix, boolean parentDirty) {
        render(gl, pvMatrix, parentMatrix, parentDirty);
        if (!children.isEmpty()) {
            for (Node node : children) {
                node.renderNode(gl, pvMatrix, transformationCache, dirty);
            }
        }

        dirty = false;
    }

    public void render(GL3 gl, Matrix pvMatrix, Matrix parentMatrix, boolean parentDirty) {
        if (dirty || parentDirty) {
            if (parentMatrix != null) {
                transformationCache.loadFromMatrix(parentMatrix);
            } else {
                transformationCache.loadIdentity();
            }
            dirty = true;
            transformationCache.multMatrix(transformation);
        }

        pvmMatrix.loadFromMatrix(pvMatrix);
        pvmMatrix.multMatrix(transformationCache);
        if (visible) {
            for (Geometry g : geometryList) {
                g.render(gl, pvMatrix, transformationCache, pvmMatrix);
            }
        }
    }

    public void addChild(Node child) {
        child.setParent(this);
        children.add(child);
    }

    @Override
    public String toString() {
        return name;
    }

}
