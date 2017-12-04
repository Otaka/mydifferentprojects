package com.gooddies.reflection;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.TreeSet;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * @author sad
 */
@SuppressWarnings("unchecked")
public class ReflectionUtils {

    public static List<Class> listClassesInPackage(String packageName) {
        try {
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            assert classLoader != null;
            String path = packageName.replace('.', '/');
            Enumeration resources = classLoader.getResources(path);
            List<String> dirs = new ArrayList();
            while (resources.hasMoreElements()) {
                URL resource = (URL) resources.nextElement();
                dirs.add(URLDecoder.decode(resource.getFile(), "UTF-8"));
            }

            TreeSet<String> classes = new TreeSet();
            for (String directory : dirs) {
                classes.addAll(findClasses(directory, packageName));
            }

            ArrayList<Class> classList = new ArrayList<Class>();
            for (String clazz : classes) {
                try {
                    classList.add(Class.forName(clazz));
                } catch (ClassNotFoundException | NoClassDefFoundError ex) {
                    ex.printStackTrace();
                }
            }

            return classList;
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException(ex);
        }
    }

    private static TreeSet findClasses(String path, String packageName) throws MalformedURLException, IOException {
        TreeSet classes = new TreeSet();
        if (path.startsWith("file:") && path.contains("!")) {
            String[] split = path.split("!");
            URL jar = new URL(split[0]);
            ZipInputStream zip = new ZipInputStream(jar.openStream());
            ZipEntry entry;
            while ((entry = zip.getNextEntry()) != null) {
                if (entry.getName().endsWith(".class")) {
                    String className = entry.getName().replaceAll("[$].*", "").replaceAll("[.]class", "").replace('/', '.');
                    if (className.startsWith(packageName)) {
                        classes.add(className);
                    }
                }
            }
        }

        File dir = new File(path);
        if (!dir.exists()) {
            return classes;
        }

        File[] files = dir.listFiles();
        for (File file : files) {
            if (file.isDirectory()) {
                assert !file.getName().contains(".");
                classes.addAll(findClasses(file.getAbsolutePath(), packageName + "." + file.getName()));
            } else if (file.getName().endsWith(".class")) {
                String className = packageName + '.' + file.getName().substring(0, file.getName().length() - 6);

                classes.add(className);
            }
        }

        return classes;
    }

    public static void iterateMethods(Class clazz, MethodResult mr) {
        while (clazz != null) {
            Method[] methods = clazz.getDeclaredMethods();
            for (Method m : methods) {
                mr.method(m);
            }
            clazz = clazz.getSuperclass();
        }
    }

    public static void iterateFields(Class clazz, FieldResult fr) {
        while (clazz != null) {
            Field[] fields = clazz.getDeclaredFields();
            for (Field f : fields) {
                fr.field(f);
            }
            clazz = clazz.getSuperclass();
        }
    }

    public interface MethodResult {

        public void method(Method m);
    }

    public interface FieldResult {

        public void field(Field m);
    }

    public static boolean isSubclass(Class parent, Class child) {
        try {
            child.asSubclass(parent);
            return true;
        } catch (ClassCastException ex) {
        }
        return false;
    }
}
