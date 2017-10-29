package com.jogl;

import com.jogl.engine.mesh.loader.impl.AsciiMdlLoader;
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
        File file = new File("G:\\kotor_Extracted\\ascii_model_testing\\plc_footlker.mdl.ascii");
        engine.node = loader.load(engine.sceneManager, file, new JoglFileInputStream(file));
        engine.sceneManager.getNodes().add(engine.node);
        engine.partNode = engine.sceneManager.getObjectByName("PLC_FootLker");
        //collision object hiding
        engine.node.moveZ(10);
        engine.node.moveY(-1);
        engine.node.rotateX((float) Math.toRadians(-90));

        engine.timeScale.setCurrentTime(System.currentTimeMillis());
        LinearRotationAnimator animator = new LinearRotationAnimator(engine.sceneManager.getObjectByName("Mesh03"), engine.timeScale);
        animator.addAnimationLinePosition("0.0 1.0 0.0 0.0 6.28319");
        animator.addAnimationLinePosition("0.666667 0.0 -1.0 0.0 5.25288");
    }

    @Override
    public void onDisplay() {

    }

}
