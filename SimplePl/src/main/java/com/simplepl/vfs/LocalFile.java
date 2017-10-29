package com.simplepl.vfs;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import org.apache.commons.io.FilenameUtils;

/**
 * @author sad
 */
public class LocalFile extends AbstractFile {

    private File file;

    public LocalFile(File file) {
        this.file = file;
    }

    public File getFile() {
        return file;
    }

    @Override
    public String getName() {
        return FilenameUtils.getBaseName(getRawName());
    }

    @Override
    public OutputStream getOutputStream() {
        try {
            return new FileOutputStream(file);
        } catch (FileNotFoundException ex) {
            throw new IllegalStateException("File [" + file.getAbsolutePath() + "] not found");
        }
    }
    
    @Override
    public InputStream getInputStream() {
        try {
            return new FileInputStream(file);
        } catch (FileNotFoundException ex) {
            throw new IllegalStateException("File [" + file.getAbsolutePath() + "] not found");
        }
    }

    @Override
    public String getRawName() {
        return file.getName();
    }

    @Override
    public String toString() {
        return getRawName()+":"+getName();
    }

    

}
