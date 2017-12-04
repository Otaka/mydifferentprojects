package com.nwn.data.script;

/**
 * @author sad
 */
public class NwStack {
    private final NwVariable[] array;
    private int arraySize;
    private int position;

    public NwStack(int size) {
        array = new NwVariable[size];
        for (int i = 0; i < size; i++) {
            array[i] = new NwVariable();
        }

        this.arraySize = size;
    }

    public NwVariable getVariable(int index) {
        return array[position + index];
    }

    public void incStack(int position) {
        this.position += position;
        if (this.position >= arraySize) {
            throw new RuntimeException("Stack overflow. Stack size " + arraySize);
        } else if (this.position < 0) {
            throw new RuntimeException("Stack underflow. Stack position " + position);
        }
    }

    public int getPosition() {
        return position;
    }

    public int popInt() {
        position--;
        return array[position].getInt();
    }

    public float popFloat() {
        position--;
        return array[position].getFloat();
    }

    public String popString() {
        position--;
        return array[position].getString();
    }

    public Object popObject() {
        position--;
        return array[position].getObject();
    }

    public void pushInt(int value) {
        array[position].setInt(value);
        position++;
    }

    public void pushFloat(float value) {
        array[position].setFloat(value);
        position++;
    }

    public void pushString(String value) {
        array[position].setString(value);
        position++;
    }

    public void pushObject(Object value) {
        array[position].setObject(value);
        position++;
    }

}
