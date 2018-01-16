package com.jogl;

import com.jogl.engine.mesh.loader.impl.AsciiMdlLoader;
import com.jogl.engine.node.AnimationNode;
import com.jogl.engine.node.Node;
import com.jogl.engine.node.animator.AnimationChannel;
import com.jogl.engine.node.animator.Animator;
import com.jogl.engine.utils.io.JoglFileInputStream;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;

/**
 * @author Dmitry
 */
public class TestTransformationSceneHandler extends ISceneHandler {

    @Override
    public void init(MainWithEngine engine) throws IOException {
        AsciiMdlLoader loader = new AsciiMdlLoader();
       // File file = new File("G:\\kotor_Extracted\\ascii_model_testing\\c_drdmkfour.mdl.ascii");
        File file = new File("G:\\kotor_Extracted\\ascii_model_testing\\testData.mdl.ascii");
        engine.node = loader.load(engine.sceneManager, file, new JoglFileInputStream(file));
        engine.sceneManager.getNodes().add(engine.node);
        engine.partNode = engine.node;
        Node cam=engine.sceneManager.getActiveCamera();
        cam.move(0, 2, -10);
        
        
        
       // engine.node.turn((float) Math.toRadians(-90), 0, 0, true);
        AnimationNode animationNode = (AnimationNode) engine.node;
        for (AnimationChannel animationChannel : animationNode.getAnimators().values()) {
            for (Animator animator : animationChannel.getAnimators()) {
                animator.setTimeScale(engine.timeScale);
            }
        }

        //engine.currentChannel = animationNode.getAnimators().get("opening1");
    }

    @Override
    public void onDisplay(MainWithEngine engine) {
        if(engine.isKeyDown(KeyEvent.VK_J)){
            engine.node.turn(0, (float) Math.toRadians(5), 0, false);
        }
        if(engine.isKeyDown(KeyEvent.VK_L)){
            engine.node.turn(0, (float) Math.toRadians(-5), 0, false);
        }
        
        if(engine.isKeyDown(KeyEvent.VK_U)){
            engine.node.turn(0, 0,(float) Math.toRadians(5), false);
        }
        if(engine.isKeyDown(KeyEvent.VK_O)){
            engine.node.turn(0,0, (float) Math.toRadians(-5),  false);
        }
        if(engine.isKeyDown(KeyEvent.VK_I)){
            engine.node.turn((float) Math.toRadians(5), 0,0, false);
        }
        if(engine.isKeyDown(KeyEvent.VK_K)){
            engine.node.turn((float) Math.toRadians(-5),0, 0,  false);
        }
        if(engine.isKeyDown(KeyEvent.VK_SPACE)){
            engine.node.move(0,0, 0.5f  );
        }
    }

}
