package com.gooddies.persistence.file;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Dmitry
 */
public class GooddiesFile extends FileSection {

    private File fileForProcess;

    public GooddiesFile(File file) {
        fileForProcess = file;
    }

    public GooddiesFile openForReading() throws IOException {
        if (!fileForProcess.exists()) {
            throw new RuntimeException("File " + fileForProcess.getAbsolutePath() + " does not exist");
        }

        if (readingType == ReadingType.WRITE) {
            throw new RuntimeException("File " + fileForProcess.getAbsolutePath() + " already opened for writing");
        }

        try {
            this.file = new RandomAccessFile(fileForProcess, "r");
        } catch (FileNotFoundException ex) {
            Logger.getLogger(GooddiesFile.class.getName()).log(Level.SEVERE, null, ex);
        }

        readingType = ReadingType.READ;
        initChunkForReading();
        return this;
    }

    public GooddiesFile openForWriting() throws IOException {
        if (readingType == ReadingType.WRITE) {
            throw new RuntimeException("File " + fileForProcess.getAbsolutePath() + " already opened for reading");
        }
        if (fileForProcess.exists()) {
            fileForProcess.delete();
        }
        try {
            this.file = new RandomAccessFile(fileForProcess, "rw");
        } catch (FileNotFoundException ex) {
            Logger.getLogger(GooddiesFile.class.getName()).log(Level.SEVERE, null, ex);
        }

        readingType = ReadingType.WRITE;
        initChunkForWriting();
        return this;
    }

    @Override
    public void close() throws IOException {
        super.close();
        file.close();
        file = null;
    }
}
