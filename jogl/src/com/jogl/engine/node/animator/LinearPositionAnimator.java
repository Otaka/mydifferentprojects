package com.jogl.engine.node.animator;

import com.jogl.*;
import com.jogl.engine.math.Vector3;
import com.jogl.engine.node.Node;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.StringUtils;

/**
 * @author Dmitry
 */
public class LinearPositionAnimator implements Animator {

    private Node node;
    private boolean run = false;

    private float startTime = 0f;
    private float endTime = 0;
    private long startMilliseconds = 0;
    private List<PositionAnimationData> animations = new ArrayList<>();
    private PositionAnimationData currentAnimation;
    private TimeScale timeScale;

    @Override
    public void setNode(Node node) {
        this.node = node;
    }

    @Override
    public void setTimeScale(TimeScale timeScale) {
        this.timeScale = timeScale;
    }

    public Node getNode() {
        return node;
    }

    public TimeScale getTimeScale() {
        return timeScale;
    }

    public void addAnimationBezierLinePosition(String line) {
        String[] lines = StringUtils.split(line, " ");
        float tEndTime = Float.parseFloat(lines[0]);
        float x = -Float.parseFloat(lines[1]);
        float y = -Float.parseFloat(lines[2]);
        float z = -Float.parseFloat(lines[3]);
        float tStartTime;
        Vector3 startPosition;
        if (animations.isEmpty()) {
            tStartTime = 0;
            startPosition = new Vector3(x, y, z);
        } else {
            tStartTime = animations.get(animations.size() - 1).endTime;
            startPosition = animations.get(animations.size() - 1).endPosition;
        }

        PositionAnimationData data = new PositionAnimationData(tStartTime, tEndTime, startPosition, new Vector3(x, y, z));
        if (this.endTime < tEndTime) {
            this.endTime = tEndTime;
        }
        animations.add(data);
    }
    
    public void addAnimationLinePosition(String line) {
        String[] lines = StringUtils.split(line, " ");
        float tEndTime = Float.parseFloat(lines[0]);
        float x = Float.parseFloat(lines[1]);
        float y = Float.parseFloat(lines[2]);
        float z = Float.parseFloat(lines[3]);
        float tStartTime;
        Vector3 startPosition;
        if (animations.isEmpty()) {
            tStartTime = 0;
            startPosition = new Vector3(x, y, z);
        } else {
            tStartTime = animations.get(animations.size() - 1).endTime;
            startPosition = animations.get(animations.size() - 1).endPosition;
        }

        PositionAnimationData data = new PositionAnimationData(tStartTime, tEndTime, startPosition, new Vector3(x, y, z));
        if (this.endTime < tEndTime) {
            this.endTime = tEndTime;
        }
        animations.add(data);
    }

    /* public void postProcessAnimation(){
        for(int i=0;i<animations.size();i++){
            AnimationData ad=animations.get(i);
            if(Math.abs(ad.endTime-ad.startTime)<0.0000001f){
                animations.remove(i);
                i--;
            }
        }
    }*/
    private PositionAnimationData findAnimationData(float time) {
        if (currentAnimation != null) {
            if (currentAnimation.startTime < time && time <= currentAnimation.endTime) {
                return currentAnimation;
            }
        }

        for (int i = 0; i < animations.size(); i++) {
            PositionAnimationData adata = animations.get(i);
            if (adata.startTime <= time && time <= adata.endTime) {
                currentAnimation = adata;
                return adata;
            }
        }

        throw new IllegalArgumentException("Cannot find animation for time [" + time + "] max time =" + animations.get(animations.size() - 1).endTime);
    }

    @Override
    public void tick() {
        if (endTime <= 0.000001f) {
            PositionAnimationData p = animations.get(0);
            node.setLocalPosition(p.startPosition.getX(), p.startPosition.getY(), p.startPosition.getZ());
        } else {
            float currentTime = (timeScale.getCurrentTime() - startMilliseconds) / 1000.f;
            currentTime = currentTime % endTime;
            /* if (currentTime >= endTime) {
            currentTime = 0;
        }*/

            PositionAnimationData data = findAnimationData(currentTime);
            float frameLength = data.endTime - data.startTime;

            Vector3 animPosition;
            if (frameLength <= 0.000001f) {
                animPosition = data.startPosition;
            } else {
                float t = currentTime - data.startTime;
                float animTimePosition = t / frameLength;
                animPosition = lerp(data.startPosition, data.endPosition, animTimePosition);
            }

            node.setLocalPosition(animPosition.getX(), animPosition.getY(), animPosition.getZ());
        }
    }

    public void stop() {
        run = false;
    }

    public void start() {
        run = true;
        startMilliseconds = timeScale.getCurrentTime();
    }

    private Vector3 tempPoint = new Vector3();

    public Vector3 lerp(Vector3 p1, Vector3 p2, float t) {
        tempPoint.setX(lerp(p1.getX(), p2.getX(), t));
        tempPoint.setY(lerp(p1.getY(), p2.getY(), t));
        tempPoint.setZ(lerp(p1.getZ(), p2.getZ(), t));
        return tempPoint;
    }

    public float lerp(float x1, float x2, float t) {
        return (1 - t) * x1 + t * x2;

    }

    private static class PositionAnimationData {

        float startTime, endTime;
        Vector3 startPosition;
        Vector3 endPosition;

        public PositionAnimationData(float startTime, float endTime, Vector3 startPosition, Vector3 endPosition) {
            this.startTime = startTime;
            this.endTime = endTime;
            this.startPosition = startPosition;
            this.endPosition = endPosition;
        }
    }
}
