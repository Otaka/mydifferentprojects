package com.gooddies.persistence.file;

/**
 * @author Dmitry
 */
public class Chunk {
    private String name;
    private int pointer;
    private ChunkType chunkType;

    Chunk(String name, int pointer, ChunkType chunkType) {
        this.name = name;
        this.pointer = pointer;
        this.chunkType = chunkType;
    }

    public String getName() {
        return name;
    }

    public int getPointer() {
        return pointer;
    }

    public ChunkType getChunkType() {
        return chunkType;
    }

    @Override
    public String toString() {
        return "Chunk{" + "name=" + name + ", chunkType=" + chunkType + '}';
    }
}
