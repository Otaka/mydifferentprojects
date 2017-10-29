package com.jogl;

/**
 * @author Dmitry
 */
public class TimeScale {

    private long currentTime = 0;

    public TimeScale() {
    }

    public TimeScale(long currentTime) {
        this.currentTime = currentTime;
    }

    public void setCurrentTime(long currentTime) {
        this.currentTime = currentTime;
    }

    public long getCurrentTime() {
        return currentTime;
    }

    public void addCurrentTime(long valueToAdd) {
        currentTime += valueToAdd;
    }

    @Override
    public String toString() {
        return ""+currentTime;
    }
    
}
