package com.jogl.engine.node.animator;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Dmitry
 */
public class AnimationChannel {

    private String name;
    private List<Animator> animators=new ArrayList<>();
    private float length;

    public void setLength(float length) {
        this.length = length;
    }

    public float getLength() {
        return length;
    }
    
    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

   

    public List<Animator> getAnimators() {
        return animators;
    }
}
