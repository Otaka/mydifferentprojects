package com.nwn.data.biff;

import java.io.File;

/**
 * @author Dmitry
 */
public class Biff {
    private final BiffEntry[] entries;
    private final File file;

    public Biff(BiffEntry[] entries, File file) {
        this.entries = entries;
        this.file = file;
    }

    public BiffEntry[] getEntries() {
        return entries;
    }

    public File getFile() {
        return file;
    }

    @Override
    public String toString() {
        return file.getName() + ". Entries count=" + entries.length;
    }
}
