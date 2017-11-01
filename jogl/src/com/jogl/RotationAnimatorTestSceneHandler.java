package com.jogl;

import com.jogl.engine.mesh.loader.impl.AsciiMdlLoader;
import com.jogl.engine.node.AnimationNode;
import com.jogl.engine.node.animator.AnimationChannel;
import com.jogl.engine.node.animator.Animator;
import com.jogl.engine.utils.io.JoglFileInputStream;
import java.io.File;
import java.io.IOException;

/**
 * @author Dmitry
 */
public class RotationAnimatorTestSceneHandler extends ISceneHandler {

    @Override
    public void init(MainWithEngine engine) throws IOException {
        AsciiMdlLoader loader = new AsciiMdlLoader();
       // File file = new File("G:\\kotor_Extracted\\ascii_model_testing\\c_drdmkfour.mdl.ascii");
        File file = new File("G:\\kotor_Extracted\\ascii_model_testing\\dor_lda01_ascii.mdl.ascii");
        engine.node = loader.load(engine.sceneManager, file, new JoglFileInputStream(file));
        engine.sceneManager.getNodes().add(engine.node);
        engine.partNode = engine.node;
        //engine.node.moveZ(10);
        //engine.node.moveY(-1);
        engine.node.rotateX((float) Math.toRadians(-90));
        AnimationNode animationNode = (AnimationNode) engine.node;
        for (AnimationChannel animationChannel : animationNode.getAnimators().values()) {
            for (Animator animator : animationChannel.getAnimators()) {
                animator.setTimeScale(engine.timeScale);
            }
        }

        engine.currentChannel = animationNode.getAnimators().get("opening1");
    }

    @Override
    public void onDisplay(MainWithEngine engine) {

    }

}
