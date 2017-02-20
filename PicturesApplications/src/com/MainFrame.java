package com;

import com.drawables.*;
import com.gooddies.swing.Toast;
import com.jsonparser.*;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.*;
import java.util.*;
import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 * @author Dmitry
 */
public class MainFrame extends JFrame {
    private Map<Integer, Drawable> drawables = new HashMap<>();
    private final List<Drawable> currentDrawables = new ArrayList<>();
    private final JPanel drawPane = new JPanel() {

        @Override
        public void paint(Graphics g) {
            super.paint(g);
            for (Drawable dr : currentDrawables) {
                dr.paint(g, getWidth(), getHeight());
            }
        }
    };

    public MainFrame() {
        init();
    }

    private void init() {
        setTitle("Кнопки");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        drawPane.setPreferredSize(new Dimension(500, 500));
        getContentPane().add(drawPane);
        setFocusTraversalKeysEnabled(false);
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                onKeyPressed(e, true);
            }

            @Override
            public void keyReleased(KeyEvent e) {
                onKeyPressed(e, false);
            }
        });

        reloadSettings();
    }

    private void onKeyPressed(KeyEvent e, boolean add) {
        System.out.println("Key " + KeyEvent.getKeyText(e.getKeyCode()).toLowerCase());
        int keyCode = e.getKeyCode();
        Drawable drawable = drawables.get(keyCode);
        if (drawable != null) {
            if (add) {
                if (!currentDrawables.contains(drawable)) {
                    currentDrawables.add(drawable);
                    repaint();
                }
            } else {
                if (currentDrawables.contains(drawable)) {
                    currentDrawables.remove(drawable);
                    repaint();
                }
            }
        }
    }

    private void reloadSettings() {
        try {
            loadSettings(new File("./settings.json"));
        } catch (Exception ex) {
            Toast.makeText(rootPane, ex.getMessage(), 5000, Toast.Style.ERROR).display();
        }
    }

    private Map<String, Integer> loadKeyMap() {
        Map<String, Integer> map = new HashMap<>();
        for (int keyCode = 0; keyCode < 550; keyCode++) {
            String description = KeyEvent.getKeyText(keyCode);
            if (!description.contains("Unknown keyCode")) {
                map.put(description.toLowerCase(), keyCode);
            }
        }
        return map;
    }

    private void loadSettings(File jsonConfigFile) throws FileNotFoundException {
        Map<String, Integer> keyCodesMap = loadKeyMap();
        JsonParser parser = new JsonParser();
        JsonObject element = parser.parse(new FileReader(jsonConfigFile)).getAsObject();
        if (!element.has("keys")) {
            throw new RuntimeException("Config file should have top level member 'keys'");
        }

        JsonObject keys = element.getElementByName("keys").getAsObject();
        drawables = loadDrawables(keys, keyCodesMap);
    }

    private int parseKeyTextToKeyCode(String keyText, Map<String, Integer> keyCodesMap) {
        keyText = keyText.toLowerCase();
        if (!keyCodesMap.containsKey(keyText)) {
            throw new IllegalArgumentException("Unknown key '" + keyText + "'");
        }
        return keyCodesMap.get(keyText);
    }

    private Map<Integer, Drawable> loadDrawables(JsonObject keys, Map<String, Integer> keyCodesMap) {
        Map<Integer, Drawable> result = new HashMap<>();
        for (FieldValuePair entry : keys.getElements()) {
            String key = entry.getName();
            int keyCode = parseKeyTextToKeyCode(key, keyCodesMap);
            JsonElement elementData = entry.getValue();
            if (!elementData.isObject()) {
                throw new IllegalArgumentException("Key data of '" + key + "'should be jsonObject");
            }

            Drawable drawable = parseDrawableConfig(key, elementData.getAsObject());
            result.put(keyCode, drawable);
        }
        return result;
    }

    private Drawable parseDrawableConfig(String key, JsonObject element) {
        List<Drawable> tDrawables = new ArrayList<>();

        for (FieldValuePair entry : element.getElements()) {
            String drawableType = entry.getName();
            JsonObject drawableConfigJson = entry.getValue().getAsObject();
            switch (drawableType) {
                case "rect":
                    tDrawables.add(new RectDrawable().parse(drawableConfigJson));
                    break;
                case "image":
                    tDrawables.add(new ImageDrawable().parse(drawableConfigJson));
                    break;

                default:
                    throw new IllegalArgumentException("Cannot parse drawable type '" + drawableType + "' in key '" + key + "'");
            }
        }

        if (tDrawables.isEmpty()) {
            System.out.println("Key '" + key + "' contains no drawables");
        }
        if (tDrawables.size() == 1) {
            return tDrawables.get(0);
        }
        return new CollectionOfDrawables(tDrawables);
    }
}
