package com.jogl.engine.node.animator;

import com.jogl.*;
import com.jogamp.opengl.math.Quaternion;
import com.jogl.engine.node.Node;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.StringUtils;

/**
 * @author Dmitry
 */
public class LinearRotationAnimator implements Animator {

    private Node node;
    private boolean run = false;

    private float startTime = 0f;
    private float endTime = 0;
    private long startMilliseconds = 0;
    private List<RotationAnimationData> animations = new ArrayList<>();
    private RotationAnimationData currentAnimation;
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

    public void addAnimationLineRotation(String line) {
        String[] lines = StringUtils.split(line.trim(), " ");
        float tEndTime = Float.parseFloat(lines[0]);
        float x = Float.parseFloat(lines[1]);
        float y = Float.parseFloat(lines[2]);
        float z = Float.parseFloat(lines[3]);
        float angle = Float.parseFloat(lines[4]);
        float tStartTime;
        Quaternion startPosition;

        if (animations.isEmpty()) {
            tStartTime = 0;
            startPosition = new Quaternion();
            startPosition.setFromAngleAxis(angle, new float[]{x, y, z}, new float[3]);
        } else {
            tStartTime = animations.get(animations.size() - 1).endTime;
            startPosition = animations.get(animations.size() - 1).endPosition;
        }

        Quaternion endPosition = new Quaternion();
        endPosition.setFromAngleAxis(angle, new float[]{x, y, z}, new float[3]);
        RotationAnimationData data = new RotationAnimationData(tStartTime, tEndTime, startPosition, endPosition);
        if (this.endTime < tEndTime) {
            this.endTime = tEndTime;
        }

        animations.add(data);
    }

    private RotationAnimationData findAnimationData(float time) {
        if (currentAnimation != null) {
            if (currentAnimation.startTime < time && time <= currentAnimation.endTime) {
                return currentAnimation;
            }
        }

        for (int i = 0; i < animations.size(); i++) {
            RotationAnimationData adata = animations.get(i);
            if (adata.startTime <= time && time <= adata.endTime) {
                currentAnimation = adata;
                return adata;
            }
        }

        throw new IllegalArgumentException("Cannot find animation for time [" + time + "] max time =" + animations.get(animations.size() - 1).endTime);
    }

    private Quaternion tempQuaternion = new Quaternion();

    @Override
    public void tick() {
        if (endTime <= 0.000001f) {
            node.setLocalRotation(animations.get(0).startPosition);
            return;
        }
        float currentTime = (timeScale.getCurrentTime() - startMilliseconds) / 1000.f;

        currentTime = currentTime % endTime;

        RotationAnimationData data = findAnimationData(currentTime);
        float frameLength = data.endTime - data.startTime;

        if (frameLength <= 0.000001f) {
            tempQuaternion = data.startPosition;
        } else {
            float t = currentTime - data.startTime;
            float animTimePosition = t / frameLength;
            tempQuaternion.setSlerp(data.startPosition, data.endPosition, animTimePosition);
        }

        node.setLocalRotation(tempQuaternion);
    }

    public void stop() {
        run = false;
    }

    public void start() {
        run = true;
        startMilliseconds = timeScale.getCurrentTime();
    }

    private static class RotationAnimationData {

        float startTime, endTime;
        Quaternion startPosition;
        Quaternion endPosition;

        public RotationAnimationData(float startTime, float endTime, Quaternion startPosition, Quaternion endPosition) {
            this.startTime = startTime;
            this.endTime = endTime;
            this.startPosition = startPosition;
            this.endPosition = endPosition;
        }
    }
}
