package com.jogl.engine.node;

import com.jogamp.opengl.GL3;
import com.jogamp.opengl.math.Quaternion;
import com.jogl.engine.SceneManager;
import com.jogl.engine.material.Material;
import com.jogl.engine.math.Matrix;
import com.jogl.engine.math.Vector4;
import com.jogl.engine.node.geometry.Geometry;
import java.util.*;

/**
 * @author Dmitry
 */
public class Node {

    protected final SceneManager sceneManager;
    protected String name;
    protected Node parent;

    protected List<Geometry> geometryList;
    protected List<Node> children;
    protected boolean dirty;
    protected boolean visible;

    protected Vector4 localPosition = new Vector4(0, 0, 0, 1);
    private Quaternion localRotation = new Quaternion();
    protected Vector4 localScale = new Vector4(1, 1, 1, 1);

    protected final Matrix localTransform;
    private Matrix worldTransform;

    private Matrix pvmMatrix;

    public Node(SceneManager sceneManager, Node parent) {
        this(sceneManager);
        this.parent = parent;
    }

    public Node(SceneManager sceneManager) {
        this.sceneManager = sceneManager;
        localTransform = new Matrix();
        worldTransform = new Matrix();
        pvmMatrix = new Matrix();
        geometryList = new ArrayList<>();
        children = new ArrayList<>();
        dirty = true;
        visible = true;
    }

    public SceneManager getSceneManager() {
        return sceneManager;
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

    public void positionNode(float x, float y, float z, boolean global) {

        dirty = true;
    }

    public void setRotationFromQuaternion(Quaternion q) {
        //restore rotation
        localRotation.invert();
        localTransform.rotate(localRotation);

        //rotate to necessary angle
        localTransform.rotate(q);
        localRotation.set(q);
        dirty = true;
    }

    public void setRotation(float x, float y, float z) {
        //restore rotation
        localRotation.invert();
        localTransform.rotate(localRotation);
        localRotation.setFromEuler(x, y, z);
        //rotate to necessary angle
        localTransform.rotate(localRotation);
        dirty = true;
    }

    public void setRotationFromAxisAngle(float x, float y, float z, float angle) {
        //restore rotation
        localRotation.invert();
        localTransform.rotate(localRotation);

        //rotate to angle axis
        localRotation.setFromAngleAxis(angle, new float[]{x, y, z}, new float[3]);
        localTransform.rotate(localRotation);
        dirty = true;
    }

    public void renderNode(GL3 gl, Matrix pvMatrix, Matrix parentMatrix, boolean parentDirty) {
        renderInternal(gl, pvMatrix, parentMatrix, parentDirty);
        if (!children.isEmpty()) {
            for (Node node : children) {
                node.renderNode(gl, pvMatrix, worldTransform, dirty);
            }
        }

        dirty = false;
    }

    private void renderInternal(GL3 gl, Matrix pvMatrix, Matrix parentMatrix, boolean parentDirty) {
        if (dirty || parentDirty) {
            if (parentMatrix != null) {
                worldTransform.loadFromMatrix(parentMatrix);
            } else {
                worldTransform.loadIdentity();
            }

            dirty = true;
            worldTransform.multMatrix(localTransform);
        }

        pvmMatrix.loadFromMatrix(pvMatrix);
        pvmMatrix.multMatrix(worldTransform);
        if (visible) {
            for (Geometry g : geometryList) {
                g.render(gl, pvMatrix, worldTransform, pvmMatrix);
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

    private Vector4 tvector1 = new Vector4(0, 0, 0, 1);
    private Vector4 tvector2 = new Vector4(0, 0, 0, 1);

    public void setWorldPosition(float x, float y, float z) {
        if (parent != null) {
            tvector1.set(x, y, z);
            parent.getWorldTransform().multVectorOnMatrix(tvector1, tvector2);
            setLocalPosition(tvector2.getX(), tvector2.getY(), tvector2.getZ());
        } else {
            setLocalPosition(x, y, z);
        }
    }

    public void setLocalPosition(float x, float y, float z) {
        localPosition.set(x, y, z);
        dirty = true;
    }

    public Vector4 getLocalPosition() {
        return localPosition;
    }

    private Vector4 worldPosition = new Vector4();

    public Vector4 getWorldPosition() {
        getWorldTransform().multVectorOnMatrix(getLocalPosition(), worldPosition);
        return worldPosition;
    }

    public void setLocalRotation(Quaternion quaternion) {
        this.localRotation.set(quaternion).normalize();
        dirty = true;
    }

    public void setWorldRotation(Quaternion quaternion) {
        if (parent != null) {
            tempQuaternion.set(parent.getWorldRotation());
            tempQuaternion.mult(quaternion);
            setLocalRotation(tempQuaternion);
        } else {
            setLocalRotation(quaternion);
        }
    }

    public Quaternion getLocalRotation() {
        return localRotation;
    }

    private Quaternion tempQuaternion = new Quaternion();

    public Quaternion getWorldRotation() {
        if (parent != null) {
            return localRotation;
        }

        tempQuaternion.set(parent.getWorldRotation());
        tempQuaternion.mult(localRotation);
        return tempQuaternion;
    }

    public Vector4 getLocalScale() {
        return localScale;
    }

    private Vector4 tscaleVector = new Vector4(1, 1, 1, 1);

    public Vector4 getWorldScale() {
        if (parent != null) {
            tscaleVector.set(parent.getWorldScale());
            tscaleVector.mul(localScale);
            return tscaleVector;
        } else {
            return localScale;
        }
    }

    public void setLocalScale(Vector4 localScale) {
        this.localScale = localScale;
        dirty = true;
    }

    public void setWorldScale(Vector4 worldScale) {
        if (parent != null) {
            tscaleVector.set(worldScale);
            tscaleVector.div(parent.getWorldScale());
            setLocalScale(tscaleVector);
        } else {
            setLocalScale(worldScale);
        }
    }

    public Matrix getLocalTransform() {
        if(dirty){
            localTransform.loadIdentity();
            localTransform.scale(localScale.getX(),localScale.getY(),localScale.getZ());
            localTransform.rotate(tempQuaternion);
            localTransform.translate(localPosition.getX(), localPosition.getY(), localPosition.getZ());
            dirty=false;
        }
        return localTransform;
    }

    public Matrix getWorldTransform() {
        return worldTransform;
    }
}
