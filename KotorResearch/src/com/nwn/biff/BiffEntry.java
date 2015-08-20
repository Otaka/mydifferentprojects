package com.nwn.biff;

import com.nwn.key.KeyResource;

/**
 * @author Dmitry
 */
public class BiffEntry {
    private final KeyResource resourceId;
    private final int offset;
    private final int size;

    public BiffEntry(KeyResource resourceId, int offset, int size) {
        this.resourceId = resourceId;
        this.offset = offset;
        this.size = size;
    }

    public KeyResource getResourceId() {
        return resourceId;
    }

    public int getOffset() {
        return offset;
    }

    public int getSize() {
        return size;
    }

    @Override
    public String toString() {
        return resourceId + ":" + offset + ":" + size;
    }

}
