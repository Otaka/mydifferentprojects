package com.nwn.key;

/**
 * @author Dmitry
 */
public class KeyResource {
    private final String resRef;
    private final ResourceType resourceType;
    private final int resId;

    public KeyResource(String resRef, ResourceType resourceType, int resId) {
        this.resRef = resRef;
        this.resourceType = resourceType;
        this.resId = resId;
    }

    public String getResRef() {
        return resRef;
    }

    public ResourceType getResourceType() {
        return resourceType;
    }

    public int getResId() {
        return resId;
    }

    @Override
    public String toString() {
        return resId + ":" + resRef + ":" + resourceType;
    }

}
