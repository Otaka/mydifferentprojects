package com.jogl.engine.mesh.loader.impl.binarymdl;

/**
 * @author Dmitry
 */
public class ControllerCookedData {
    private final int type;
    private final short[] values;

    public ControllerCookedData(int type, short[] values) {
        this.type = type;
        this.values = values;
    }

    public int getType() {
        return type;
    }

    public short[] getValues() {
        return values;
    }

}
