package com.jogl;

import com.jogl.engine.mesh.loader.impl.AsciiMdlLoader;
import com.jogl.engine.utils.io.JoglFileInputStream;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import javax.swing.JOptionPane;
import org.apache.commons.lang3.StringUtils;

/**
 * @author Dmitry
 */
public class TestRotationsAndMove extends ISceneHandler {

    @Override
    public void init(MainWithEngine engine) throws IOException {
        File file = new File("G:\\kotor_Extracted\\ascii_model_testing\\dor_lda01_ascii.mdl.ascii");
        AsciiMdlLoader loader = new AsciiMdlLoader();
        engine.node = loader.load(engine.sceneManager, file, new JoglFileInputStream(file));
        engine.sceneManager.getNodes().add(engine.node);
        engine.partNode = engine.sceneManager.getObjectByName("DOR_LDA01");
        engine.node.moveZ(5);
        //engine.node.moveY(-1);

        //engine.node.rotateX((float) Math.toRadians(-90));
        engine.timeScale.setCurrentTime(System.currentTimeMillis());
    }

    private ManipType manipType = ManipType.MOVE;

    @Override
    public void onDisplay(MainWithEngine engine) {
        if (engine.isKeyPressed(KeyEvent.VK_N)) {
            manipType = ManipType.ROTATE;
            System.out.println("Manip type " + manipType);
        }
        if (engine.isKeyPressed(KeyEvent.VK_M)) {
            manipType = ManipType.MOVE;
            System.out.println("Manip type " + manipType);
        }
        if (engine.isKeyPressed(KeyEvent.VK_COMMA)) {
            manipType = ManipType.SCALE;
            System.out.println("Manip type " + manipType);
        }
        if (engine.isKeyPressed(KeyEvent.VK_U)) {
            if (null != manipType) {
                switch (manipType) {
                    case MOVE:
                        engine.node.moveZ(0.5f);
                        break;
                    case ROTATE:
                        engine.node.rotateZ(0.05f);
                        break;
                    case SCALE:
                        engine.node.scaleZ(0.9f);
                        break;
                    default:
                        break;
                }
            }
        }
        if (engine.isKeyPressed(KeyEvent.VK_O)) {
            if (null != manipType) {
                switch (manipType) {
                    case MOVE:
                        engine.node.moveZ(-0.5f);
                        break;
                    case ROTATE:
                        engine.node.rotateZ(-0.05f);
                        break;
                    case SCALE:
                        engine.node.scaleZ(1.1f);
                        break;
                    default:
                        break;
                }
            }
        }
        if (engine.isKeyPressed(KeyEvent.VK_J)) {
            if (null != manipType) {
                switch (manipType) {
                    case MOVE:
                        engine.node.moveX(0.5f);
                        break;
                    case ROTATE:
                        engine.node.rotateX(0.05f);
                        break;
                    case SCALE:
                        engine.node.scaleX(0.9f);
                        break;
                    default:
                        break;
                }
            }
        }

        if (engine.isKeyPressed(KeyEvent.VK_K)) {
            if (null != manipType) {
                switch (manipType) {
                    case MOVE:
                        engine.node.moveY(-0.5f);
                        break;
                    case ROTATE:
                        engine.node.rotateY(-0.05f);
                        break;
                    case SCALE:
                        engine.node.scaleY(0.9f);
                        break;
                    default:
                        break;
                }
            }
        }

        if (engine.isKeyPressed(KeyEvent.VK_L)) {
            if (null != manipType) {
                switch (manipType) {
                    case MOVE:
                        engine.node.moveX(-0.5f);
                        break;
                    case ROTATE:
                        engine.node.rotateX(-0.05f);
                        break;
                    case SCALE:
                        engine.node.scaleX(1.1f);
                        break;
                    default:
                        break;
                }
            }
        }
        if (engine.isKeyPressed(KeyEvent.VK_I)) {
            if (null != manipType) {
                switch (manipType) {
                    case MOVE:
                        engine.node.moveY(0.5f);
                        break;
                    case ROTATE:
                        engine.node.rotateY(0.05f);
                        break;
                    case SCALE:
                        engine.node.scaleY(1.1f);
                        break;
                    default:
                        break;
                }
            }
        }
        if (engine.isKeyPressed(KeyEvent.VK_Y)) {
            processPositionCommand(engine);
        }
    }

    private void processPositionCommand(MainWithEngine engine) {
        String line = JOptionPane.showInputDialog(engine.frame,"Enter command(template:  m/r/s x/y/z value )","");
        if (line==null||line.isEmpty()) {
            System.out.println("You entered empty command");
            return;
        }
        String[] parts = StringUtils.split(line, " ");
        ManipType manipType;
        switch (parts[0].toLowerCase()) {
            case "m":
                manipType = ManipType.MOVE;
                break;
            case "r":
                manipType = ManipType.ROTATE;
                break;
            case "s":
                manipType = ManipType.SCALE;
                break;
            default:
                throw new IllegalArgumentException("Cannot process manipulation type [" + parts[0] + "]");
        }

        String axis = parts[1].toLowerCase();
        if (!(axis.equals("x") || axis.equals("y") || axis.equals("z"))) {
            throw new IllegalArgumentException("Unknow axis");
        }

        float value = Float.parseFloat(parts[2]);
        switch (manipType) {
            case MOVE:
                switch (axis) {
                    case "x":
                        engine.node.setX(value);
                        break;
                    case "y":
                        engine.node.setY(value);
                        break;
                    case "z":
                        engine.node.setZ(value);
                        break;
                }
                break;
            case ROTATE:
                switch (axis) {
                    case "x":
                        engine.node.setRotation(value, 0, 0);
                        break;
                    case "y":
                        engine.node.setRotation(0, value, 0);
                        break;
                    case "z":
                        engine.node.setRotation(0, 0, value);
                        break;
                }
                break;
            case SCALE:
                switch (axis) {
                    case "x":
                        engine.node.setScale(value, 1, 1);
                        break;
                    case "y":
                        engine.node.setScale(1, value, 1);
                        break;
                    case "z":
                        engine.node.setScale(1, 1, value);
                        break;
                }
                break;
        }
    }

    private enum ManipType {
        MOVE, SCALE, ROTATE
    }
}
