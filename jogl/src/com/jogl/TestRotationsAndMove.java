package com.jogl;

import com.jogl.engine.math.Vector4;
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
        engine.node.move(0, 0, 5);
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
                        engine.node.move(0, 0, 0.5f);
                        ;
                        break;
                    case ROTATE:
                        engine.node.turn(0, 0, 0.5f, false);
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
                        engine.node.move(0, 0, -0.5f);
                        break;
                    case ROTATE:
                        engine.node.turn(0, 0, -0.05f, false);
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
                        engine.node.move(0.5f, 0, 0);
                        break;
                    case ROTATE:
                        engine.node.turn(0.05f, 0, 0, false);
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
                        engine.node.move(0, -0.5f, 0);
                        break;
                    case ROTATE:
                        engine.node.turn(0, -0.05f, 0, false);
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
                        engine.node.move(-0.5f, 0, 0);
                        break;
                    case ROTATE:
                        engine.node.turn(0, -0.05f, 0, false);
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
                        engine.node.move(0, 0.5f, 0);
                        break;
                    case ROTATE:
                        engine.node.turn(0, 0.05f, 0, false);
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
        String line = JOptionPane.showInputDialog(engine.frame, "Enter command(template:  m/r/s x/y/z value )", "");
        if (line == null || line.isEmpty()) {
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
                        Vector4 pos = engine.node.getLocalPosition();
                        engine.node.setLocalPosition(value, pos.getY(), pos.getZ());
                        break;
                    case "y":
                        Vector4 pos2 = engine.node.getLocalPosition();
                        engine.node.setLocalPosition(pos2.getX(), value, pos2.getZ());
                        break;
                    case "z":
                        Vector4 pos3 = engine.node.getLocalPosition();
                        engine.node.setLocalPosition(pos3.getX(), pos3.getY(), value);
                        break;
                }
                break;
            case ROTATE:
                switch (axis) {
                    case "x":
                        engine.node.setLocalRotation(value, 0, 0);
                        break;
                    case "y":
                        engine.node.setLocalRotation(0, value, 0);
                        break;
                    case "z":
                        engine.node.setLocalRotation(0, 0, value);
                        break;
                }

                break;
        }
    }

    private enum ManipType {
        MOVE, SCALE, ROTATE
    }
}
