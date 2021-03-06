package com.jogl;

import com.jogl.engine.mesh.loader.impl.AsciiMdlLoader;
import com.jogl.engine.node.Node;
import com.jogl.engine.node.animator.LinearPositionAnimator;
import com.jogl.engine.utils.io.JoglFileInputStream;
import java.io.File;
import java.io.IOException;

/**
 * @author Dmitry
 */
public class PositionAnimatorTestSceneHandler extends ISceneHandler{

    @Override
    public void init(MainWithEngine engine) throws IOException {
        AsciiMdlLoader loader = new AsciiMdlLoader();
            File file = new File("G:\\kotor_Extracted\\ascii_model_testing\\dor_lda01_ascii.mdl.ascii");
           
            engine.node = loader.load(engine.sceneManager, file, new JoglFileInputStream(file));
            engine.sceneManager.getNodes().add(engine.node);
            engine.partNode = engine.sceneManager.getObjectByName("DOR_LDA01");
            //collision object hiding
            Node trans = engine.sceneManager.getObjectByName("trans");
            if (trans != null) {
                trans.setVisible(false);
            }
            engine.node.move(0,0,-10);
            engine.node.turn((float) Math.toRadians(-90), 0, 0, true);
            engine.timeScale.setCurrentTime(System.currentTimeMillis());
            
            //opening
            LinearPositionAnimator animator = new LinearPositionAnimator();
            animator.setNode(engine.sceneManager.getObjectByName("Mesh01"));
            animator.setTimeScale(engine.timeScale);
            animator.addAnimationLinePosition("1.0 -0.0419167 0.0967285 0.0  0.0 0.0 0.0  0.0 0.0 0.0");
            animator.addAnimationLinePosition("1.66667 0.923998 0.0967285 0.0  0.0 0.0 0.0  0.0536619 0.0 0.0");
            animator.addAnimationLinePosition("2.0 0.923998 0.0967285 0.0  0.0 0.0 0.0  0.0 0.0 0.0");
            animator.start();
           // engine.positionAnimators.add(animator);

            animator = new LinearPositionAnimator();
            animator.setNode(engine.sceneManager.getObjectByName("Box04"));
            animator.setTimeScale(engine.timeScale);
            animator.addAnimationLinePosition("0.0 -0.0419167 0.0967285 0.0  0.0 0.0 0.0  0.0 0.0 0.0");
            animator.addAnimationLinePosition("0.1 -0.106418 0.0967285 0.0  0.0 0.0 0.0  0.0 0.0 0.0");
            animator.addAnimationLinePosition("0.2 -0.0893335 0.0967285 0.0  -0.00970267 0.0 0.0  0.0229322 0.0 0.0");
            animator.addAnimationLinePosition("0.533333 -0.0893335 0.0967285 0.0  0.0 0.0 0.0  0.0 0.0 0.0");
            animator.addAnimationLinePosition("2.0 -1.94879 0.0967285 0.0  1.23963 0.0 0.0  0.0 0.0 0.0");
            animator.start();
           // engine.positionAnimators.add(animator);
            
            
            //closing
            /*LinearPositionAnimator animator = new LinearPositionAnimator(sceneManager.getObjectByName("Mesh01"), timeScale);
            animator.addAnimationLinePosition("0.0 0.923998 0.0967285 0.0  0.0 0.0 0.0  0.0 0.0 0.0");
            animator.addAnimationLinePosition("0.333333 0.923998 0.0967285 0.0  0.0536619 0.0 0.0  -0.107324 0.0 0.0");
            animator.addAnimationLinePosition("1.0 -0.0419167 0.0967285 0.0  0.643943 0.0 0.0  0.0 0.0 0.0");
            animator.addAnimationLinePosition("2.0 -0.0419167 0.0967285 0.0  0.643943 0.0 0.0  0.0 0.0 0.0");
            animator.start();
            positionAnimators.add(animator);
            
            
            animator = new LinearPositionAnimator(sceneManager.getObjectByName("Object01"), timeScale);
            animator.addAnimationLinePosition("2.0 0.0 0.0 0.0  0.0 0.0 0.0  0.0 0.0 0.0");
            animator.start();
            positionAnimators.add(animator);
            
            animator = new LinearPositionAnimator(sceneManager.getObjectByName("Box04"), timeScale);
            animator.addAnimationLinePosition("0.0 -1.94879 0.0967285 0.0  0.0 0.0 0.0  1.23964 0.0 0.0");
            animator.addAnimationLinePosition("1.46667 -0.08933 0.0967285 0.0  -1.23964 0.0 0.0  0.0316088 0.0 0.0");
            animator.addAnimationLinePosition("2.0 -0.0419167 0.0967285 0.0  0.0400847 0.0 0.0  0.0 0.0 0.0");
            animator.start();
            positionAnimators.add(animator);*/
            
    }

    @Override
    public void onDisplay(MainWithEngine engine) {
        
    }

    
}
