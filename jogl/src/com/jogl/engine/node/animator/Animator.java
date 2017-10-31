package com.jogl.engine.node.animator;

import com.jogl.TimeScale;
import com.jogl.engine.node.Node;

/**
 * @author Dmitry
 */
public interface Animator {
    public void tick();
    public void setNode(Node node);
     public void setTimeScale(TimeScale timeScale);
}
