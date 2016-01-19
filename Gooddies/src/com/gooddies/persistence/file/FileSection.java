package com.gooddies.persistence.file;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * @author Dmitry
 */
public abstract class FileSection {
    protected static enum ReadingType {
        READ, WRITE
    };
    private String sectionName;
    protected RandomAccessFile file;
    protected List<Chunk> chunks = new ArrayList<Chunk>(20);
    protected ReadingType readingType;
    protected boolean closed = false;
    protected FileSection openedFileSection;
    protected int offsetOfPointerToTable = 0;
    protected Map<String, Chunk> chunksMapping = new HashMap<String, Chunk>();

    boolean isClosed() {
        return closed;
    }

    public String getSectionName() {
        return sectionName;
    }

    public void setSectionName(String name) {
        sectionName = name;
    }

    protected void initChunkForReading() throws IOException {
        int pointerToChunksTable = file.readInt();
        file.seek(pointerToChunksTable);
        int chunksCount = file.readShort();
        for (int i = 0; i < chunksCount; i++) {
            String name = file.readUTF();
            int pointer = file.readInt();
            ChunkType chunkType = ChunkType.getFromId(file.readByte());
            Chunk chunk = new Chunk(name, pointer, chunkType);
            chunks.add(chunk);
            chunksMapping.put(name, chunk);
        }
    }

    public Chunk getChunk(String name) {
        return chunksMapping.get(name);
    }

    protected void initChunkForWriting() throws IOException {
        offsetOfPointerToTable = (int) file.getFilePointer();
        file.writeInt(0);///temp offset that will be replaced when we will close the section
    }

    public void writeInt(String name, int value) throws IOException {
        addChunk(name, ChunkType.Integer);
        file.writeInt(value);
    }

    public void writeBoolean(String name, boolean value) throws IOException {
        addChunk(name, ChunkType.Boolean);
        file.writeBoolean(value);
    }

    public void writeByte(String name, byte value) throws IOException {
        addChunk(name, ChunkType.Byte);
        file.writeByte(value);
    }

    public void writeChar(String name, char value) throws IOException {
        addChunk(name, ChunkType.Char);
        file.writeChar(value);
    }

    public void writeDouble(String name, double value) throws IOException {
        addChunk(name, ChunkType.Double);
        file.writeDouble(value);
    }

    public void writeFloat(String name, float value) throws IOException {
        addChunk(name, ChunkType.Float);
        file.writeFloat(value);
    }

    public void writeLong(String name, long value) throws IOException {
        addChunk(name, ChunkType.Long);
        file.writeLong(value);
    }

    public void writeLongs(String name, long... values) throws IOException {
        addChunk(name, ChunkType.ArrayOfLongs);
        file.writeShort(values.length);
        for (long l : values) {
            file.writeLong(l);
        }
    }

    public void writeBooleans(String name, boolean... values) throws IOException {
        addChunk(name, ChunkType.ArrayOfBooleans);
        file.writeShort(values.length);
        for (boolean b : values) {
            file.writeBoolean(b);
        }
    }

    public void writeShort(String name, short value) throws IOException {
        addChunk(name, ChunkType.Short);
        file.writeShort(value);
    }

    public void writeString(String name, String value) throws IOException {
        addChunk(name, ChunkType.String);
        file.writeUTF(value);
    }

    public PersistenceSection createNewSection(String sectionName) throws IOException {
        addChunk(sectionName, ChunkType.Section);
        PersistenceSection section = new PersistenceSection(file, readingType);
        section.setSectionName(sectionName);
        openedFileSection = section;
        return section;
    }

    protected void addChunk(String name, ChunkType type) throws IOException {
        if (readingType != ReadingType.WRITE) {
            throw new RuntimeException("File is not opened for writing");
        }
        if (openedFileSection != null && !openedFileSection.isClosed()) {
            throw new RuntimeException("Trying to write attribute while the section contains opened nested section");
        }

        if (closed) {
            throw new RuntimeException("Trying to write attribute while section is already closed");
        }

        int position = (int) file.getFilePointer();
        Chunk chunk = new Chunk(name, position, type);
        chunks.add(chunk);
    }

    public void close() throws IOException {
        if (readingType == ReadingType.WRITE) {
            if (openedFileSection != null && !openedFileSection.isClosed()) {
                throw new RuntimeException("Trying to close section while the section contains opened nested section");
            }
            int realTableOffset = (int) file.getFilePointer();
            file.seek(offsetOfPointerToTable);
            file.writeInt(realTableOffset);
            file.seek(realTableOffset);
            file.writeShort(chunks.size());
            for (Chunk chunk : chunks) {
                file.writeUTF(chunk.getName());
                file.writeInt(chunk.getPointer());
                file.writeByte(chunk.getChunkType().getID());
            }
            closed = true;
        }
    }

    public List<Chunk> getAllSections() {
        if (readingType != ReadingType.READ) {
            throw new RuntimeException("File is not opened for reading");
        }
        List<Chunk> chunkSections = new ArrayList<Chunk>();
        for (Chunk fp : chunks) {
            if (fp.getChunkType() == ChunkType.Section) {
                chunkSections.add(fp);
            }
        }

        return chunkSections;
    }

    public List<Chunk> getAllAtributes() {
        if (readingType != ReadingType.READ) {
            throw new RuntimeException("File is not opened for reading");
        }
        List<Chunk> chunkSections = new ArrayList<Chunk>();
        for (Chunk fp : chunks) {
            if (fp.getChunkType() != ChunkType.Section) {
                chunkSections.add(fp);
            }
        }

        return chunkSections;
    }

    public FileSection readSection(Chunk chunk) throws IOException {
        if (readingType != ReadingType.READ) {
            throw new RuntimeException("File is not opened for reading");
        }
        if (chunk.getChunkType() != ChunkType.Section) {
            throw new RuntimeException("Error. Trying to read Section from chunk of the type " + chunk.getChunkType());
        }

        file.seek(chunk.getPointer());
        PersistenceSection section = new PersistenceSection(file, readingType);
        section.setSectionName(chunk.getName());
        return section;
    }

    public FileSection readSection(String name) throws IOException {
        Chunk chunk = getChunk(name);
        if (chunk == null) {
            throw new RuntimeException("Section [" + sectionName + "] does not contain element with name [" + name + "]");
        }
        return readSection(chunk);
    }

    public String readString(Chunk chunk) throws IOException {
        if (readingType != ReadingType.READ) {
            throw new RuntimeException("File is not opened for reading");
        }
        if (chunk.getChunkType() != ChunkType.String) {
            throw new RuntimeException("Error. Trying to read String from chunk of the type " + chunk.getChunkType());
        }

        file.seek(chunk.getPointer());
        return file.readUTF();
    }

    public String readString(String name) throws IOException {
        Chunk chunk = getChunk(name);
        if (chunk == null) {
            throw new RuntimeException("Section [" + sectionName + "] does not contain element with name [" + name + "]");
        }
        return readString(chunk);
    }

    public int readInt(Chunk chunk) throws IOException {
        if (readingType != ReadingType.READ) {
            throw new RuntimeException("File is not opened for reading");
        }
        if (chunk.getChunkType() != ChunkType.Integer && chunk.getChunkType() != ChunkType.SColor) {
            throw new RuntimeException("Error. Trying to read Integer from chunk of the type " + chunk.getChunkType());
        }

        file.seek(chunk.getPointer());
        return file.readInt();
    }

    public int readInt(String name) throws IOException {
        Chunk chunk = getChunk(name);
        if (chunk == null) {
            throw new RuntimeException("Section [" + sectionName + "] does not contain element with name [" + name + "]");
        }
        return readInt(chunk);
    }

    public short readShort(String name) throws IOException {
        Chunk chunk = getChunk(name);
        if (chunk == null) {
            throw new RuntimeException("Section [" + sectionName + "] does not contain element with name [" + name + "]");
        }
        return readShort(chunk);
    }

    public short readShort(Chunk chunk) throws IOException {
        if (readingType != ReadingType.READ) {
            throw new RuntimeException("File is not opened for reading");
        }
        if (chunk.getChunkType() != ChunkType.Short) {
            throw new RuntimeException("Error. Trying to read Short from chunk of the type " + chunk.getChunkType());
        }

        file.seek(chunk.getPointer());
        return file.readShort();
    }

    public long readLong(String name) throws IOException {
        Chunk chunk = getChunk(name);
        if (chunk == null) {
            throw new RuntimeException("Section [" + sectionName + "] does not contain element with name [" + name + "]");
        }
        return readLong(chunk);
    }

    public long readLong(Chunk chunk) throws IOException {
        if (readingType != ReadingType.READ) {
            throw new RuntimeException("File is not opened for reading");
        }
        if (chunk.getChunkType() != ChunkType.Long) {
            throw new RuntimeException("Error. Trying to read Long from chunk of the type " + chunk.getChunkType());
        }

        file.seek(chunk.getPointer());
        return file.readLong();
    }

    public long[] readLongs(String name) throws IOException {
        Chunk chunk = getChunk(name);
        if (chunk == null) {
            throw new RuntimeException("Section [" + sectionName + "] does not contain element with name [" + name + "]");
        }
        return readLongs(chunk);
    }

    public long[] readLongs(Chunk chunk) throws IOException {
        if (readingType != ReadingType.READ) {
            throw new RuntimeException("File is not opened for reading");
        }
        if (chunk.getChunkType() != ChunkType.ArrayOfLongs) {
            throw new RuntimeException("Error. Trying to read ArrayOfLongs from chunk of the type " + chunk.getChunkType());
        }

        file.seek(chunk.getPointer());
        int elemCount = file.readShort();
        long[] array = new long[elemCount];
        for (int i = 0; i < elemCount; i++) {
            array[i] = file.readLong();
        }
        return array;
    }

    public boolean[] readBooleans(String name) throws IOException {
        Chunk chunk = getChunk(name);
        if (chunk == null) {
            throw new RuntimeException("Section [" + sectionName + "] does not contain element with name [" + name + "]");
        }
        return readBooleans(chunk);
    }

    public boolean[] readBooleans(Chunk chunk) throws IOException {
        if (readingType != ReadingType.READ) {
            throw new RuntimeException("File is not opened for reading");
        }
        if (chunk.getChunkType() != ChunkType.ArrayOfBooleans) {
            throw new RuntimeException("Error. Trying to read ArrayOfBooleans from chunk of the type " + chunk.getChunkType());
        }

        file.seek(chunk.getPointer());
        int elemCount = file.readShort();
        boolean[] array = new boolean[elemCount];
        for (int i = 0; i < elemCount; i++) {
            array[i] = file.readBoolean();
        }
        return array;
    }

    public boolean readBoolean(String name) throws IOException {
        Chunk chunk = getChunk(name);
        if (chunk == null) {
            throw new RuntimeException("Section [" + sectionName + "] does not contain element with name [" + name + "]");
        }
        return readBoolean(chunk);
    }

    public boolean readBoolean(Chunk chunk) throws IOException {
        if (readingType != ReadingType.READ) {
            throw new RuntimeException("File is not opened for reading");
        }
        if (chunk.getChunkType() != ChunkType.Boolean) {
            throw new RuntimeException("Error. Trying to read Boolean from chunk of the type " + chunk.getChunkType());
        }

        file.seek(chunk.getPointer());
        return file.readBoolean();
    }

    public char readChar(String name) throws IOException {
        Chunk chunk = getChunk(name);
        if (chunk == null) {
            throw new RuntimeException("Section [" + sectionName + "] does not contain element with name [" + name + "]");
        }
        return readChar(chunk);
    }

    public char readChar(Chunk chunk) throws IOException {
        if (readingType != ReadingType.READ) {
            throw new RuntimeException("File is not opened for reading");
        }
        if (chunk.getChunkType() != ChunkType.Char) {
            throw new RuntimeException("Error. Trying to read Char from chunk of the type " + chunk.getChunkType());
        }

        file.seek(chunk.getPointer());
        return file.readChar();
    }

    public byte readByte(String name) throws IOException {
        Chunk chunk = getChunk(name);
        if (chunk == null) {
            throw new RuntimeException("Section [" + sectionName + "] does not contain element with name [" + name + "]");
        }
        return readByte(chunk);
    }

    public byte readByte(Chunk chunk) throws IOException {
        if (readingType != ReadingType.READ) {
            throw new RuntimeException("File is not opened for reading");
        }
        if (chunk.getChunkType() != ChunkType.Byte) {
            throw new RuntimeException("Error. Trying to read Byte from chunk of the type " + chunk.getChunkType());
        }

        file.seek(chunk.getPointer());
        return file.readByte();
    }

    public float readFloat(String name) throws IOException {
        Chunk chunk = getChunk(name);
        if (chunk == null) {
            throw new RuntimeException("Section [" + sectionName + "] does not contain element with name [" + name + "]");
        }
        return readFloat(chunk);
    }

    public float readFloat(Chunk chunk) throws IOException {
        if (readingType != ReadingType.READ) {
            throw new RuntimeException("File is not opened for reading");
        }
        if (chunk.getChunkType() != ChunkType.Float) {
            throw new RuntimeException("Error. Trying to read Float from chunk of the type " + chunk.getChunkType());
        }

        file.seek(chunk.getPointer());
        return file.readFloat();
    }

    public double readDouble(String name) throws IOException {
        Chunk chunk = getChunk(name);
        if (chunk == null) {
            throw new RuntimeException("Section [" + sectionName + "] does not contain element with name [" + name + "]");
        }
        return readDouble(chunk);
    }

    public double readDouble(Chunk chunk) throws IOException {
        if (readingType != ReadingType.READ) {
            throw new RuntimeException("File is not opened for reading");
        }
        if (chunk.getChunkType() != ChunkType.Double) {
            throw new RuntimeException("Error. Trying to read Double from chunk of the type " + chunk.getChunkType());
        }

        file.seek(chunk.getPointer());
        return file.readDouble();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(sectionName);
        sb.append("[");
        boolean first = true;
        for (Chunk chunk : chunks) {
            if (!first) {
                sb.append(", ");
            }
            sb.append(chunk.getName());
            first = false;
        }
        sb.append("]");
        return sb.toString();
    }
}