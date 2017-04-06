package com.simplepl.vfs;

import com.simplepl.entity.Context;
import com.simplepl.exception.SourceNotFoundException;
import com.simplepl.vfs.packg.LocalFilePackage;
import com.simplepl.vfs.packg.SPackage;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author sad
 */
public class SrcRoot {

    private List<SPackage> rootPackages = new ArrayList<>();

    public SrcRoot addNewRoot(SPackage pkg) {
        rootPackages.add(pkg);
        return this;
    }

    public SrcRoot addFileSystemRoot(File directory, Context context) {
        if (directory == null) {
            throw new IllegalArgumentException("File system root cannot be null");
        }

        if (!directory.exists()) {
            throw new IllegalArgumentException("File system root [" + directory.getAbsolutePath() + "] does not exists");
        }

        if (!directory.isDirectory()) {
            throw new IllegalArgumentException("File system root [" + directory.getAbsolutePath() + "] is not a directory");
        }

        addNewRoot(new LocalFilePackage(null, directory));
        return this;
    }

    public List<SPackage> getRootPackages() {
        return rootPackages;
    }

    public List<SPackage> getChildPackages(String pkgPath) {
        List<NamedPackageObject> found = findObjectsByPkgPath(pkgPath);
        return (List) found.stream().filter(n -> n instanceof SPackage).collect(Collectors.toList());
    }

    public List<AbstractFile> getAbstractFiles(String pkgPath) {
        List<NamedPackageObject> found = findObjectsByPkgPath(pkgPath);
        return (List) found.stream().filter(n -> n instanceof AbstractFile).collect(Collectors.toList());
    }

    public AbstractFile getAbstractFile(String pkgPath) {
        List<NamedPackageObject> found = findObjectsByPkgPath(pkgPath);
        List<AbstractFile> files = (List) found.stream().filter(n -> n instanceof AbstractFile).collect(Collectors.toList());
        if (files.isEmpty()) {
            throw new SourceNotFoundException("Source not found by this path " + pkgPath);
        }

        return files.get(0);
    }

    public NamedPackageObject findObjectByPkgPath(String pkgPath) {
        List<NamedPackageObject> result = findObjectsByPkgPath(pkgPath);
        if (result.isEmpty()) {
            return null;
        }

        return result.get(0);
    }

    private List<NamedPackageObject> findObjectsByPkgPath(String pkgPath) {
        String[] pkgParts = pkgPath.split("\\.");
        List<NamedPackageObject> packageCandidates = new ArrayList<>(getRootPackages());

        for (int i = 0; i < pkgParts.length; i++) {
            boolean lastPart = (pkgParts.length - 1 == i);
            String pkgPart = pkgParts[i];
            List<NamedPackageObject> filteredPackageObjects = new ArrayList<>();
            for (NamedPackageObject pkgObject : packageCandidates) {
                if (pkgObject instanceof SPackage) {
                    SPackage sPackage = (SPackage) pkgObject;
                    List<NamedPackageObject> filtered = filterPackages((List) sPackage.getChildPackages(), pkgPart);
                    filtered.addAll(filterPackages((List) sPackage.getContent(), pkgPart));
                    if (!filtered.isEmpty()) {
                        filteredPackageObjects.addAll(filtered);
                        if (lastPart) {
                            return filteredPackageObjects;
                        }
                    }
                } else if (pkgObject instanceof AbstractFile) {
                    if (lastPart) {
                        filteredPackageObjects.add(pkgObject);
                        return filteredPackageObjects;
                    }
                } else {
                    throw new IllegalArgumentException("NamedPart should be SPackage or AbstractFile, but it is [" + pkgObject.getClass().getSimpleName() + "]");
                }
            }

            packageCandidates = filteredPackageObjects;
        }

        return Collections.EMPTY_LIST;
    }

    private List<NamedPackageObject> filterPackages(List<NamedPackageObject> pkgObjects, String expectedName) {
        List<NamedPackageObject> result = new ArrayList<>();
        for (NamedPackageObject pkgObject : pkgObjects) {
            if (pkgObject.getName().equals(expectedName)) {
                result.add(pkgObject);
            }
        }

        return result;
    }

}
