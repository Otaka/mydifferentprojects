package com.gooddies.persistence.file;

/**
 * @author Dmitry
 */
public enum ChunkType {
    String((byte) 1),
    Section((byte) 2),
    Integer((byte) 3),
    Char((byte) 4),
    Short((byte) 5),
    Long((byte) 6),
    Float((byte) 7),
    Double((byte) 8),
    Boolean((byte) 9),
    Byte((byte) 10),
    ArrayOfLongs((byte)11),
    SColor((byte)12),
    Vector((byte)13),
    Dimension2df((byte)14),
    ArrayOfBooleans((byte)15),
    ArrayOfVectors((byte)16)
    ;
    
    private byte id;

    private ChunkType(byte id) {
        this.id = id;
    }

    public int getID() {
        return id;
    }
    
    public static ChunkType getFromId(int id){
        switch(id){
            case 1:return String;
            case 2:return Section;
            case 3:return Integer;
            case 4:return Char;
            case 5:return Short;
            case 6:return Long;
            case 7:return Float;
            case 8:return Double;
            case 9:return Boolean;
            case 10:return Byte;
            case 11:return ArrayOfLongs;
            case 12:return SColor;
            case 13:return Vector;
            case 14:return Dimension2df;
            case 15:return ArrayOfBooleans;
            case 16:return ArrayOfVectors;
        }
        throw new RuntimeException("code "+id+" is not a valid ChunkType");
    }
}
