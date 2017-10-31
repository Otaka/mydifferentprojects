package com.jogl.engine.node;

import com.jogamp.opengl.GL3;
import com.jogamp.opengl.math.Quaternion;
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
    protected float pX, pY, pZ;
    private Quaternion rotationQuaternion = new Quaternion();
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
        pX += x;
        dirty = true;
    }

    public void moveY(float y) {
        transformation.translate(0, y, 0);
        pY += y;
        dirty = true;
    }

    public void moveZ(float z) {
        transformation.translate(0, 0, z);
        pZ += z;
        dirty = true;
    }

    public void rotateX(float x) {
        transformation.rotate(x, 1, 0, 0);
        rotationQuaternion.rotateByAngleX(x);
        dirty = true;
    }

    public void rotateY(float y) {
        transformation.rotate(y, 0, 1, 0);
        rotationQuaternion.rotateByAngleY(y);
        dirty = true;
    }

    public void rotateZ(float z) {
        transformation.rotate(z, 0, 0, 1);
        rotationQuaternion.rotateByAngleZ(z);
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

    public float getScaleX() {
        return sX;
    }

    public float getScaleY() {
        return sY;
    }

    public float getScaleZ() {
        return sZ;
    }

    public void setPosition(float x, float y, float z) {
        move(x - pX, y - pY, z - pZ);
        dirty = true;
    }

    public void setRotationFromQuaternion(Quaternion q) {
        //restore rotation
        rotationQuaternion.invert();
        transformation.rotate(rotationQuaternion);
        
        //rotate to necessary angle
        transformation.rotate(q);
        rotationQuaternion.set(q);
        dirty = true;
    }

    public void setRotation(float x, float y, float z) {
        //restore rotation
        rotationQuaternion.invert();
        transformation.rotate(rotationQuaternion);
        rotationQuaternion.setFromEuler(x, y, z);
        //rotate to necessary angle
        transformation.rotate(rotationQuaternion);
        dirty = true;
    }

    public void setRotationFromAxisAngle(float x, float y, float z, float angle) {
        //restore rotation
        rotationQuaternion.invert();
        transformation.rotate(rotationQuaternion);

        //rotate to angle axis
        rotationQuaternion.setFromAngleAxis(angle, new float[]{x, y, z}, new float[3]);
        transformation.rotate(rotationQuaternion);
        dirty = true;
    }


    public void setScale(float x, float y, float z) {
        transformation.scale(x / sX, y / sY, z / sZ);
        sX = x;
        sY = y;
        sZ = z;
        dirty = true;
    }

    public void setX(float x) {
        setPosition(x, pY, pZ);
    }

    public void setY(float y) {
        setPosition(pX, y, pZ);
    }

    public void setZ(float z) {
        setPosition(pX, pY, z);
    }

    public void renderNode(GL3 gl, Matrix pvMatrix, Matrix parentMatrix, boolean parentDirty) {
        renderInternal(gl, pvMatrix, parentMatrix, parentDirty);
        if (!children.isEmpty()) {
            for (Node node : children) {
                node.renderNode(gl, pvMatrix, transformationCache, dirty);
            }
        }

        dirty = false;
    }

    private void renderInternal(GL3 gl, Matrix pvMatrix, Matrix parentMatrix, boolean parentDirty) {
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
