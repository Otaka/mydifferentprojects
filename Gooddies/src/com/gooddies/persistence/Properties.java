package com.gooddies.persistence;

import com.google.gson.Gson;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.PrintStream;
import java.util.HashMap;

/**
 * @author Dmitry
 */
@SuppressWarnings("UnusedDeclaration")
public class Properties {
    private static Properties instance;

    public static Properties get() {
        if (instance == null) {
            instance = new Properties();
        }
        return instance;
    }
    private HashMap<String, Object> values = new HashMap<String, Object>(1000);

    private Properties() {
        readProperties();
    }

    public Properties putInt(String key, Integer value) {
        values.put(key, value);
        return this;
    }

    public Properties putString(String key, String value) {
        values.put(key, value);
        return this;
    }

    public Properties putFloat(String key, Float value) {
        values.put(key, value);
        return this;
    }

    public Properties putDouble(String key, Double value) {
        values.put(key, value);
        return this;
    }

    public Properties putBoolean(String key, Boolean value) {
        values.put(key, value);
        return this;
    }

    public boolean hasKey(String key) {
        return values.containsKey(key);
    }

    public void removeKey(String key) {
        values.remove(key);
    }

    public Integer getInt(String key) {
        Object o = values.get(key);
        if (o instanceof Double) {
            return (int) (double)(Double) o;
        }
        return (Integer) o;
    }

    public Integer getInt(String key, int defaultInteger) {
        Object value = values.get(key);
        if (value == null) {
            return defaultInteger;
        }
        if (value instanceof Double) {
            return (int) (double) (Double)value;
        }
        return (Integer) value;
    }

    public String getString(String key) {
        return (String) values.get(key);
    }

    public String getString(String key, String defaultString) {
        String value = (String) values.get(key);
        if (value == null) {
            value = defaultString;
        }
        return value;
    }

    public Boolean getBoolean(String key) {
        return (Boolean) values.get(key);
    }

    public Boolean getBoolean(String key, Boolean defaultBoolean) {
        Boolean value = (Boolean) values.get(key);
        if (value == null) {
            value = defaultBoolean;
        }
        return value;
    }

    public Float getFloat(String key) {
        return (Float) values.get(key);
    }

    public Float getFloat(String key, Float defaultFloat) {
        Float value = (Float) values.get(key);
        if (value == null) {
            value = defaultFloat;
        }
        return value;
    }

    public Double getDouble(String key) {
        return (Double) values.get(key);
    }

    public Double getDouble(String key, Double defaultDouble) {
        Double value = (Double) values.get(key);
        if (value == null) {
            value = defaultDouble;
        }
        return value;
    }

    @SuppressWarnings("unchecked")
    final public void readProperties() {
        String filePath = getFilePath();
        if (isFileExist(filePath)) {
            try {
                values = new Gson().fromJson(new FileReader(getFilePath()), values.getClass());
            } catch (FileNotFoundException ex) {
                ex.printStackTrace();
            }
        }
    }

    public void saveProperties() {
        String string = new Gson().toJson(values);
        PrintStream str = null;
        try {
            FileOutputStream file = new FileOutputStream(new File(getFilePath()), false);
            str = new PrintStream(file);
            str.print(string);
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } finally {
            if (str != null) {
                str.close();
            }
        }
    }

    private boolean isFileExist(String path) {
        File file = new File(path);
        return file.exists();
    }

    protected String getFilePath() {
        String path = System.getProperty("user.dir");
        return path + "\\properties.pr";
    }
}
