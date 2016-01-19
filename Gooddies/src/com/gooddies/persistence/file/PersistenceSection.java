package com.gooddies.persistence.file;

import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * @author Dmitry
 */
public class PersistenceSection extends FileSection {
    protected PersistenceSection(RandomAccessFile file, ReadingType readingType) throws IOException {
        this.file = file;
        this.readingType = readingType;
        if (readingType == ReadingType.READ) {
            initChunkForReading();
        } else {
            initChunkForWriting();
        }
    }
}
