package com.simplepl.vfs.packg;

import com.simplepl.Const;
import com.simplepl.vfs.AbstractFile;
import com.simplepl.vfs.LocalFile;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.io.FilenameUtils;

/**
 * @author sad
 */
public class LocalFilePackage extends SPackage {

    private File actualFolder;

    public LocalFilePackage(LocalFilePackage parent, File packagePart) {
        setParent(parent);
        setName(packagePart.getName());
        actualFolder = packagePart;
    }

    @Override
    public List<AbstractFile> getContent() {
        List<AbstractFile> files = new ArrayList<>();
        for (File child : actualFolder.listFiles()) {
            if (child.isFile()) {
                String extension = FilenameUtils.getExtension(child.getName());
                if (extension.toLowerCase().equals(Const.EXT)) {
                    LocalFile localFile = new LocalFile(child);
                    files.add(localFile);
                }
            }
        }

        return files;
    }

    @Override
    public List<SPackage> getChildPackages() {
        List<SPackage> packageList = new ArrayList<>();
        for (File child : actualFolder.listFiles()) {
            if (child.isDirectory()) {
                LocalFilePackage pkg = new LocalFilePackage(this, child);
                packageList.add(pkg);
            }
        }

        return packageList;
    }

    @Override
    public String toString() {
        return actualFolder!=null?actualFolder.getAbsolutePath():"NULL";
    }
    
    
}
