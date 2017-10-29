package com.jogl;

import com.jogamp.opengl.*;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.util.Animator;
import com.jogl.engine.SceneManager;
import com.jogl.engine.mesh.loader.impl.*;
import com.jogl.engine.node.Camera;
import com.jogl.engine.node.Node;
import com.jogl.engine.utils.io.JoglFileInputStream;
import com.jogl.gui.ManipFrame;
import com.jogl.gui.OnLoadFile;
import com.swingson.SwingsonGuiBuilder;
import java.awt.Frame;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import javax.swing.SwingUtilities;

public class MainWithEngine extends KeyAdapter implements GLEventListener {

    private static Animator sceneAnimator;
    public SceneManager sceneManager;

    public static GLCanvas canvas;
    long time = 0;
    int counter = 0;

    public final BitSet keySet = new BitSet();
    public Node node;
    public Node partNode;

    public Camera camera;
    public File fileToLoad = null;
    public List<LinearPositionAnimator> animators = new ArrayList<>();
    public TimeScale timeScale = new TimeScale(0);
    public static Frame frame;
    public ISceneHandler iSceneHandler = new RotationAnimatorTestSceneHandler();

    public static void main(String[] args) {
        System.out.println("Start application");
        SwingsonGuiBuilder.setWindowsLookAndFeel();
        frame = new Frame("Simple JOGL Application");
        GLCapabilities cap = new GLCapabilities(GLProfile.getMaxProgrammable(true));
        cap.setDoubleBuffered(true);
        cap.setDepthBits(16);
        cap.setSampleBuffers(true);
        cap.setNumSamples(4);
        cap.setHardwareAccelerated(true);
        canvas = new GLCanvas(cap);
        canvas.addGLEventListener(new MainWithEngine());
        frame.add(canvas);
        frame.setSize(800, 600);
        canvas.setFocusable(true);
        canvas.requestFocus();
        sceneAnimator = new Animator(canvas);
        sceneAnimator.setRunAsFastAsPossible(true);
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                // Run this on another thread than the AWT event queue to
                // make sure the call to Animator.stop() completes before
                // exiting
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        sceneAnimator.stop();
                        System.exit(0);
                    }
                }).start();
            }
        });
        // Center frame
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        sceneAnimator.start();

    }

    private void loadNewFile(File file) {
        fileToLoad = file;
    }

    @Override
    public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
    }

    @Override
    public void init(GLAutoDrawable drawable) {
        try {
            SwingUtilities.invokeLater(new Runnable() {

                @Override
                public void run() {
                    ManipFrame manipFrame = new ManipFrame();
                    manipFrame.pack();
                    manipFrame.setVisible(true);
                    manipFrame.setOnLoadFile(new OnLoadFile() {
                        @Override
                        public void load(File file) {
                            loadNewFile(file);
                        }
                    });
                }
            });

            GL3 gl = (GL3) drawable.getGL();
            canvas.addKeyListener(this);
            sceneManager = new SceneManager(gl);
            camera = new Camera(sceneManager);
            //camera.setPositionAndLook(0, 0, 0.1f, 0, 0, 0, 0, 1, 0);
            sceneManager.setActiveCamera(camera);
            iSceneHandler.init(this);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public void display(GLAutoDrawable drawable) {
        timeScale.setCurrentTime(System.currentTimeMillis());
        if (fileToLoad != null) {
            sceneManager.getNodes().clear();
            AsciiMdlLoader loader = new AsciiMdlLoader();
            try {
                node = loader.load(sceneManager, fileToLoad, new JoglFileInputStream(fileToLoad));
                sceneManager.getNodes().add(node);
                node.moveZ(10);
                node.rotateX((float) Math.toRadians(-90));

            } catch (Exception ex) {
                ex.printStackTrace();
            }
            fileToLoad = null;
        }

        long currentTime = System.currentTimeMillis();
        if ((currentTime - time) > 1000) {
            frame.setTitle("FPS = " + counter);
            time = currentTime;
            counter = 0;
        } else {
            counter++;
        }

        if (addToTimeScale > 0) {
            timeScale.addCurrentTime(addToTimeScale);
            addToTimeScale = 0;
            System.out.println("Time = " + timeScale.getCurrentTime());
        }
        iSceneHandler.onDisplay();

        if (keySet.get(KeyEvent.VK_BACK_SPACE)) {
            timeScale.addCurrentTime(10);
        }

        for (int i = 0; i < animators.size(); i++) {

            LinearPositionAnimator animator = animators.get(i);
            animator.tick();
        }

        float speed = 0.1f;
        if (keySet.get(KeyEvent.VK_UP)) {
            camera.moveZ(speed);
        }
        if (keySet.get(KeyEvent.VK_DOWN)) {
            camera.moveZ(-speed);
        }
        if (keySet.get(KeyEvent.VK_LEFT)) {
            camera.moveX(speed);
        }
        if (keySet.get(KeyEvent.VK_RIGHT)) {
            camera.moveX(-speed);
        }
        if (keySet.get(KeyEvent.VK_PAGE_UP)) {
            camera.moveY(speed);
        }
        if (keySet.get(KeyEvent.VK_PAGE_DOWN)) {
            camera.moveY(-speed);
        }

        if (keySet.get(KeyEvent.VK_A)) {
            camera.rotateY(0.05f);
        }
        if (keySet.get(KeyEvent.VK_D)) {
            camera.rotateY(-0.05f);
        }
        if (keySet.get(KeyEvent.VK_W)) {
            camera.rotateX(-0.05f);
        }
        if (keySet.get(KeyEvent.VK_S)) {
            camera.rotateX(0.05f);
        }

        if (keySet.get(KeyEvent.VK_Z)) {
            camera.rotateZ(0.01f);
        }
        if (keySet.get(KeyEvent.VK_X)) {
            camera.rotateZ(-0.01f);
        }

        if (keySet.get(KeyEvent.VK_J)) {
            partNode.rotateZ(speed);
            System.out.println("rz=" + partNode.getRotationZ());
        }
        if (keySet.get(KeyEvent.VK_L)) {
            partNode.rotateZ(-speed);
            System.out.println("rz=" + partNode.getRotationZ());
        }
        if (keySet.get(KeyEvent.VK_I)) {
            partNode.rotateX(speed);
            System.out.println("rx=" + partNode.getRotationX());
        }
        if (keySet.get(KeyEvent.VK_K)) {
            partNode.rotateX(-speed);
            System.out.println("rx=" + partNode.getRotationX());
        }

        sceneManager.clear();
        sceneManager.render();
    }

    private long addToTimeScale = 0;

    @Override
    public void keyPressed(KeyEvent e) {
        keySet.set(e.getKeyCode(), true);
    }

    @Override
    public void keyReleased(KeyEvent e) {
        keySet.set(e.getKeyCode(), false);
    }

    @Override
    public void dispose(GLAutoDrawable drawable) {

    }
}
