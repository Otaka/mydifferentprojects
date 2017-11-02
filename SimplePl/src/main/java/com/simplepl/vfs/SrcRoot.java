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
    private Context context;

    public SrcRoot(Context context) {
        this.context = context;
    }
    
    private List<SPackage> rootPackages = new ArrayList<>();

    public SrcRoot addNewRoot(SPackage pkg) {
        rootPackages.add(pkg);
        return this;
    }

    public SrcRoot addFileSystemRoot(File directory) {
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

    public List<SPackage> getChildPackages(String modulePath) {
        List<NamedModuleObject> found = findObjectsByModulePath(modulePath);
        return (List) found.stream().filter(n -> n instanceof SPackage).collect(Collectors.toList());
    }

    public List<AbstractFile> getAbstractFiles(String modulePath) {
        List<NamedModuleObject> found = findObjectsByModulePath(modulePath);
        return (List) found.stream().filter(n -> n instanceof AbstractFile).collect(Collectors.toList());
    }

    public AbstractFile getAbstractFile(String modulePath) {
        List<NamedModuleObject> found = findObjectsByModulePath(modulePath);
        List<AbstractFile> files = (List) found.stream().filter(n -> n instanceof AbstractFile).collect(Collectors.toList());
        if (files.isEmpty()) {
            throw new SourceNotFoundException("Source file not found by this path " + modulePath);
        }

        return files.get(0);
    }

    public NamedModuleObject findObjectByPkgPath(String modulePath) {
        List<NamedModuleObject> result = findObjectsByModulePath(modulePath);
        if (result.isEmpty()) {
            return null;
        }

        return result.get(0);
    }

    private List<NamedModuleObject> findObjectsByModulePath(String modulePath) {
        String[] modulePathParts = modulePath.split("\\.");
        List<NamedModuleObject> moduleCandidates = new ArrayList<>(getRootPackages());

        for (int i = 0; i < modulePathParts.length; i++) {
            boolean lastPart = (modulePathParts.length - 1 == i);
            String pkgPart = modulePathParts[i];
            List<NamedModuleObject> filteredModuleObjects = new ArrayList<>();
            for (NamedModuleObject pkgObject : moduleCandidates) {
                if (pkgObject instanceof SPackage) {
                    SPackage sPackage = (SPackage) pkgObject;
                    List<NamedModuleObject> filtered = filterModules((List) sPackage.getChildPackages(), pkgPart);
                    filtered.addAll(filterModules((List) sPackage.getContent(), pkgPart));
                    if (!filtered.isEmpty()) {
                        filteredModuleObjects.addAll(filtered);
                        if (lastPart) {
                            return filteredModuleObjects;
                        }
                    }
                } else if (pkgObject instanceof AbstractFile) {
                    if (lastPart) {
                        filteredModuleObjects.add(pkgObject);
                        return filteredModuleObjects;
                    }
                } else {
                    throw new IllegalArgumentException("NamedPart should be SPackage or AbstractFile, but it is [" + pkgObject.getClass().getSimpleName() + "]");
                }
            }

            moduleCandidates = filteredModuleObjects;
        }

        return Collections.EMPTY_LIST;
    }

    private List<NamedModuleObject> filterModules(List<NamedModuleObject> pkgObjects, String expectedName) {
        List<NamedModuleObject> result = new ArrayList<>();
        for (NamedModuleObject pkgObject : pkgObjects) {
            if (pkgObject.getName().equals(expectedName)) {
                result.add(pkgObject);
            }
        }

        return result;
    }

}
