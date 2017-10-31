package com.jogl.engine.node;

import com.jogl.engine.SceneManager;
import com.jogl.engine.node.animator.AnimationChannel;
import com.jogl.engine.node.animator.Animator;
import java.util.Map;

/**
 * @author Dmitry
 */
public class AnimationNode extends Node {

    private Map<String, AnimationChannel> animators;
    private AnimationChannel currentAnimation;

    public AnimationNode(SceneManager sceneManager, Node parent) {
        super(sceneManager, parent);
    }

    public AnimationNode(SceneManager sceneManager) {
        super(sceneManager);
    }

    public Map<String, AnimationChannel> getAnimators() {
        return animators;
    }

    public void setAnimators(Map<String, AnimationChannel> animators) {
        this.animators = animators;
    }

    public void processAnimation() {
        if (currentAnimation != null) {
            int size = currentAnimation.getAnimators().size();
            for (int i = 0; i < size; i++) {
                Animator a = currentAnimation.getAnimators().get(i);
                a.tick();
            }
        }
    }
}
