package com.sqlprocessor.utils;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class ClassFinder {

    private final char PKG_SEPARATOR = '.';
    private final char DIR_SEPARATOR = '/';
    private final String BAD_PACKAGE_ERROR = "Unable to get resources from path '%s'. Are you sure the package '%s' exists?";
    private String classFileSuffix = ".class";

    public ClassFinder setClassFileSuffix(String classFileSuffix) {
        this.classFileSuffix = classFileSuffix;
        return this;
    }

    public String getClassFileSuffix() {
        return classFileSuffix;
    }

    public List<String> find(String scannedPackage) {
        String scannedPath = scannedPackage.replace(PKG_SEPARATOR, DIR_SEPARATOR);
        URL scannedUrl = Thread.currentThread().getContextClassLoader().getResource(scannedPath);
        if (scannedUrl == null) {
            throw new IllegalArgumentException(String.format(BAD_PACKAGE_ERROR, scannedPath, scannedPackage));
        }

        File scannedDir = new File(scannedUrl.getFile());
        List<String> classes = new ArrayList<String>();
        for (File file : scannedDir.listFiles()) {
            classes.addAll(find(file, scannedPackage));
        }

        return classes;
    }

    private List<String> find(File file, String scannedPackage) {
        List<String> classes = new ArrayList<>();
        String resource = scannedPackage + PKG_SEPARATOR + file.getName();
        if (file.isDirectory()) {
            for (File child : file.listFiles()) {
                classes.addAll(find(child, resource));
            }
        } else if (resource.endsWith(classFileSuffix)) {
            int endIndex = resource.length() - classFileSuffix.length();
            String className = resource.substring(0, endIndex);
            classes.add(className);
        }

        return classes;
    }
}
