package com.jogl.engine.mesh.loader.impl.binarymdl;

import java.util.List;

/**
 * @author Dmitry
 */
public class Mesh {
    private String name;
    private Mesh parent;
    private float[] bboxmin;
    private float[] bboxmax;
    private float radius;
    private String bitmap;
    private int vertexNum;
    private float[] vertexCoords;

    private int facesNumber;
    private short[] faces;
    private boolean visible;
    private boolean shadow;
    private int bonesNumber;
    private float[] position;
    private float[] rotation;
    private float[] textureCoordinate;
    private Mesh[] children;
    private List<AnimationChunk> animationChunks;

    public List<AnimationChunk> getAnimationChunks() {
        return animationChunks;
    }

    public void setAnimationChunks(List<AnimationChunk> animationChunks) {
        this.animationChunks = animationChunks;
    }

    public Mesh[] getChildren() {
        return children;
    }

    public void setChildren(Mesh[] children) {
        this.children = children;
    }

    public float[] getTextureCoordinate() {
        return textureCoordinate;
    }

    public void setTextureCoordinate(float[] textureCoordinate) {
        this.textureCoordinate = textureCoordinate;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Mesh getParent() {
        return parent;
    }

    public void setParent(Mesh parent) {
        this.parent = parent;
    }

    public float[] getBboxmin() {
        return bboxmin;
    }

    public void setBboxmin(float[] bboxmin) {
        this.bboxmin = bboxmin;
    }

    public float[] getBboxmax() {
        return bboxmax;
    }

    public void setBboxmax(float[] bboxmax) {
        this.bboxmax = bboxmax;
    }

    public float getRadius() {
        return radius;
    }

    public void setRadius(float radius) {
        this.radius = radius;
    }

    public String getBitmap() {
        return bitmap;
    }

    public void setBitmap(String bitmap) {
        this.bitmap = bitmap;
    }

    public int getVertexNum() {
        return vertexNum;
    }

    public void setVertexNum(int vertexNum) {
        this.vertexNum = vertexNum;
    }

    public float[] getVertexCoords() {
        return vertexCoords;
    }

    public void setVertexCoords(float[] vertexCoords) {
        this.vertexCoords = vertexCoords;
    }

    public int getFacesNumber() {
        return facesNumber;
    }

    public void setFacesNumber(int facesNumber) {
        this.facesNumber = facesNumber;
    }

    public short[] getFaces() {
        return faces;
    }

    public void setFaces(short[] faces) {
        this.faces = faces;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public boolean isShadow() {
        return shadow;
    }

    public void setShadow(boolean shadow) {
        this.shadow = shadow;
    }

    public int getBonesNumber() {
        return bonesNumber;
    }

    public void setBonesNumber(int bonesNumber) {
        this.bonesNumber = bonesNumber;
    }

    public float[] getPosition() {
        return position;
    }

    public void setPosition(float[] position) {
        this.position = position;
    }

    public float[] getRotation() {
        return rotation;
    }

    public void setRotation(float[] rotation) {
        this.rotation = rotation;
    }

    @Override
    public String toString() {
        return name;
    }
}
