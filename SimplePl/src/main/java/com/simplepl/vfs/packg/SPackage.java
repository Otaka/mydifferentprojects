package com.simplepl.vfs.packg;

import com.simplepl.vfs.AbstractFile;
import com.simplepl.vfs.NamedPackageObject;
import java.util.List;

/**
 * @author sad
 */
public abstract class SPackage implements NamedPackageObject{

    private SPackage parent;
    private String name;
    private String packagePath;

    public String getPackagePath() {
        if (packagePath == null) {
            if (getParent() != null) {
                String parentPath = getParent().getPackagePath();
                if (parentPath.isEmpty()) {
                    packagePath = getName();
                } else {
                    packagePath = parentPath + "." + getName();
                }
            } else {
                packagePath = getName();
            }
        }

        return packagePath;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    public SPackage getParent() {
        return parent;
    }

    protected void setParent(SPackage parent) {
        this.parent = parent;
    }

    public abstract List<AbstractFile> getContent();

    public abstract List<SPackage> getChildPackages();
}
