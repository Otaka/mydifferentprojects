package com.nwn.script;

/**
 * @author sad
 */
public class NwStack {
    private int[]array;
    private int position;
    
    public NwStack(int size) {
        array=new int[size];
    }

    public int getPosition() {
        return position;
    }
    
    public int popInt(){
        position--;
        return array[position];
    }
    
    public float popFloat(){
        position--;
        return Float.intBitsToFloat(array[position]);
    }
    
    public void pushInt(int value){
        array[position]=value;
        position++;
    }
    
    public void pushFloat(float value){
        array[position]=Float.floatToIntBits(value);
        position++;
    }
    
}
