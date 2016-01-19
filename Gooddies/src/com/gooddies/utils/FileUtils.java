package com.gooddies.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;

/**
 * @author sad
 */
public class FileUtils {

    public static String readToString(File file, String encoding) {
        try {
            return readToString(new FileInputStream(file), encoding);
        } catch (FileNotFoundException ex) {
            throw new RuntimeException(ex);
        }
    }

    public static String getFileExtension(File file) {
        return getFileExtension(file.getAbsolutePath());
    }

    public static String getFileExtension(String fileName) {
        String extension = "";

        int i = fileName.lastIndexOf('.');
        int p = Math.max(fileName.lastIndexOf('/'), fileName.lastIndexOf('\\'));

        if (i > p) {
            extension = fileName.substring(i + 1);
        }
        return extension;
    }

    public static String replacePath(String imagePath, String oldDir, String newDir) {
        int index = imagePath.indexOf(oldDir);
        if (index == -1) {
            return imagePath;
        }

        String imageName = imagePath.substring(index + oldDir.length());
        return newDir + imageName;
    }
    
    public static String getFileNameWithoutExtension(File file) {
        return getFileNameWithoutExtension(file.getAbsolutePath());
    }

    public static String getFileNameWithoutExtension(String filePath) {
        String extension = getFileExtension(filePath);

        String tstr;
        if (extension.isEmpty()) {
            tstr=filePath;
        } else {
            tstr=filePath.substring(0, filePath.length() - extension.length() - 1);
        }

        int separatorIndex = Math.max(tstr.lastIndexOf('/'),tstr.lastIndexOf('\\'));
        if(separatorIndex==-1){
            return tstr;
        }
        return tstr.substring(separatorIndex+1);
    }

    public static String readToString(InputStream is, String encoding) {
        int bufferSize = 2048;
        final char[] buffer = new char[bufferSize];
        final StringBuilder out = new StringBuilder();
        try {
            final Reader in = new InputStreamReader(is, encoding);
            try {
                for (;;) {
                    int rsz = in.read(buffer, 0, buffer.length);
                    if (rsz < 0) {
                        break;
                    }
                    out.append(buffer, 0, rsz);
                }
            } finally {
                in.close();
            }
        } catch (UnsupportedEncodingException ex) {
            throw new RuntimeException(ex);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
        return out.toString();
    }
}
