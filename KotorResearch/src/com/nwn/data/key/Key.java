package com.nwn.data.key;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Dmitry
 */
public class Key {
    private final KeyResource[] keyResources;
    private final BifShort[] bifs;
    private final Map<Integer, KeyResource> resourcesMap = new HashMap<>();

    public Key(KeyResource[] keyResources, BifShort[] biffs) {
        this.keyResources = keyResources;
        this.bifs = biffs;
        for (KeyResource res : keyResources) {
            resourcesMap.put(res.getResId(), res);
        }
    }

    public KeyResource getResourceByResId(int resId) {
        return resourcesMap.get(resId);
    }

    public BifShort[] getBiffs() {
        return bifs;
    }

    public KeyResource[] getKeyResources() {
        return keyResources;
    }
}
