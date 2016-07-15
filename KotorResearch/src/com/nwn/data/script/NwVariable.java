package com.nwn.data.script;

/**
 * @author Dmitry
 */
public class NwVariable {
    private int primitive;
    private Object object;
    public NwVariableType type;

    public NwVariable() {
        type = NwVariableType.INT;
    }

    public void setInt(int value) {
        primitive = value;
        object = null;
        type = NwVariableType.INT;
    }

    public void setFloat(float value) {
        primitive = Float.floatToIntBits(value);
        object = null;
        type = NwVariableType.FLOAT;
    }

    public void setString(String value) {
        object = value;
        type = NwVariableType.STRING;
    }

    public void setObject(Object value) {
        object = value;
        type = NwVariableType.OBJECT;
    }

    public NwVariableType getType() {
        return type;
    }

    public int getInt() {
        return primitive;
    }

    public float getFloat() {
        return Float.intBitsToFloat(primitive);
    }

    public Object getObject() {
        return object;
    }

    public String getString() {
        return (String) object;
    }

    @Override
    public String toString() {
        switch (type) {
            case FLOAT:
                return "Float " + getFloat();
            case INT:
                return "Int " + getInt();
            case OBJECT:
                return "Obj " + getObject();
            case STRING:
                return "'" + getString() + "'";
            default:
                return "undefined type of the NwVariable '" + type + "'";
        }
    }

    public void copyFrom(NwVariable sourceVariable) {
        type = sourceVariable.type;
        primitive = sourceVariable.primitive;
        object = sourceVariable.object;
    }
}
