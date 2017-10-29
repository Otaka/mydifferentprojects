package com.jogl.engine.mesh.loader.impl.binarymdl;

import com.jogamp.opengl.math.Quaternion;
import com.jogl.engine.SceneManager;
import com.jogl.engine.exceptions.JoglException;
import com.jogl.engine.mesh.loader.MeshLoader;
import com.jogl.engine.node.Node;
import com.jogl.engine.utils.io.*;
import com.jogl.unpack.Unpack;
import java.io.*;
import java.util.*;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * @author Dmitry
 */
public class BinaryPerlMdlLoader extends MeshLoader {

    private static final String[] exts = new String[]{"mdl"};
    private final Map<String, Mesh> nodes = new HashMap<>();
    private File directory;
    private String[] namesStringArray;
    private JoglFileInputStream mdxStream;
    private int numAnimations;
    private List partNames;
    private static final int ROOTNODE = 3;
    private static final int DATAEXTLEN = 62;
    private static final int NODE_HAS_MESH = 0x00000020;
    private static final int NODE_HAS_HEADER = 0x00000001;
    private static final int NODE_HAS_LIGHT = 0x00000002;
    private static final int NODE_HAS_EMITTER = 0x00000004;
    private static final int NODE_HAS_SKIN = 0x00000040;
    private static final int NODE_HAS_DANGLY = 0x00000100;
    private static final int NODE_HAS_AABB = 0x00000200;
    private static final int NODE_HAS_SABER = 0x00000800;
    private final static Map<String, DataArrayInfo> subHeadInfoMap = new HashMap<String, DataArrayInfo>() {
        {
            put("3k1", new DataArrayInfo(92, "3k1", "light_header", "fl*"));
            put("5k1", new DataArrayInfo(224, "5k1", "emitter_header", "l[2]f[3]l[3]Z[32]Z[32]Z[32]Z[64]Z[16]l[2]S[2]l"));
            put("33k1", new DataArrayInfo(332, "33k1", "trimesh_header", "l[5]f[16]lZ[32]Z[32]l[19]f[6]l[13]SSSSSSf[2]ll"));
            put("97k1", new DataArrayInfo(432, "97k1", "skin_header", "l[5]f[16]lZ[32]Z[32]l[19]f[6]l[13]SSSSSSf[2]lll[16]S*"));
            put("289k1", new DataArrayInfo(360, "289k1", "dangly_header", "l[5]f[16]lZ[32]Z[32]l[19]f[6]l[13]SSSSSSf[2]lll[3]f[3]l"));
            put("545k1", new DataArrayInfo(336, "545k1", "walkmesh_header", "l[5]f[16]lZ[32]Z[32]l[19]f[6]l[13]SSSSSSf[2]lll"));
            put("2081k1", new DataArrayInfo(336, "2081k1", "2081_header", "l[5]f[16]lZ[32]Z[32]l[19]f[6]l[13]SSSSSSf[2]lll*"));
        }
    };

    DataArrayInfo[] darrayInfoMap = new DataArrayInfo[]{
        new DataArrayInfo(32, "0darray", "faces", "fffflssssss", 3, 2),
        new DataArrayInfo(4, "1darray", "pntr_to_vert_num", "l", 31, 30),
        new DataArrayInfo(4, "2darray", "pntr_to_vert_loc", "l", 34, 33),
        new DataArrayInfo(4, "3darray", "array3", "l", 37, 36),
        new DataArrayInfo(2, "4darray", "vertindexes", "s*", -1, -1),
        new DataArrayInfo(4, "5darray", "bonemap", "f", 80, 79),
        new DataArrayInfo(16, "6darray", "qbones", "f[4]", 82, 81),
        new DataArrayInfo(12, "7darray", "tbones", "f[3]", 85, 84),
        new DataArrayInfo(4, "8darray", "array8", "SS", 88, 87),
        new DataArrayInfo(16, "9darray", "constraints+", "f[4]", 75, 74),
        new DataArrayInfo(40, "10darray", "aabb", "ffffffllll", -1, 74)
    };

    private static final Map<Integer, String> classificationMap = new HashMap<Integer, String>() {
        {
            put(0x01, "Effect");
            put(0x02, "Tile");
            put(0x04, "Character");
            put(0x08, "Door");
            put(0x20, "Placeable");
            put(0x00, "Other");
        }
    };

    private int trueNodeNum = 0;

    public BinaryPerlMdlLoader() {
    }

    @Override
    public String[] getExtensions() {
        return exts;
    }

    @Override
    public boolean isMatch(JoglFileInputStream stream) {
        try {
            stream.mark(20);
            int value = stream.readInt();
            stream.reset();
            return value == 0;
        } catch (IOException ex) {
            throw new JoglException("Error while reading stream", ex);
        }
    }

    private JoglFileInputStream getMdxStream(File mdlFile) throws FileNotFoundException {
        JoglFileInputStream stream = new JoglFileInputStream(new File(FilenameUtils.removeExtension(mdlFile.getAbsolutePath()) + ".mdx"));
        return stream;
    }

    private List readAndUnpack(String pattern, JoglFileInputStream stream, int size) throws IOException {
        byte[] buffer = new byte[size];
        stream.getAbsolutePosition();
        stream.readBytes(buffer);
        NwnByteArrayInputStream nwStream = new NwnByteArrayInputStream(buffer);
        return Unpack.unpack(pattern, nwStream);
    }

    private List readAndUnpack(String pattern, JoglFileInputStream stream, int size, String label) throws IOException {
        byte[] buffer = new byte[size];
        stream.getAbsolutePosition();
        stream.readBytes(buffer, label + "${" + pattern + "}");
        NwnByteArrayInputStream nwStream = new NwnByteArrayInputStream(buffer);
        return Unpack.unpack(pattern, nwStream);
    }

    private String mulString(String str, int count) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < count; i++) {
            sb.append(str);
        }
        return sb.toString();
    }

    @Override
    public Node load(SceneManager sceneManager, File file, JoglFileInputStream stream) throws IOException {
        int animRoot = 5;
        stream.storeFileParsingDescriptionInfo(new File(file.getAbsolutePath() + ".descr"));
        mdxStream = getMdxStream(file);
        stream.rememberFileOffset();
        int magic = stream.readInt("magic");
        if (magic != 0) {
            throw new IllegalArgumentException();
        }

        stream.setAbsolutePosition(12);
        List geoHeaderUnpacked = readAndUnpack("llZ[32]lllllllllCCCC", stream, 80, "geoModelHeader");
        String modelName = (String) geoHeaderUnpacked.get(2);
        int rootNode = (int) geoHeaderUnpacked.get(3);
        int totalNumNodes = (int) geoHeaderUnpacked.get(4);
        short modelType = (short) geoHeaderUnpacked.get(12);

        List modelHeader = readAndUnpack("CCCClllllffffffffZ[32]", stream, 88, "modelHeader");
        String classification = classificationMap.get(((int) (short) modelHeader.get(0)));
        int animStart = (int) modelHeader.get(5);
        numAnimations = (int) modelHeader.get(6);
        float[] bmin = new float[]{(float) modelHeader.get(9), (float) modelHeader.get(10), (float) modelHeader.get(11)};
        float[] bmax = new float[]{(float) modelHeader.get(12), (float) modelHeader.get(13), (float) modelHeader.get(14)};
        float radius = (float) modelHeader.get(15);
        float animationScale = (float) modelHeader.get(16);
        String superModel = (String) modelHeader.get(17);

        stream.setAbsolutePosition(180);//nameHeader
        List nameIndexesHeader = readAndUnpack("lllllll", stream, 28, "nameIndexesHeader");
        int positionOfTheNameIndexes = (int) nameIndexesHeader.get(4) + 12;
        int nameIndexesBufferSize = 4 * (int) nameIndexesHeader.get(5);
        List nameIndexesUnpacked = readAndUnpack("l*", stream, nameIndexesBufferSize, "nameIndexes");

        int partNamesSize = (int) modelHeader.get(animRoot) - ((int) nameIndexesHeader.get(4) + (4 * (int) nameIndexesHeader.get(5)));
        partNames = readAndUnpack(mulString("Z*", (int) nameIndexesHeader.get(5)), stream, partNamesSize, "partNames");
        trueNodeNum = 0;

        System.out.println("Model " + modelName);
        System.out.println(" RootNode " + rootNode);
        System.out.println(" TotalNumNodes " + totalNumNodes);
        System.out.println(" Classification " + classification);

        Mesh mesh = getNodes("nodes", stream, "NULL", rootNode, false);
        int animHeaderSize = 56;
        if (numAnimations != 0) {
            int animationBufferStart = animStart + 12;
            stream.setAbsolutePosition(animationBufferStart);
            int bufferSize = 4 * numAnimations;
            List animIndexes = readAndUnpack("l*", stream, bufferSize, "Animation indexes");
            for (int i = 0; i < numAnimations; i++) {
                int offset = (int) animIndexes.get(i) + 12;
                stream.setAbsolutePosition(offset);
                List animationGeoHeader = readAndUnpack("llZ[32]lllllllllCCCC", stream, 80, "Animation " + i + " geo header");
                String animationName = (String) animationGeoHeader.get(2);
                System.out.println("Loading animation " + animationName);
                List animationHeader = readAndUnpack("ffZ[32]llll", stream, animHeaderSize, modelName);
                float length = (float) animationHeader.get(0);
                float transTime = (float) animationHeader.get(1);
                String animationRoot = (String) animationHeader.get(2);
                int eventsLocation = (int) animationHeader.get(3);
                int eventsNum = (int) animationHeader.get(4);
                if (eventsNum != 0) {
                    int animEventBufferSize = 36;
                    for (int j = 0; j < eventsNum; j++) {
                        List animEvent = readAndUnpack("fZ[32]", stream, animEventBufferSize, "Animation event");
                        float time = (float) animEvent.get(0);
                        String soundName = (String) animEvent.get(1);
                    }
                }

                int dataOffset = (int) animationGeoHeader.get(ROOTNODE);
                getNodes(modelName, stream, superModel, dataOffset, true);
                loadAnimation(mesh, animationName, stream, dataOffset);
            }
        }

        stream.closeFileParsingDescriptionInfo();
        return null;
    }

    private void loadAnimation(Mesh mesh, String animationName, JoglFileInputStream stream, int dataOffset) throws IOException {
        stream.setAbsolutePosition(dataOffset + 12);
        short nodeIndex = stream.readShort();
        String nodeName = (String) partNames.get(nodeIndex);
        System.out.println("Node name " + nodeName);
        int nodeHeadersSize = 80;
        stream.setAbsolutePosition(dataOffset + 12);
        List nodeHeader = readAndUnpack("SSSSllffffffflllllllll", stream, nodeHeadersSize, "nodeAnimationHeader_" + nodeName + "_" + animationName);
        int childrenLocation = (int) nodeHeader.get(13);
        int childrenCount = (int) nodeHeader.get(14);
        int controllerLocation = (int) nodeHeader.get(16);
        int controllerNum = (int) nodeHeader.get(17);
        int controllerDataLocation = (int) nodeHeader.get(19);
        int controllerDataNum = (int) nodeHeader.get(20);
        List<ControllerCookedData> controllerCookedDataList = null;
        List controllerDataUnpacked;
        List<AnimationChunk> animationChunks = new ArrayList<>();
        mesh.setAnimationChunks(animationChunks);
        List controllersUnpacked = null;
        if (controllerNum > 0) {
            controllerCookedDataList = new ArrayList<>(controllerNum);
            stream.setAbsolutePosition(controllerLocation + 12);
            controllersUnpacked = readAndUnpack(mulString("lssssCCCC", controllerNum), stream, 16 * controllerNum, "controllerUnpacked_" + nodeName + "_" + animationName);
            for (int i = 0; i < controllerNum; i++) {
                int type = (int) controllersUnpacked.get(i * 9 + 0);
                short[] data = new short[]{
                    (short) controllersUnpacked.get(i * 9 + 1),
                    (short) controllersUnpacked.get(i * 9 + 2),
                    (short) controllersUnpacked.get(i * 9 + 3),
                    (short) controllersUnpacked.get(i * 9 + 4),
                    (short) controllersUnpacked.get(i * 9 + 5),
                    (short) controllersUnpacked.get(i * 9 + 6), // (short) controllersUnpacked.get(i * 9 + 7),
                //(short) controllersUnpacked.get(i * 9 + 8)
                };

                ControllerCookedData cookedData = new ControllerCookedData(type, data);
                controllerCookedDataList.add(cookedData);
            }
        }

        if (controllerDataNum > 0) {
            //something wrong here
            StringBuilder templateString = new StringBuilder();
            for (int i = 0; i < controllerNum; i++) {
                ControllerCookedData cookedData = controllerCookedDataList.get(i);
                templateString.append(mulString("f", cookedData.getValues()[1]));
                short[] values = cookedData.getValues();
                if (cookedData.getValues()[0] != 128) {
                    if (cookedData.getType() == 20 && values[4] == 2) {
                        templateString.append(mulString("L", values[1]));
                    } else if (cookedData.getType() == 8 && values[4] > 16) {
                        templateString.append(mulString("f", values[1] * ((values[4] - 16) * 3)));
                    } else {
                        templateString.append(mulString("f", values[1] * values[4]));
                    }
                } else {
                    templateString.append(mulString("s", ((values[1] * values[4]) * 2)));
                }
            }
            stream.setAbsolutePosition(controllerDataLocation + 12);
            System.out.println("Controller data position 0x" + (Integer.toHexString(controllerDataLocation + 12)) + "  size = " + (4 * controllerDataNum));
            controllerDataUnpacked = readAndUnpack(templateString.toString(), stream, 4 * controllerDataNum, "ControllerData_" + nodeName + "_" + animationName);
            System.out.println("  ControllerData " + StringUtils.join(controllerDataUnpacked, ','));
        }

        //cook the controllers
        if (controllersUnpacked != null) {
            /*for (int i = 0; i < controllerNum; i++) {

             int controllerType = (int) controllersUnpacked.get(i * 9 + 0);
             short controllerInfo = (short) controllersUnpacked.get(i * 9 + 1);
             short dataRows = (short) controllersUnpacked.get(i * 9 + 2 - 1);
             short timeStart = (short) controllersUnpacked.get(i * 9 + 3 - 1);
             short dataStart = (short) controllersUnpacked.get(i * 9 + 4 - 1);
             short dataColumns = (short) controllersUnpacked.get(i * 9 + 5 - 1);
             if (!(controllerType == 8 || controllerType == 20)) {
             continue;
             }
             // check for controller type 20 and column count 2:
             // special compressed quaternion, only read one value here
             if (controllerType == 20 && dataColumns == 2) {
             dataColumns = 1;
             }

             for (int j = 0; j < dataRows; j++) {
             AnimationChunk animChunk = new AnimationChunk();
             animChunk.setTime((float) controllerDataUnpacked.get(timeStart + j));
             float[] values = new float[dataColumns];
             for (int k = 0; k < dataColumns; k++) {
             values[k] = (float) controllerDataUnpacked.get(dataStart + k + (j * dataColumns));
             }
             if (controllerType == 8) {
             animChunk.setPosition(values);
             } else if (controllerType == 20) {
             animChunk.setRotation(values);
             }
             animationChunks.add(animChunk);
             }
             }*/
        }

        if (childrenCount > 0) {
            stream.setAbsolutePosition(childrenLocation + 12);
            int[] childrenLocationPositions = new int[childrenCount];
            for (int i = 0; i < childrenCount; i++) {
                childrenLocationPositions[i] = stream.readInt();
            }
            for (int i = 0; i < childrenCount; i++) {
                Mesh child = mesh.getChildren()[i];
                loadAnimation(child, animationName, stream, childrenLocationPositions[i]);
            }
        }
    }

    private Mesh getNodes(String tree, JoglFileInputStream stream, String parent, int startNode, boolean anim) throws IOException {
        int uOffset = -2;
        Mesh node = new Mesh();
        trueNodeNum++;
        int nodeHeadersSize = 80;
        stream.setAbsolutePosition(startNode + 16);
        short nodeIndex = stream.readShort();
        String nodeName = (String) partNames.get(nodeIndex);

        stream.setAbsolutePosition(startNode + 12);
        List nodeHeader = readAndUnpack("SSSSllffffffflllllllll", stream, nodeHeadersSize, "nodeHeader");
        int nodeType = (short) nodeHeader.get(0);
        int superNode = (short) nodeHeader.get(1);
        int childrenLocation = (int) nodeHeader.get(13);
        int childrenCount = (int) nodeHeader.get(14);
        int controllerLocation = (int) nodeHeader.get(16);
        int controllerNum = (int) nodeHeader.get(17);
        int controllerDataLocation = (int) nodeHeader.get(19);
        int controllerDataNum = (int) nodeHeader.get(20);
        List<ControllerCookedData> controllerCookedDataList = null;
        List controllerDataUnpacked = null;
        List<AnimationChunk> animationChunks = new ArrayList<>();
        List controllersUnpacked = null;
        node.setAnimationChunks(animationChunks);
        // node.setPosition(new float[]{(Float) nodeHeader.get(6), (Float) nodeHeader.get(7), (Float) nodeHeader.get(8)});
        // node.setRotation(new float[]{(Float) nodeHeader.get(12), (Float) nodeHeader.get(9), (Float) nodeHeader.get(10), (Float) nodeHeader.get(11)});
        System.out.println("Part " + nodeName + "\n  ChildrenCount " + childrenCount + "\n  ControllerNum " + controllerNum);
        node.setName(nodeName);
        if (anim) {
            if (controllerNum > 0) {
                controllerCookedDataList = new ArrayList<>(controllerNum);
                if (tree.startsWith("anims")) {
                    throw new IllegalStateException("Not implemented");
                }
                stream.setAbsolutePosition(controllerLocation + 12);
                controllersUnpacked = readAndUnpack(mulString("lssssCCCC", controllerNum), stream, 16 * controllerNum, "controllerUnpacked_" + nodeName);
                for (int i = 0; i < controllerNum; i++) {
                    int type = (int) controllersUnpacked.get(i * 9 + 0);
                    short[] data = new short[]{
                        (short) controllersUnpacked.get(i * 9 + 1),
                        (short) controllersUnpacked.get(i * 9 + 2),
                        (short) controllersUnpacked.get(i * 9 + 3),
                        (short) controllersUnpacked.get(i * 9 + 4),
                        (short) controllersUnpacked.get(i * 9 + 5),
                        (short) controllersUnpacked.get(i * 9 + 6),
                        (short) controllersUnpacked.get(i * 9 + 7),
                        (short) controllersUnpacked.get(i * 9 + 8)
                    };

                    ControllerCookedData cookedData = new ControllerCookedData(type, data);
                    controllerCookedDataList.add(cookedData);
                }
            }

            if (controllerDataNum > 0) {
                if (tree.startsWith("anims")) {
                    throw new IllegalStateException("Not implemented");
                }
                //something wrong here
                StringBuilder templateString = new StringBuilder();
                for (int i = 0; i < controllerNum; i++) {
                    ControllerCookedData cookedData = controllerCookedDataList.get(i);
                    templateString.append(mulString("f", cookedData.getValues()[1]));
                    short[] values = cookedData.getValues();
                    if (cookedData.getValues()[0] != 128) {
                        if (cookedData.getType() == 20 && values[4] == 2) {
                            templateString.append(mulString("L", values[1]));
                        } else if (cookedData.getType() == 8 && values[4] > 16) {
                            templateString.append(mulString("f", values[1] * ((values[4] - 16) * 3)));
                        } else {
                            templateString.append(mulString("f", values[1] * values[4]));
                        }
                    } else {
                        templateString.append(mulString("s", ((values[1] * values[4]) * 2)));
                    }
                }
                stream.setAbsolutePosition(controllerDataLocation + 12);
                controllerDataUnpacked = readAndUnpack(templateString.toString(), stream, 4 * controllerDataNum, "ControllerData_" + nodeName);
                System.out.println("  ControllerData " + StringUtils.join(controllerDataUnpacked, ','));
            }

            //cook the controllers
            if (controllersUnpacked != null) {
                for (int i = 0; i < controllerDataNum; i++) {
                    if ((i * 9 + 0) >= controllersUnpacked.size()) {
                        break;
                    }
                    //get the controller info
                    int controllerType = (int) controllersUnpacked.get(i * 9 + 0);
                    short controllerInfo = (short) controllersUnpacked.get(i * 9 + 1);
                    short dataRows = (short) controllersUnpacked.get(i * 9 + 2);
                    short timeStart = (short) controllersUnpacked.get(i * 9 + 3);
                    short dataStart = (short) controllersUnpacked.get(i * 9 + 4);
                    short dataColumns = (short) controllersUnpacked.get(i * 9 + 5);
                    if (!(controllerType == 8 || controllerType == 20)) {
                        continue;
                    }
                    // check for controller type 20 and column count 2:
                    // special compressed quaternion, only read one value here
                    if (controllerType == 20 && dataColumns == 2) {
                        dataColumns = 1;
                    }

                    for (int j = 0; j < dataRows; j++) {
                        AnimationChunk animChunk = new AnimationChunk();
                        animChunk.setTime((float) controllerDataUnpacked.get(timeStart + j));
                        Object[] values = new Object[dataColumns];
                        for (int k = 0; k < dataColumns; k++) {
                            values[k] = controllerDataUnpacked.get(dataStart + k + (j * dataColumns));
                        }
                        if (controllerType == 8) {
                            float[] position = new float[values.length];
                            if (values.length == 3) {
                                for (int k = 0; k < values.length; k++) {
                                    position[k] = (float) values[k];
                                }
                                animChunk.setPosition(position);
                            }
                        } else if (controllerType == 20) {
                            if (values.length == 1 || values[0] instanceof Long) {
                                Quaternion quaternion = convertPackedQuaternion((Long) values[0]);
                                animChunk.setRotation(quaternion);
                            } else {
                                throw new RuntimeException("Strange quaternion in anim chunks [" + values + "]");
                            }

                        }
                        animationChunks.add(animChunk);
                    }
                }
            }
        }

        List subheadData = null;
        //check the "node type" and read in the subheader for it
        if (nodeType != 1) {
            int nodeSubHeaderOffset = startNode + 92;
            DataArrayInfo subheadInfo = subHeadInfoMap.get("" + nodeType + "k1");
            if (subheadInfo == null) {
                throw new IllegalArgumentException("Cannot process nodeType " + nodeType);
            }
            stream.setAbsolutePosition(nodeSubHeaderOffset);
            subheadData = readAndUnpack(subheadInfo.getTemplate(), stream, subheadInfo.getSize(), "subHeadData_" + nodeName);
        }

        if (nodeType == 3) {//light
            throw new IllegalStateException("Not implemented yet. Light node type");
        }

        if (nodeType == 5) {//emitter
            //throw new IllegalStateException("Not implemented yet. Emitter node type");
        }

        int facesLoc;
        int facesNum = -1;
        float[] bboxmin;
        float[] bboxmax;
        float radius;
        float[] average;
        float[] diffuse;
        float[] ambient;
        String bitmap;
        String bitmap2;
        int vertNumLoc;
        int vertLocLoc;
        int mdxDataSize = 0;
        int vertCoordNum = 0;
        int textureNum;
        boolean shadow;
        boolean visible;
        int mdxDataLocation = -1;
        int vertCoordLocation = -1;
        int bonesLocation = -1;
        int bonesNum = 0;
        float[] vertCoords;
        Map<String, List> nameToDArray = new HashMap<>();
        if ((nodeType & NODE_HAS_MESH) != 0) {
            facesLoc = (int) subheadData.get(2);
            facesNum = (int) subheadData.get(3);
            bboxmin = new float[]{(float) subheadData.get(5), (float) subheadData.get(6), (float) subheadData.get(7)};
            bboxmax = new float[]{(float) subheadData.get(8), (float) subheadData.get(9), (float) subheadData.get(10)};
            radius = (float) subheadData.get(11);
            average = new float[]{(float) subheadData.get(12), (float) subheadData.get(13), (float) subheadData.get(14)};
            diffuse = new float[]{(float) subheadData.get(15), (float) subheadData.get(16), (float) subheadData.get(17)};
            ambient = new float[]{(float) subheadData.get(18), (float) subheadData.get(19), (float) subheadData.get(20)};
            bitmap = (String) subheadData.get(22);
            bitmap2 = (String) subheadData.get(23);
            vertNumLoc = (int) subheadData.get(30);
            vertLocLoc = (int) subheadData.get(33);
            mdxDataSize = (int) subheadData.get(49);
            vertCoordNum = (short) subheadData.get(62);
            textureNum = (short) subheadData.get(63);
            shadow = ((short) subheadData.get(65) & 256) != 0;
            visible = ((short) subheadData.get(66) & 256) != 0;
            mdxDataLocation = (int) subheadData.get(72 + uOffset);
            vertCoordLocation = (int) subheadData.get(73 + uOffset);
            if (nodeType == 97) {
                bonesLocation = (int) subheadData.get(77 + uOffset);
                bonesNum = (int) subheadData.get(78 + uOffset);
            }
            node.setBboxmax(bboxmax);
            node.setBboxmin(bboxmin);
            node.setBitmap(bitmap);
            node.setBonesNumber(bonesNum);
            node.setRadius(radius);
            node.setShadow(shadow);
            node.setVisible(visible);
            System.out.println("  Bitmap " + bitmap + "\n  Visible " + visible + "\n  Shadow " + shadow);
        }

        if ((nodeType & NODE_HAS_SABER) != 0) {
            throw new IllegalStateException("Not implemented yet. Mesh has saber");
        } else if ((nodeType & NODE_HAS_MESH) != 0 && vertCoordNum > 0) {
            stream.setAbsolutePosition(vertCoordLocation + 12);
            /*vertCoords = new float[vertCoordNum * 3];
             for (int i = 0; i < vertCoords.length; i++) {
             vertCoords[i] = stream.readFloat();
             }*/
        }

        if ((nodeType & NODE_HAS_MESH) != 0) {
            for (int i = 0; i < 10; i++) {
                int offset = 0;
                if (i >= 5) {
                    offset = uOffset;
                }
                int darrayNum = darrayInfoMap[i].getNum() + offset;

                if (darrayNum >= 0 && (subheadData.size() > darrayNum) && ((int) subheadData.get(darrayNum)) != 0 && i != 4) {
                    if (i == 5 && (nodeType & NODE_HAS_DANGLY) != 0) {
                        continue;
                    }
                    if (i == 9 && (nodeType & NODE_HAS_DANGLY) == 0) {
                        continue;
                    }
                    int darrayLocation = darrayInfoMap[i].getLoc() + offset;
                    int calculatedPosition = (int) subheadData.get(darrayLocation) + 12;
                    stream.setAbsolutePosition(calculatedPosition);
                    int bufferLength = ((int) subheadData.get(darrayNum + offset)) * darrayInfoMap[i].getSize();
                    String unpackPattern = mulString(darrayInfoMap[i].getTemplate(), (int) subheadData.get(darrayNum + offset));
                    List unpackedDarray = readAndUnpack(unpackPattern, stream, bufferLength, "darray_" + darrayInfoMap[i].getName());
                    nameToDArray.put(darrayInfoMap[i].getName(), unpackedDarray);
                }
            }

            //fill darray4 that because it did not filled in previous step
            if ((int) subheadData.get(darrayInfoMap[2].getNum()) != 0) {
                int pos = (int) nameToDArray.get(darrayInfoMap[2].getName()).get(0) + 12;
                stream.setAbsolutePosition(pos);
                int bufferSize = (int) nameToDArray.get(darrayInfoMap[1].getName()).get(0) * darrayInfoMap[4].getSize();
                List unpackedDArray = readAndUnpack(darrayInfoMap[4].getTemplate(), stream, bufferSize, "darrayData_4_");
                nameToDArray.put(darrayInfoMap[4].getName(), unpackedDArray);
            }

            //prepare faces list
            short[] facesBuffer = new short[facesNum * 3];
            List unpackedDArray0 = nameToDArray.get(darrayInfoMap[0].getName());
            System.out.println("  Faces[" + facesNum + "] ");
            for (int i = 0; i < facesNum; i++) {
                int t = i * 11;
                facesBuffer[i * 3 + 0] = (short) unpackedDArray0.get(t + 8);
                facesBuffer[i * 3 + 1] = (short) unpackedDArray0.get(t + 9);
                facesBuffer[i * 3 + 2] = (short) unpackedDArray0.get(t + 10);
                /* System.out.print("    ");
                 System.out.print(facesBuffer[i * 3 + 0]);
                 System.out.print(' ');
                 System.out.print(facesBuffer[i * 3 + 1]);
                 System.out.print(' ');
                 System.out.print(facesBuffer[i * 3 + 2]);
                 System.out.println(' ');*/
            }
            node.setFaces(facesBuffer);
            node.setFacesNumber(facesNum);
            // System.out.println();
        }

        //if we have nodetype 97 (skin mesh node) cook the bone map stored in data array 5
        if ((nodeType & NODE_HAS_SKIN) != 0) {
//TODO:            
//throw new IllegalStateException("Not implemented yet");
        }

        //if we have a non-saber mesh node then we have MDX data to read in
        if (((nodeType & NODE_HAS_MESH) != 0) && ((nodeType & NODE_HAS_SABER) == 0) && (vertCoordNum > 0)) {
            int bufferSize = mdxDataSize * (vertCoordNum + 1);
            mdxStream.setAbsolutePosition(mdxDataLocation);
            List verticesList = readAndUnpack("f*", mdxStream, bufferSize, "verticesList_" + nodeName);
            float[] verticesCoordsBuffer = new float[vertCoordNum * 3];

            int temp = mdxDataSize / 4;// divide by 4 because the data is unpacked
            boolean hasTextureCoords = false;
            float[] textureCoords = null;
            if ((short) subheadData.get(DATAEXTLEN + 1) != 0) {
                textureCoords = new float[vertCoordNum * 2];
                hasTextureCoords = true;
            }
            System.out.println("  VertCoords");
            for (int i = 0; i < vertCoordNum; i++) {
                verticesCoordsBuffer[i * 3 + 0] = (float) verticesList.get(i * temp + 0);
                verticesCoordsBuffer[i * 3 + 1] = (float) verticesList.get(i * temp + 1);
                verticesCoordsBuffer[i * 3 + 2] = (float) verticesList.get(i * temp + 2);
                /* System.out.print("    ");
                 System.out.print(verticesCoordsBuffer[i * 3 + 0]);
                 System.out.print(' ');
                 System.out.print(verticesCoordsBuffer[i * 3 + 1]);
                 System.out.print(' ');
                 System.out.print(verticesCoordsBuffer[i * 3 + 2]);
                 System.out.println();*/
                if (hasTextureCoords) {
                    textureCoords[i * 2 + 0] = (float) verticesList.get(i * temp + 6);
                    textureCoords[i * 2 + 1] = (float) verticesList.get(i * temp + 7);
                }
            }

            node.setVertexCoords(verticesCoordsBuffer);
            node.setTextureCoordinate(textureCoords);
            node.setVertexNum(bonesNum);

            /* if (hasTextureCoords) {
             System.out.println("  Texture coordinates");
             for (int i = 0; i < vertCoordNum; i++) {
             System.out.print("    ");
             System.out.print(textureCoords[i * 2 + 0]);
             System.out.print(' ');
             System.out.print(textureCoords[i * 2 + 1]);
             System.out.println();
             }
             }*/
        }

        if ((nodeType & NODE_HAS_SABER) != 0) {
            throw new IllegalStateException("Not implemented yet");
        }

        if (childrenCount > 0) {
            stream.setAbsolutePosition(childrenLocation + 12);
            int[] childrenLocationPositions = new int[childrenCount];
            for (int i = 0; i < childrenCount; i++) {
                childrenLocationPositions[i] = stream.readInt();
            }
            Mesh[] children = new Mesh[childrenCount];
            for (int i = 0; i < childrenCount; i++) {
                Mesh child = getNodes(tree, stream, (String) partNames.get(nodeIndex), childrenLocationPositions[i], anim);
                children[i] = child;
                child.setParent(node);
            }
            node.setChildren(children);
        }
        return node;
    }

    protected static Quaternion convertPackedQuaternion(long packedQuaternion) {
        double x, y, z, w;
        x = (1.0 - ((packedQuaternion & 0x7ff) / 1023.0));
        y = (1.0 - (((packedQuaternion >> 11) & 0x7ff) / 1023.0));
        z = 1.0 - ((packedQuaternion >> 22) / 511.0);
        double temp = (x * x) + (y * y) + (z * z);
//!tx=-0.948191593352884                                                                                                                                    
//!ty=-0.952101661779081                                                                                                                                    
//!tz=-99.0958904109589                                                                                                                                     
//!ttemp=9821.80106121284  
//-0.00956754544780686 -0.00960699924346792 -0.99990807959514 0
        if (temp < 1.0) {
            w = -Math.sqrt(1.0 - temp);
        } else {
            temp = Math.sqrt(temp);
            x = x / temp;
            y = y / temp;
            z = z / temp;
            w = 0;
        }

        Quaternion q = new Quaternion((float) x, (float) y, (float) z, (float) w);
        return q;
    }
}
