package com.jogl.engine.mesh.loader.impl;

import com.jogamp.opengl.math.Quaternion;
import com.jogl.engine.SceneManager;
import com.jogl.engine.exceptions.JoglException;
import com.jogl.engine.material.ShaderMaterial;
import com.jogl.engine.mesh.loader.MeshLoader;
import com.jogl.engine.node.AnimationNode;
import com.jogl.engine.node.Node;
import com.jogl.engine.node.animator.AnimationChannel;
import com.jogl.engine.node.animator.Animator;
import com.jogl.engine.node.animator.LinearPositionAnimator;
import com.jogl.engine.node.animator.LinearRotationAnimator;
import com.jogl.engine.node.geometry.IndexedGeometry;
import com.jogl.engine.texture.Texture;
import com.jogl.engine.utils.io.JoglFileInputStream;
import java.io.*;
import java.text.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * @author Dmitry
 */
public class AsciiMdlLoader extends MeshLoader {

    private static final String[] exts = new String[]{"mdl"};
    private final DecimalFormat format;
    private final Map<String, Node> nodes = new HashMap<>();
    private File directory;
    private Map<String, AnimationChannel> animators = new HashMap<>();

    public AsciiMdlLoader() {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        symbols.setDecimalSeparator('.');
        format = new DecimalFormat("0.#");
        format.setDecimalFormatSymbols(symbols);
        format.setMaximumFractionDigits(15);
    }

    @Override
    public String[] getExtensions() {
        return exts;
    }

    @Override
    public boolean isMatch(JoglFileInputStream stream) {
        try {
            stream.mark(20);
            byte[] buffer = new byte[10];
            stream.read(buffer);
            stream.reset();
            String magic = new String(buffer);
            return magic.equals("# mdlops ");
        } catch (IOException ex) {
            throw new JoglException("Error while reading stream", ex);
        }
    }

    private float parseFloat(String value) {
        try {
            value = value.replace('e', 'E');
            return format.parse(value).floatValue();
        } catch (ParseException ex) {
            throw new RuntimeException("Error while trying to parse float value " + value, ex);
        }
    }

    @Override
    public Node load(SceneManager sceneManager, File file, JoglFileInputStream fileInputStream) throws IOException {
        BufferedReader stream = new BufferedReader(new InputStreamReader(fileInputStream));
        String line;
        String modelName;
        this.directory = file.getParentFile();
        Node node = null;
        while ((line = stream.readLine()) != null) {
            line = line.trim();
            if (line.isEmpty()) {
                continue;
            }
            String[] values = StringUtils.split(line, ' ');
            String token = values[0];
            if (token.equals("newmodel")) {
                modelName = values[1];
            }

            if (token.equals("beginmodelgeom")) {
                node = loadGeometry(sceneManager, stream);
            }

            if (token.equals("newanim")) {
                loadAnimation(values[1], stream);
            }
        }

        if (node != null) {
            ((AnimationNode) node).setAnimators(animators);
        }

        return node;
    }

    private void loadAnimation(String animationName, BufferedReader stream) throws IOException {
        String line;
        AnimationChannel currentAnimationChannel = new AnimationChannel();
        currentAnimationChannel.setName(animationName);

        while ((line = stream.readLine()) != null) {
            line = line.trim();
            String[] values = StringUtils.split(line, ' ');
            String token = values[0];
            if (token.equalsIgnoreCase("length")) {
                currentAnimationChannel.setLength(Float.parseFloat(values[1]));
            } else if (token.equalsIgnoreCase("node")) {
                String nodeName = values[2];
                List<Animator> animatorsList = parseNodeAnimators(nodeName, stream);
                if (animatorsList != null && !animatorsList.isEmpty()) {
                    currentAnimationChannel.getAnimators().addAll(animatorsList);
                }
            } else if (token.equalsIgnoreCase("doneanim")) {
                break;
            }
        }

        animators.put(animationName, currentAnimationChannel);
    }

    private Node getNodeByName(String name) {
        Node n = nodes.get(name);
        if (n == null) {
            throw new IllegalArgumentException("Cannot find node with name [" + name + "]");
        }
        return n;
    }

    private List<Animator> parseNodeAnimators(String nodeName, BufferedReader stream) throws IOException {
        List<Animator> animatorList = new ArrayList<>();
        String line;
        while ((line = stream.readLine()) != null) {
            line = line.trim();
            String[] values = StringUtils.split(line, ' ');
            String token = values[0];
            if (token.equals("positionkey")) {
                String positionString;
                LinearPositionAnimator animator = new LinearPositionAnimator();
                animator.setNode(getNodeByName(nodeName));
                while ((positionString = stream.readLine()) != null) {
                    positionString = positionString.trim();
                    if (positionString.equals("endlist")) {
                        break;
                    }

                    animator.addAnimationLinePosition(positionString);
                }
                animatorList.add(animator);
            } else {
                if (token.equals("orientationkey")) {
                    String rotationString;
                    LinearRotationAnimator animator = new LinearRotationAnimator();
                    animator.setNode(getNodeByName(nodeName));
                    while ((rotationString = stream.readLine()) != null) {
                        rotationString = rotationString.trim();
                        if (rotationString.equals("endlist")) {
                            break;
                        }

                        animator.addAnimationLineRotation(rotationString);
                    }
                    animatorList.add(animator);
                } else {
                    if (token.equals("endnode")) {
                        break;
                    }
                }
            }
        }

        return animatorList;
    }

    private Node loadGeometry(SceneManager sceneManager, BufferedReader stream) throws IOException {
        String line;
        Node root = null;
        OUTER:
        while ((line = stream.readLine()) != null) {
            line = line.trim();
            if (line.isEmpty()) {
                continue;
            }
            String[] values = StringUtils.split(line, ' ');
            String token = values[0];

            float minx, miny, minz;

            float maxx, maxy, maxz;
            float radius;
            switch (token) {
                case "bmin":
                    minx = parseFloat(values[1]);
                    miny = parseFloat(values[2]);
                    minz = parseFloat(values[3]);
                    break;
                case "bmax":
                    maxx = parseFloat(values[1]);
                    maxy = parseFloat(values[2]);
                    maxz = parseFloat(values[3]);
                    break;
                case "radius":
                    radius = parseFloat(values[1]);
                    break;
                case "endmodelgeom":
                    break OUTER;
                case "node":
                    String nodeType = values[1];
                    String name = values[2];
                    if (nodeType.equals("dummy")) {
                        Node dummy = loadDummy(sceneManager, stream);
                        dummy.setName(name);
                        nodes.put(name, dummy);
                        if (dummy.getParent() == null) {
                            root = dummy;
                        }
                    } else if (nodeType.equals("emitter")) {
                        //skip until endnode
                        System.out.println("Skip emitter");
                        while (true) {
                            line = stream.readLine();
                            if (line.equals("endnode")) {
                                break;
                            }
                        }
                        continue;
                    } else {
                        Node child = loadNode(sceneManager, stream);
                        child.setName(name);
                        nodes.put(name, child);
                        if (child.getParent() == null) {
                            root = child;
                        }
                    }
                    break;
            }
        }
        return root;
    }

    private Node loadDummy(SceneManager sceneManager, BufferedReader stream) throws IOException {
        String line;
        String parentName = null;
        float[] wireColor = null;
        float[] specular = null;
        float shininess = 0;
        float[] position = null;
        float[] orientation = null;

        OUTER:
        while ((line = stream.readLine()) != null) {
            line = line.trim();
            if (line.isEmpty()) {
                continue;
            }
            String[] values = StringUtils.split(line, ' ');
            String token = values[0];
            switch (token) {
                case "parent":
                    parentName = values[1];
                    break;
                case "position":
                    position = getVec3(values);
                    break;
                case "orientation":
                    orientation = getVec4(values);
                    break;
                case "wirecolor":
                    wireColor = getVec3(values);
                    break;
                case "specular":
                    specular = getVec3(values);
                    break;
                case "shininess":
                    shininess = parseFloat(values[1]);
                    break;
                case "endnode":
                    break OUTER;
            }
        }

        Node parent;
        if (parentName == null || parentName.equals("NULL")) {
            parent = null;
        } else {
            parent = nodes.get(parentName);
        }

        Node node = new AnimationNode(sceneManager, parent);
        if (parent != null) {
            parent.addChild(node);
        }

        if (position != null) {
            node.setPosition(position[0], position[1], position[2]);
        }

        if (orientation != null) {
            float x = orientation[0];
            float y = orientation[1];
            float z = orientation[2];
            float angle = orientation[3];
            node.setRotationFromAxisAngle(x, y, z, angle);
        }

        node.setVisible(false);
        return node;
    }

    private Node loadNode(SceneManager sceneManager, BufferedReader stream) throws IOException {
        String line;
        String parentName = null;
        float[] wireColor = null;
        float[] specular = null;
        float shininess = 0;
        float[] position = null;
        float[] orientation = null;
        String texture = null;
        boolean render = false;
        boolean shadow = false;
        float[] diffuse;
        float[] ambient;
        float[] vertices = null;
        int[] indices = null;
        float[] uvs = null;

        OUTER:
        while ((line = stream.readLine()) != null) {
            line = line.trim();
            if (line.isEmpty()) {
                continue;
            }
            String[] values = StringUtils.split(line, ' ');
            String token = values[0];
            switch (token) {
                case "parent":
                    parentName = values[1];
                    break;
                case "position":
                    position = getVec3(values);
                    break;
                case "orientation":
                    orientation = getVec4(values);
                    break;
                case "wirecolor":
                    wireColor = getVec3(values);
                    break;
                case "specular":
                    specular = getVec3(values);
                    break;
                case "shininess":
                    shininess = parseFloat(values[1]);
                    break;
                case "render":
                    if (values[1].equalsIgnoreCase("normal")) {
                        render = true;
                    } else {
                        render = Integer.parseInt(values[1]) == 1;
                    }
                    break;
                case "bitmap":
                    texture = values[1];
                    break;
                case "verts":
                    int vertsCount = Integer.parseInt(values[1]);
                    vertices = new float[vertsCount * 3];
                    for (int i = 0; i < vertsCount; i++) {
                        line = stream.readLine().trim();
                        String[] vertsValues = StringUtils.split(line, ' ');
                        vertices[i * 3 + 0] = parseFloat(vertsValues[0]);
                        vertices[i * 3 + 1] = parseFloat(vertsValues[1]);
                        vertices[i * 3 + 2] = parseFloat(vertsValues[2]);
                    }
                    break;
                case "faces":
                    int facesCount = Integer.parseInt(values[1]);
                    indices = new int[facesCount * 3];
                    for (int i = 0; i < facesCount; i++) {
                        line = stream.readLine().trim();
                        String[] faceValues = StringUtils.split(line, ' ');
                        indices[i * 3 + 0] = Integer.parseInt(faceValues[0]);
                        indices[i * 3 + 1] = Integer.parseInt(faceValues[1]);
                        indices[i * 3 + 2] = Integer.parseInt(faceValues[2]);
                    }
                    break;
                case "tverts":
                    int count = Integer.parseInt(values[1]);
                    uvs = new float[count * 3];
                    for (int i = 0; i < count; i++) {
                        line = stream.readLine().trim();
                        String[] uvsValues = StringUtils.split(line, ' ');
                        uvs[i * 2 + 0] = parseFloat(uvsValues[0]);
                        uvs[i * 2 + 1] = parseFloat(uvsValues[1]);
                    }
                    break;
                case "endnode":
                    break OUTER;
            }
        }

        Node parent;
        if (parentName == null || parentName.equals("NULL")) {
            parent = null;
        } else {
            parent = nodes.get(parentName);
        }

        Node node = new AnimationNode(sceneManager, parent);
        if (parent != null) {
            parent.addChild(node);
        }

        if (position != null) {
            node.setPosition(position[0], position[1], position[2]);
        }

        if (orientation != null) {
            Quaternion q = new Quaternion();
            q.setIdentity();
            float x = orientation[0];
            float y = orientation[1];
            float z = orientation[2];
            float angle = orientation[3];
            // q.rotateByAngleNormalAxis(angle, x, y, z);
            //q.toEuler(orientation);
            //node.setRotationFromAxisAngle(orientation[0], orientation[1], orientation[2]);
            node.setRotationFromAxisAngle(x, y, z, angle);
        }

        node.setVisible(render);
        IndexedGeometry geometry = new IndexedGeometry();
        geometry.init(sceneManager, vertices, uvs, indices);
        node.getGeometryList().add(geometry);
        if (texture != null && !texture.equals("NULL")) {
            Texture loadedTexture = sceneManager.getTextureManager().loadTexture(new File(directory, texture + ".tga"));
            geometry.addTexture(loadedTexture);
        }
        node.setMaterial(new ShaderMaterial(sceneManager, FileUtils.readFileToString(new File("f:\\java\\mydifferentprojects\\jogl\\vertexshader.txt")), FileUtils.readFileToString(new File("f:\\java\\mydifferentprojects\\jogl\\fragmentshader.txt"))));
        return node;
    }

    private float[] getVec3(String[] value) {
        return getVec(value, 1, 3);
    }

    private float[] getVec4(String[] value) {
        return getVec(value, 1, 4);
    }

    private float[] getVec(String[] value, int offset, int count) {
        float[] array = new float[count];
        for (int i = 0; i < count; i++) {
            array[i] = parseFloat(value[offset + i]);
        }
        return array;
    }

}
