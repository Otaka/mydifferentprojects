package com.jogl;

import com.jogl.engine.math.Vector3;
import com.jogl.engine.node.Node;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.StringUtils;

/**
 * @author Dmitry
 */
public class LinearRotationAnimator {

    private Node node;
    private boolean run = false;

    private float dx, dy, dz;

    private float startTime = 0f;
    private float endTime = 0;
    private long startMilliseconds = 0;
    private List<RotationAnimationData> animations = new ArrayList<>();
    private RotationAnimationData currentAnimation;
    private TimeScale timeScale;

    public LinearRotationAnimator(Node node, TimeScale timeScale) {
        this.node = node;
        this.timeScale = timeScale;
    }

    public void addAnimationLinePosition(String line) {
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

        RotationAnimationData data = new RotationAnimationData(tStartTime, tEndTime, startPosition, new Vector3(x, y, z));
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

    public void tick() {
        float currentTime = (timeScale.getCurrentTime() - startMilliseconds) / 1000.f;
        currentTime = currentTime % endTime;
        /* if (currentTime >= endTime) {
            currentTime = 0;
        }*/

        RotationAnimationData data = findAnimationData(currentTime);
        float frameLength = data.endTime - data.startTime;

        Vector3 animPosition;
        if (frameLength <= 0.000001f) {
            animPosition = data.startPosition;
        } else {
            float t = currentTime - data.startTime;
            float animTimePosition = t / frameLength;
            animPosition = lerp(data.startPosition, data.endPosition, animTimePosition);
        }

        node.move(-dx, -dy, -dz);
        dx = animPosition.getX();
        dy = animPosition.getY();
        dz = animPosition.getZ();
        node.move(dx, dy, dz);

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

    private static class RotationAnimationData {

        float startTime, endTime;
        Vector3 startPosition;
        Vector3 endPosition;

        public RotationAnimationData(float startTime, float endTime, Vector3 startPosition, Vector3 endPosition) {
            this.startTime = startTime;
            this.endTime = endTime;
            this.startPosition = startPosition;
            this.endPosition = endPosition;
        }
    }
}
