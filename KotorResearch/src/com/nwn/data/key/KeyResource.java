package com.nwn.data.key;

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

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 59 * hash + this.resId;
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final KeyResource other = (KeyResource) obj;
        if (this.resId != other.resId) {
            return false;
        }
        return true;
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
        return resRef + ":" + resId + ":" + resourceType;
    }

}
