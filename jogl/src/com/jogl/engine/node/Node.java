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
    protected boolean localTransformDirty;
    protected boolean worldTransformDirty;
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
        localTransformDirty = true;
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

    public boolean isLocalTransformDirty() {
        return localTransformDirty;
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

        setDirty();
    }
/*
    public void setRotationFromQuaternion(Quaternion q) {
        //restore rotation
        localRotation.invert();
        localTransform.rotate(localRotation);

        //rotate to necessary angle
        localTransform.rotate(q);
        localRotation.set(q);
        setDirty();
    }

    public void setRotation(float x, float y, float z) {
        //restore rotation
        localRotation.invert();
        localTransform.rotate(localRotation);
        localRotation.setFromEuler(x, y, z);
        //rotate to necessary angle
        localTransform.rotate(localRotation);
        setDirty();
    }

    public void setRotationFromAxisAngle(float x, float y, float z, float angle) {
        //restore rotation
        localRotation.invert();
        localTransform.rotate(localRotation);

        //rotate to angle axis
        localRotation.setFromAngleAxis(angle, new float[]{x, y, z}, new float[3]);
        localTransform.rotate(localRotation);
        setDirty();
    }*/

    public void renderNode(GL3 gl, Matrix pvMatrix, Matrix parentMatrix, boolean parentDirty) {
        renderInternal(gl, pvMatrix, parentMatrix, parentDirty);
        if (!children.isEmpty()) {
            for (Node node : children) {
                node.renderNode(gl, pvMatrix, worldTransform, localTransformDirty);
            }
        }

        setDirty();
    }

    private void renderInternal(GL3 gl, Matrix pvMatrix, Matrix parentMatrix, boolean parentDirty) {
        if (localTransformDirty || parentDirty) {
            if (parentMatrix != null) {
                worldTransform.loadFromMatrix(parentMatrix);
            } else {
                worldTransform.loadIdentity();
            }

            worldTransform.multMatrix(localTransform);
            localTransformDirty = false;
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
        setDirty();
    }

    public Vector4 getLocalPosition() {
        return localPosition;
    }

    private Vector4 worldPosition = new Vector4();

    public Vector4 getWorldPosition() {
        getWorldTransform().multVectorOnMatrix(getLocalPosition(), worldPosition);
        return worldPosition;
    }

    /**
        Move in local space, take into account the orientation of the node
     */
    public void move(float x, float y, float z) {
        Vector4 tLocalPosition = getLocalPosition();
        tvector1.set(tLocalPosition);
        tvector1.rotateByQuaternion(getLocalRotation());
        tvector1.sum(x, y, z, 0);
        localPosition.set(tvector1);
        setDirty();
    }

    /**
        Move in local/global space, do not take into account the orientation of the node
     */
    public void translate(float x, float y, float z, boolean global) {
        if (global) {
            tvector1.set(getWorldPosition());
            tvector1.sum(x, y, z, 0);
            setWorldPosition(tvector1.getX(), tvector1.getY(), tvector1.getZ());
        } else {
            localPosition.sum(x, y, z, 0);
            setDirty();
        }
    }

    public void turn(float x, float y, float z, boolean global) {
        if(global){
            tempQuaternion2.setFromEuler(x, y, z);
            tempQuaternion2.mult(getWorldRotation());
            setWorldRotation(tempQuaternion2);
        }else{
            tempQuaternion.set(getLocalRotation());
            tempQuaternion2.setFromEuler(x,y,z);
            tempQuaternion.mult(tempQuaternion2);
            setLocalRotation(tempQuaternion);
        }
    }

    /**
    Set local rotation from euler angles
     */
    public void setLocalRotation(float x, float y, float z) {
        this.localRotation.setFromEuler(x, y, z);
        setDirty();
    }

    public void setLocalRotation(Quaternion quaternion) {
        this.localRotation.set(quaternion).normalize();
        setDirty();
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
    private Quaternion tempQuaternion2 = new Quaternion();

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
        this.localScale.set(localScale);
        setDirty();
    }
    
    public void setLocalScale(float x, float y, float z) {
        this.localScale.set(x, y, z);
        setDirty();
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
        if (localTransformDirty) {
            localTransform.loadIdentity();
            localTransform.scale(localScale.getX(), localScale.getY(), localScale.getZ());
            localTransform.rotate(tempQuaternion);
            localTransform.translate(localPosition.getX(), localPosition.getY(), localPosition.getZ());
            localTransformDirty = false;
        }

        return localTransform;
    }

    public Matrix getWorldTransform() {
        if (worldTransformDirty) {
            if (parent == null) {
                worldTransform.loadFromMatrix(getLocalTransform());
            } else {
                worldTransform.loadFromMatrix(parent.getWorldTransform());
                worldTransform.multMatrix(getLocalTransform());
            }

            worldTransformDirty = false;
        }

        return worldTransform;
    }

    private void setDirty() {
        localTransformDirty = true;
        worldTransformDirty = true;
    }
}
